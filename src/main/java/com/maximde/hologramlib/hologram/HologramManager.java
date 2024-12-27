package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.maximde.hologramlib.utils.BukkitTasks;
import com.maximde.hologramlib.utils.TaskHandle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class HologramManager {
    private final Map<TextHologram, TaskHandle> hologramAnimations = new ConcurrentHashMap<>();
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
        updateLeaderboard(leaderboardHologram, leaderboardData, options);
        spawn(leaderboardHologram.getTextHologram(), location);
        spawn(leaderboardHologram.getFirstPlaceHead(), location);
        return leaderboardHologram;
    }

    public void updateLeaderboard(LeaderboardHologram leaderboardHologram, Map<Integer, String> leaderboardData, LeaderboardHologram.LeaderboardOptions options) {
        leaderboardHologram.updateLeaderboard(leaderboardData, options);
    }

    public <H extends Hologram<H>> H spawn(H hologram, Location location) {
        hologram.getInternalAccess().setLocation(location);
        hologram.getInternalAccess().setEntityId(ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE));
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                hologram.getEntityID(), Optional.of(UUID.randomUUID()), hologram.getEntityType(),
                new Vector3d(location.getX(), location.getY(), location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );

        BukkitTasks.runTask(() -> {
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

    public boolean remove(LeaderboardHologram leaderboardHologram) {
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
        Optional.ofNullable(hologramAnimations.remove(hologram)).ifPresent(TaskHandle::cancel);
    }

    private TaskHandle animateHologram(TextHologram hologram, TextAnimation textAnimation) {
        return BukkitTasks.runTaskTimerAsync(() -> {
            if (textAnimation.getTextFrames().isEmpty()) return;
            hologram.setMiniMessageText(textAnimation.getTextFrames().get(0));
            hologram.update();
            Collections.rotate(textAnimation.getTextFrames(), -1);
        }, textAnimation.getDelay(), textAnimation.getSpeed());
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

    public Hologram<?> copyHologram(Hologram<?> source, String id) {
        return this.spawn(source.copy(id), source.getLocation());
    }

    public Hologram<?> copyHologram(Hologram<?> source) {
        return this.spawn(source.copy(), source.getLocation());
    }
}
