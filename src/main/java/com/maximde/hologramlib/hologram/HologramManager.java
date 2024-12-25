package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.maximde.hologramlib.HologramLib;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class HologramManager {
    private final Map<TextHologram, BukkitRunnable> hologramAnimations = new ConcurrentHashMap<>();
    private final Map<String, Hologram<?>> hologramsMap = new ConcurrentHashMap<>();

    public List<Hologram<?>> getHolograms() {
        return new ArrayList<>(hologramsMap.values());
    }

    public Optional<Hologram<?>> getHologram(String id) {
        return Optional.ofNullable(hologramsMap.get(id));
    }

    public LeaderboardHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData) {
        return generateLeaderboard(location, leaderboardData, LeaderboardHologram.LeaderboardOptions.builder().build());
    }

    public LeaderboardHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData, LeaderboardHologram.LeaderboardOptions options) {
        LeaderboardHologram leaderboardHologram = new LeaderboardHologram(options);
        spawn(leaderboardHologram.getTextHologram(), location);
        spawn(leaderboardHologram.getFirstPlaceHead(), location.clone().add(0, 3.2f + (options.scale() / 2.8f), 0));
        updateLeaderboard(leaderboardHologram, leaderboardData, options);
        return leaderboardHologram;
    }

    public void updateLeaderboard(LeaderboardHologram leaderboardHologram, Map<Integer, String> leaderboardData, LeaderboardHologram.LeaderboardOptions options) {
        leaderboardHologram.updateLeaderboard(leaderboardData, options);
    }

    public <H extends Hologram<H>> H spawn(H hologram, Location location) {
        hologram.getInternalAccess().setLocation(location);
        hologram.getInternalAccess().setEntityId(ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE));
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                hologram.getEntityID(), Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
        Bukkit.getServer().getScheduler().runTask(HologramLib.getInstance(), () -> {
            hologram.getInternalAccess().updateAffectedPlayers();
            hologram.getInternalAccess().sendPacket(packet);
            hologram.getInternalAccess().setDead(false);
        });
        hologram.update();
        register(hologram);
        return hologram;
    }

    public void attach(Hologram<?> hologram, int entityID) {
        this.attach(hologram, entityID, true);
    }

    public void attach(Hologram<?> hologram, int entityID, boolean persistent) {
        hologram.attach(entityID, persistent);
    }

    public <H extends Hologram<H>> boolean register(H hologram) {
        if (hologram == null) {
            return false;
        }
        if (hologramsMap.containsKey(hologram.getId())) {
            Bukkit.getLogger().severe("Error: Hologram with ID " + hologram.getId() + " is already registered.");
            return false;
        }
        hologramsMap.put(hologram.getId(), hologram);
        return true;
    }


    public boolean removeLeaderboard(LeaderboardHologram leaderboardHologram) {
        return remove(leaderboardHologram.getTextHologram()) && remove(leaderboardHologram.getFirstPlaceHead());
    }

    public boolean remove(Hologram<?> hologram) {
        return hologram != null && remove(hologram.getId());
    }

    public boolean remove(String id) {
        Hologram<?> hologram = hologramsMap.remove(id);
        if (hologram != null) {
            hologram.kill();
            return true;
        }
        return false;
    }

    public void removeAll() {
        hologramsMap.values().forEach(Hologram::kill);
        hologramsMap.clear();
    }

    public void applyAnimation(TextHologram hologram, TextAnimation textAnimation) {
        cancelAnimation(hologram);
        hologramAnimations.put(hologram, animateHologram(hologram, textAnimation));
    }

    public void cancelAnimation(TextHologram hologram) {
        Optional.ofNullable(hologramAnimations.remove(hologram)).ifPresent(BukkitRunnable::cancel);
    }

    private BukkitRunnable animateHologram(TextHologram hologram, TextAnimation textAnimation) {
        final BukkitRunnable animation = new BukkitRunnable() {
            int currentFrame = 0;
            public void run() {
                if (textAnimation.getTextFrames().isEmpty()) return;
                hologram.setMiniMessageText(textAnimation.getTextFrames().get(currentFrame));
                hologram.update();
                currentFrame = (currentFrame + 1) % textAnimation.getTextFrames().size();
            }
        };

        animation.runTaskTimerAsynchronously(HologramLib.getInstance(), textAnimation.getDelay(), textAnimation.getSpeed());
        return animation;
    }

    public void ifHologramExists(String id, Consumer<Hologram<?>> action) {
        Optional.ofNullable(hologramsMap.get(id)).ifPresent(action);
    }

    public boolean updateHologramIfExists(String id, Consumer<Hologram<?>> updateAction) {
        Hologram<?> hologram = hologramsMap.get(id);
        if (hologram != null) {
            updateAction.accept(hologram);
            return true;
        }
        return false;
    }

    /**
     * Creates a copy of an existing hologram at a new location
     * @param source The hologram to copy from
     * @param id The ID for the new hologram
     * @return The newly created hologram copy
     */
    public TextHologram copyHologram(TextHologram source, String id) {
        return this.spawn(source.copy(id), source.getLocation());
    }

    /**
     * Creates a copy of an existing hologram at a new location
     * @param source The hologram to copy from
     * @return The newly created hologram copy
     */
    public TextHologram copyHologram(TextHologram source) {
        return this.spawn(source.copy(), source.getLocation());
    }
}