package com.redteam.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.redteam.client.modules.Module;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class RedTeamClient implements ClientModInitializer {
    private static KeyBinding menuKey;
    private static final Map<Module, KeyBinding> TOGGLE_KEYS = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("redteam", "main"));

        // Меню: по умолчанию Right Shift.
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.redteam.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        // Отдельная клавиша вкл/выкл для каждого чита (по умолчанию не назначена).
        for (Module m : ModuleManager.MODULES) {
            KeyBinding kb = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.redteam.toggle." + m.name.toLowerCase(), InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN, category));
            TOGGLE_KEYS.put(m, kb);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) client.setScreen(new MenuScreen());
            for (Map.Entry<Module, KeyBinding> e : TOGGLE_KEYS.entrySet()) {
                while (e.getValue().wasPressed()) e.getKey().toggle();
            }
            if (client.player != null) ModuleManager.applyDefaults();
            ModuleManager.tick();
        });
    }
}
