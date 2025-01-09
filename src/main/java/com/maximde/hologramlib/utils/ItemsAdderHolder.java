package com.maximde.hologramlib.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class ItemsAdderHolder implements ReplaceText {

    public ItemsAdderHolder() throws ClassNotFoundException {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            throw new ClassNotFoundException();
        }
    }

    @Override
    public String replace(String s) {
        return FontImageWrapper.replaceFontImages(s);
    }
}
