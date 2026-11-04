package com.redteam.client.modules;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/** Сообщает серверу, что игрок «стоит на земле», пока падает: урон от падения не проходит. */
public class NoFall extends Module {
    public NoFall() { super("NoFall"); }

    @Override
    public void onTick() {
        var p = mc().player;
        if (p == null || mc().getNetworkHandler() == null) return;
        if (p.fallDistance > 2.0f && !p.isOnGround()) {
            mc().getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, p.horizontalCollision));
        }
    }
}
