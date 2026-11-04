package com.redteam.client.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface InteractionManagerAccessor {
    @Accessor("blockBreakingCooldown")
    void redteam$setBlockBreakingCooldown(int value);
}
