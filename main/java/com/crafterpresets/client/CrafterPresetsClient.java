package com.crafterpresets.client;

import net.fabricmc.api.ClientModInitializer;

public class CrafterPresetsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PresetStorage.load();
    }
}
