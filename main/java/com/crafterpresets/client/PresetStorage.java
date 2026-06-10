package com.crafterpresets.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PresetStorage {

    public static class Preset {
        public String name;
        public boolean[] disabledSlots;

        public Preset(String name, boolean[] disabledSlots) {
            this.name = name;
            this.disabledSlots = disabledSlots.clone();
        }

        public int countDisabled() {
            int count = 0;
            for (boolean b : disabledSlots) if (b) count++;
            return count;
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("crafter-presets.json");

    private static List<Preset> presets = new ArrayList<>();

    public static List<Preset> getPresets() {
        return presets;
    }

    public static void load() {
        if (!Files.exists(SAVE_PATH)) {
            presets = new ArrayList<>();
            return;
        }
        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            Type type = new TypeToken<List<Preset>>() {}.getType();
            List<Preset> loaded = GSON.fromJson(reader, type);
            presets = loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            presets = new ArrayList<>();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(SAVE_PATH)) {
            GSON.toJson(presets, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPreset(String name, boolean[] disabledSlots) {
        presets.add(new Preset(name, disabledSlots));
        save();
    }

    public static void deletePreset(int index) {
        if (index >= 0 && index < presets.size()) {
            presets.remove(index);
            save();
        }
    }
}
