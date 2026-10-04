package com.mykhaylo.betterlookingeffects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EffectConfig {
    public static class Entry {
        public boolean hidden = false;
        public boolean forced = false;   // show even if you don't have the effect
        public int level = 0;            // 0 = real level
        public int durationSeconds = 0;  // 0 = real duration
        public boolean infinite = false; // show as infinite (∞)
    }

    public boolean hideAll = false;
    public Map<String, Entry> effects = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("betterlookingeffects.json");
    private static EffectConfig instance;

    public static EffectConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) instance = GSON.fromJson(Files.readString(FILE), EffectConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (instance == null) instance = new EffectConfig();
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Entry entry(String id) {
        return effects.computeIfAbsent(id, k -> new Entry());
    }
}
