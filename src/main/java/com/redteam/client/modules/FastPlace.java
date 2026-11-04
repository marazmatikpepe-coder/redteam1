package com.redteam.client.modules;

import com.redteam.client.mixin.MinecraftClientAccessor;

/** Убирает задержку между установкой блоков / использованием предметов. */
public class FastPlace extends Module {
    public FastPlace() { super("FastPlace"); }

    @Override
    public void onTick() {
        try {
            ((MinecraftClientAccessor) mc).redteam$setItemUseCooldown(0);
        } catch (Throwable ignored) {}
    }
}
