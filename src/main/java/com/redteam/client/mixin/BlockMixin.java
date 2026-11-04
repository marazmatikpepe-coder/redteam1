package com.redteam.client.mixin;

import com.redteam.client.modules.XRay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true, require = 0)
    private static void redteam$xray(BlockState state, BlockState neighborState, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (XRay.isActive()) cir.setReturnValue(XRay.shouldDraw(state, neighborState));
    }
}
