package com.redteam.client.modules;

import com.redteam.client.mixin.InteractionManagerAccessor;

/** Убирает задержку между разрушением соседних блоков. */
public class FastBreak extends Module {
    public FastBreak() { super("FastBreak"); }

    @Override
    public void onTick() {
        if (mc.interactionManager == null) return;
        try {
            ((InteractionManagerAccessor) mc.interactionManager).redteam$setBlockBreakingCooldown(0);
        } catch (Throwable ignored) {}
    }
}
