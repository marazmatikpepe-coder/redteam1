package com.redteam.client.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

/** Общие боевые приёмы: удар и «крит» через пакеты движения (мини-прыжок без реального прыжка). */
public final class Combat {
    private Combat() {}

    public static void crit() {
        var mc = MinecraftClient.getInstance();
        var p = mc.player;
        var h = mc.getNetworkHandler();
        if (p == null || h == null) return;
        h.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() + 0.0625, p.getZ(), false, p.horizontalCollision));
        h.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), false, p.horizontalCollision));
    }

    public static void hit(Entity target, boolean withCrit) {
        var mc = MinecraftClient.getInstance();
        var p = mc.player;
        if (p == null || mc.interactionManager == null) return;
        if (withCrit) crit();
        mc.interactionManager.attackEntity(p, target);
    }

    public static void swing() {
        var p = MinecraftClient.getInstance().player;
        if (p != null) p.swingHand(Hand.MAIN_HAND);
    }
}
