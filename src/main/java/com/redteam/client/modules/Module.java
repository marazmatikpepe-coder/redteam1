package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    private boolean enabled;

    protected Module(String name) { this.name = name; }

    protected MinecraftClient mc() { return MinecraftClient.getInstance(); }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        CheatLogger.log(name, value ? "ON" : "OFF", getDetails());
        if (value) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    /** Включён ли чит сразу при входе в мир. */
    public boolean enabledByDefault() { return false; }

    /** Короткое описание текущих настроек для лога и меню. */
    public String getDetails() { return ""; }

    /** Переключение режима/настройки (кнопка «режим» в меню). */
    public void cycleMode() {}

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
