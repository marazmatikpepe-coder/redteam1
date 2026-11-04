package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Если здоровья <= 4 (2 сердца) и игрок только что получил урон, а в левой руке нет тотема:
 * находит тотем в инвентаре и меняет его с левой рукой через SWAP (кнопка 40).
 */
public class AutoTotem extends Module {
    private int cooldown = 0;

    public AutoTotem() { super("AutoTotem"); }

    @Override
    public void onTick() {
        var p = mc().player;
        if (p == null || mc().interactionManager == null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (p.getHealth() > 4.0f || p.hurtTime <= 0) return;
        if (p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;

        // Слоты в окне инвентаря игрока: 9-35 - основной инвентарь, 36-44 - хотбар.
        int found = -1;
        for (int slot = 9; slot <= 44; slot++) {
            if (p.playerScreenHandler.getSlot(slot).getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                found = slot;
                break;
            }
        }
        if (found == -1) return;

        mc().interactionManager.clickSlot(p.playerScreenHandler.syncId, found, 40, SlotActionType.SWAP, p);
        CheatLogger.log(name, "SWAP", "slot=" + found + ";hp=" + p.getHealth());
        cooldown = 10; // ~0.5 сек
    }
}
