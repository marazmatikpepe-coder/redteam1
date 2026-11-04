package com.redteam.client.modules;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Хождение по воде и лаве. Если игрок упал в жидкость, его выталкивает на поверхность; над жидкостью
 * он «встаёт» на её верхнюю границу. В лаву игрок не погружается, поэтому не горит и не получает урон
 * (кроме случая, когда сервер сам считает его внутри лавы). Shift - нырнуть.
 */
public class Jesus extends Module {
    public Jesus() { super("Jesus"); }

    @Override
    public void onTick() {
        var p = mc().player;
        var world = mc().world;
        if (p == null || world == null) return;
        if (mc().options.sneakKey.isPressed()) return;

        Vec3d v = p.getVelocity();
        if (p.isTouchingWater() || p.isInLava()) {
            p.setVelocity(v.x, 0.25, v.z);
            return;
        }
        if (v.y > 0) return;

        double predY = p.getY() + v.y - 0.05;
        BlockPos pos = BlockPos.ofFloored(p.getX(), predY, p.getZ());
        FluidState fs = world.getFluidState(pos);
        if (fs.isEmpty()) return;

        double top = pos.getY() + fs.getHeight(world, pos);
        if (p.getY() >= top - 0.05 && predY < top) {
            p.setPosition(p.getX(), top, p.getZ());
            p.setVelocity(v.x, 0, v.z);
            p.setOnGround(true);
        }
    }
}
