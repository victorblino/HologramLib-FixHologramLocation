package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.maximde.hologramlib.utils.PlayerUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class LeaderboardHologram {

    private final TextHologram textHologram;
    private ItemHologram firstPlaceHead;

    @Data
    @Builder
    @Accessors(fluent = true)
    public static class LeaderboardOptions {
        @Builder.Default
        private String title = "Leaderboard";

        @Builder.Default
        private String[] placeFormats = new String[] {
                "<color:#fdcc00><bold>1. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>",
                "<color:#dcdcdc><bold>2. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>",
                "<color:#e65f2f><bold>3. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>"
        };

        @Builder.Default
        private String defaultPlaceFormat = "<color:#ffb486><bold>{place}. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>";

        @Builder.Default
        private String titleFormat = "<gradient:#ff6000:#ffa42a>▛▀▀▀▀ {title} ▀▀▀▀▜</gradient>";

        @Builder.Default
        private String footerFormat = "<color:#ff6000>▙▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▟</color>";

        @Builder.Default
        private String suffix = "";

        @Builder.Default
        private float scale = 1.0f;

        @Builder.Default
        private boolean topPlayerHead = true;

        @Builder.Default
        private boolean showEmptyPlaces = false;

        @Builder.Default
        private int maxDisplayEntries = 10;
    }

    @Setter
    @Accessors(chain = true)
    private LeaderboardOptions options;

    public LeaderboardHologram(LeaderboardOptions options) {
        this.options = options;
        this.textHologram = new TextHologram(
                "leaderboard_" + UUID.randomUUID()
        );
    }

    public void updateLeaderboard(Map<Integer, String> leaderboardData, LeaderboardOptions options) {

        Map<Integer, String> sortedData = new LinkedHashMap<>(leaderboardData);
        StringBuilder leaderboardText = new StringBuilder();

        leaderboardText.append(
                options.titleFormat()
                        .replace("{title}", options.title())
        ).append("\n\n");

        for (Map.Entry<Integer, String> entry : sortedData.entrySet()) {
            int place = entry.getKey();
            if (place > options.maxDisplayEntries()) break;
            if (!options.showEmptyPlaces() && entry.getValue().isEmpty()) continue;

            String[] parts = entry.getValue().split(":");
            String name = parts.length > 0 ? parts[0] : "Unknown";
            String score = parts.length > 1 ? parts[1] : "N/A";

            String placeFormat = place <= 3 && place <= options.placeFormats().length
                    ? options.placeFormats()[place - 1]
                    : options.defaultPlaceFormat();

            leaderboardText.append(
                    placeFormat
                            .replace("{place}", String.valueOf(place))
                            .replace("{name}", name)
                            .replace("{score}", score)
                            .replace("{suffix}", options.suffix())
            ).append("\n");
        }

        leaderboardText.append("\n").append(options.footerFormat());

        this.textHologram
                .setScale(options.scale(), options.scale(), options.scale())
                .setBackgroundColor(520093695)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(Display.Billboard.VERTICAL);

        this.textHologram.update();

        UUID topPlayerUUID = Optional.ofNullable(sortedData.get(1))
                .map(data -> data.split(":")[0])
                .flatMap(PlayerUtils::getUUID)
                .orElse(UUID.randomUUID());

        if (options.topPlayerHead()) {
            if (firstPlaceHead == null) createFirstPlaceHead(topPlayerUUID);
            else updateFirstPlaceHead(topPlayerUUID);
            leaderboardText.insert(0, "\n\n\n");
        } else {
            removeFirstPlaceHead();
        }
        textHologram.setMiniMessageText(leaderboardText.toString());

        textHologram.update();
    }

    private void createFirstPlaceHead(UUID uuid) {
        try {
            firstPlaceHead = new ItemHologram(textHologram.getId() + "_head");
            firstPlaceHead.setScale(2 * options.scale, 2 * options.scale, 0.01f * options.scale);
            firstPlaceHead.setGlowing(true);
            firstPlaceHead.setGlowColor(java.awt.Color.YELLOW.getRGB());
            firstPlaceHead.setBillboard(Display.Billboard.VERTICAL);


            ItemStack headItem = new ItemStack.Builder()
                    .type(ItemTypes.PLAYER_HEAD)
                    .nbt("SkullOwner", new NBTString("MaximDe"))
                    .build();

            firstPlaceHead.setItem(headItem);

            Location headLocation = textHologram.getLocation().clone().add(0, 1.2f + (options.scale / 2.8f), 0);
            firstPlaceHead.getInternalAccess().setLocation(headLocation);
            firstPlaceHead.update();
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.WARNING, exception.getMessage());
        }
    }

    private void updateFirstPlaceHead(UUID uuid) {
        try {
            ItemStack headItem = new ItemStack.Builder()
                    .type(ItemTypes.PLAYER_HEAD)
                    .build();
            firstPlaceHead.setItem(headItem);
            firstPlaceHead.update();
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.WARNING, exception.getMessage());
        }
    }

    private void removeFirstPlaceHead() {
        if (firstPlaceHead != null) {
            firstPlaceHead.kill();
            firstPlaceHead = null;
        }
    }

    public void remove() {
        removeFirstPlaceHead();
        textHologram.kill();
    }

    public Location getLocation() {
        return textHologram.getLocation();
    }

}