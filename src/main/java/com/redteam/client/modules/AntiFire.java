package com.redteam.client.modules;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/**
 * Если игрок горит и в хотбаре есть ведро воды: смотрит вниз, выливает воду,
 * на следующем тике забирает её обратно и возвращает прежний слот.
 */
public class AntiFire extends Module {
    private int phase = 0;
    private int prevSlot = -1;
    private int cooldown = 0;

    public AntiFire() { super("AntiFire"); }

    @Override protected void onDisable() { phase = 0; }

    @Override
    public void onTick() {
        var p = mc.player;
        if (p == null || mc.interactionManager == null) return;
        if (cooldown > 0) { cooldown--; return; }

        if (phase == 0) {
            if (!p.isOnFire() || p.isTouchingWater()) return;
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (p.getInventory().getStack(i).isOf(Items.WATER_BUCKET)) { slot = i; break; }
            }
            if (slot == -1) return;
            prevSlot = p.getInventory().getSelectedSlot();
            p.getInventory().setSelectedSlot(slot);
            p.setPitch(90f);
            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            phase = 1;
        } else {
            // забираем воду обратно
            p.setPitch(90f);
            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            if (prevSlot >= 0) p.getInventory().setSelectedSlot(prevSlot);
            phase = 0;
            cooldown = 40;
        }
    }
}
