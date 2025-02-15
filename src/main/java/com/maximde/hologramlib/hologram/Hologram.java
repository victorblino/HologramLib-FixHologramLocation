package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.utils.BukkitTasks;
import com.maximde.hologramlib.utils.TaskHandle;
import com.maximde.hologramlib.utils.Vector3F;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import me.tofaa.entitylib.ve.ViewerRule;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Hologram<T extends Hologram<T>> {

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
    @Getter
    protected Location location;

    @Getter
    protected boolean dead = true;

    @Getter @Accessors(chain = true)
    protected long updateTaskPeriod = 20L * 3;

    @Getter @Accessors(chain = true)
    protected double nearbyEntityScanningDistance = 30.0;

    @Getter @Accessors(chain = true)
    protected Display.Billboard billboard = Display.Billboard.CENTER;

    @Getter @Accessors(chain = true)
    protected int teleportDuration = 10;

    @Getter @Accessors(chain = true)
    protected int interpolationDurationTransformation = 10;

    @Getter @Accessors(chain = true)
    protected double viewRange = 1.0;

    @Getter
    protected final String id;

    @Getter @Accessors(chain = true)
    protected int entityID;

    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    /**
     * The render mode determines which players can see the hologram:
     * - NEARBY: Only players within viewing distance
     * - ALL: All players on the server
     * - VIEWER_LIST: Only specific players added as viewers
     * - NONE: Hologram is not visible to any players
     */
    @Getter
    protected final RenderMode renderMode;

    @Getter
    protected final EntityType entityType;

    @Getter
    protected TaskHandle task;

    @Getter
    /**
     * Do not use this if you don't know what you are doing!
     * this interface for accessing specific setters is only for internal methods.
     */
    private Internal internalAccess;

    protected WrapperEntity entity;

    public interface Internal {
        Hologram spawn(Location location);
        void kill();
    }

    protected Hologram(String id, EntityType entityType) {
        this(id, RenderMode.ALL, entityType);
    }


    protected Hologram(String id, RenderMode renderMode, EntityType entityType) {
        this.entityType = entityType;
        validateId(id);
        this.entity = new WrapperEntity(entityType);
        this.id = id.toLowerCase();
        this.entityID = entity.getEntityId();
        this.renderMode = renderMode;
        this.internalAccess = new InternalSetters();
        startRunnable();
    }

    private void startRunnable() {
        if (task != null) return;
        task = BukkitTasks.runTaskTimer(this::updateAffectedPlayers, 20L, updateTaskPeriod);
    }


    private class InternalSetters implements Internal {

        @Override
        public Hologram spawn(Location location) {
            Hologram.this.spawn(location);
            return Hologram.this;
        }

        @Override
        public void kill() {
            Hologram.this.kill();
        }
    }


    /**
     * Updates the set properties for the entity (shows them to the players).
     * Should be called after making any changes to the hologram object.
     */
    public T update() {
        updateAffectedPlayers();
        applyMeta();
        return self();
    }

    protected void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    protected com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

    /**
     * THIS METHOD WILL BE MADE 'private' SOON!
     * Use HologramManager#remove(Hologram) instead!
     */
    @Deprecated
    public void kill() {
        this.entity.remove();
        this.task.cancel();
        this.dead = true;
    }

    public T teleport(Location newLocation) {
        this.location = newLocation;
        this.entity.teleport(SpigotConversionUtil.fromBukkitLocation(newLocation));
        return self();
    }

    protected abstract EntityMeta applyMeta();

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }


    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    /**
     * Updates which players should be able to see this hologram based on the render mode.
     * For ALL mode, adds all online players.
     * For VIEWER_LIST mode, only uses manually added viewers.
     * Removes viewers who are too far away or in different worlds.
     */
    private void updateAffectedPlayers() {
        if(this.dead || renderMode == RenderMode.VIEWER_LIST) return;

        if(this.location == null) {
            Bukkit.getLogger().log(Level.WARNING, "Tried to update hologram with ID " + this.id + " entity type " + this.entityType.getName().getKey() + ". But the location is not set!");
            return;
        }

        if (this.renderMode == RenderMode.ALL || this.renderMode == RenderMode.NEARBY) {
            List<Player> viewersToKeep = this.location.getWorld().getPlayers().stream()
                    .filter(Objects::nonNull)
                    .filter(player -> player.isOnline()
                            && player.getLocation().getWorld().equals(this.location.getWorld())
                            && player.getLocation().distanceSquared(this.location) <= 62500)
                    .toList();

            Set<UUID> viewersToRemove = new HashSet<>(this.entity.getViewers());
            viewersToKeep.forEach(player -> viewersToRemove.remove(player.getUniqueId()));

            viewersToRemove.forEach(this.entity::removeViewer);
            this.addAllViewers(viewersToKeep);
        }

    }

    protected void sendPacket(PacketWrapper<?> packet, List<Player> players) {
        if (this.renderMode == RenderMode.NONE) return;
        players.forEach(player -> {
            HologramLib.getPlayerManager().sendPacket(player, packet);
        });
    }

    private void spawn(Location location) {
        this.location = location;
        this.entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        this.dead = false;
    }

    /**
     * Attaches this hologram to another entity, making it ride the target entity.
     *
     * @param entityID The entity ID to attach the hologram to
     * @param persistent If the hologram should be re-attached automatically or not TODO
     */
    public void attach(int entityID, boolean persistent) {
        int[] hologramToArray = { this.entityID };
        WrapperPlayServerSetPassengers attachPacket = new WrapperPlayServerSetPassengers(entityID, hologramToArray);
        BukkitTasks.runTask(() -> {
            this.entity.sendPacketsToViewers(attachPacket);
        });
    }

    /**
     * Attaches entities to this hologram.
     *
     * @param entityIDs The passengers
     */
    public void addPassenger(int... entityIDs) {
        this.entity.addPassengers(entityIDs);
    }

    public Set<Integer> getPassengers() {
        return this.entity.getPassengers();
    }


    /**
     * Period in ticks between updates of the hologram's viewer list.
     * Lower values mean more frequent updates but higher server load.
     * Default is 60 ticks (3 seconds).
     */
    public T setUpdateTaskPeriod(long updateTaskPeriod) {
        this.updateTaskPeriod = updateTaskPeriod;
        return self();
    }

    public T setNearbyEntityScanningDistance(double nearbyEntityScanningDistance) {
        this.nearbyEntityScanningDistance = nearbyEntityScanningDistance;
        return self();
    }

    public T setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return self();
    }

    @Deprecated(forRemoval = true)
    public int getInterpolationDurationRotation() {
        return this.teleportDuration;
    }

    @Deprecated(forRemoval = true)
    public T setInterpolationDurationRotation(int teleportDuration) {
        this.teleportDuration = teleportDuration;
        return self();
    }

    public T setTeleportDuration(int teleportDuration) {
        this.teleportDuration = teleportDuration;
        return self();
    }

    public T setInterpolationDurationTransformation(int interpolationDurationTransformation) {
        this.interpolationDurationTransformation = interpolationDurationTransformation;
        return self();
    }

    public T setViewRange(double viewRange) {
        this.viewRange = viewRange;
        return self();
    }

    public T setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return self();
    }

    public T setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return self();
    }

    public T setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return self();
    }

    public T setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return self();
    }

    public T addViewer(Player player) {
        this.entity.addViewer(player.getUniqueId());
        return self();
    }

    public T removeViewer(Player player) {
        this.entity.removeViewer(player.getUniqueId());
        return self();
    }

    public T addAllViewers(List<Player> viewerList) {
        for (Player player : viewerList) {
            this.entity.addViewer(player.getUniqueId());
        }
        return self();
    }

    public T removeAllViewers() {
        this.entity.getViewers().forEach(this.entity::removeViewer);
        return self();
    }

    public T setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return self();
    }

    public T setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return self();
    }

    protected abstract T copy();
    protected abstract T copy(String id);
}
