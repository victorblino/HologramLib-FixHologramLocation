package com.maximde.hologramlib.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerUtils {

    public static PlayerProfile getPlayerProfile(UUID uuid) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(PlayerUtils.getPlayerSkinUrl(uuid)));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
        profile.setTextures(textures);
        return profile;
    }

    public static Optional<UUID> getUUID(String playerName) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return Optional.empty();
            }

            String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                    .lines()
                    .findFirst()
                    .orElse("");

            if (response.isEmpty()) {
                return Optional.empty();
            }

            String uuid = response.split("\"id\":\"")[1].split("\"")[0];
            return Optional.of(UUID.fromString(uuid.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")));

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String getPlayerSkinUrl(UUID uuid) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                uuid.toString().replace("-", "")).openConnection().getInputStream())) {

            JsonObject profile = JsonParser.parseReader(reader).getAsJsonObject();
            if (!profile.has("properties")) return null;

            String encodedTextures = profile.getAsJsonArray("properties")
                    .get(0).getAsJsonObject()
                    .get("value").getAsString();

            JsonObject textures = JsonParser.parseString(new String(Base64.getDecoder().decode(encodedTextures)))
                    .getAsJsonObject()
                    .getAsJsonObject("textures");

            return textures.has("SKIN") ? textures.getAsJsonObject("SKIN").get("url").getAsString() : null;
        }
    }
}
