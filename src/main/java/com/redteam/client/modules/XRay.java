package com.redteam.client.modules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.redteam.client.CheatLogger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;

/**
 * X-ray: рисуются только руды (и древние обломки), всё остальное невидимо. Включает ночное зрение.
 * Режимы (кнопка «режим»):
 *  NORMAL - все руды, которые прислал сервер.
 *  SAFE   - только руды, у которых есть открытая грань (рядом пустота). Антиксрей на сервере подменяет
 *           только скрытые внутри камня блоки, поэтому «ложные руды» в толще камня в этом режиме не рисуются.
 * Клиент показывает только то, что прислал сервер: настоящих руд под защитой сервера он не видит.
 */
public class XRay extends Module {
    private static volatile boolean active = false;
    private static volatile boolean safe = false;
    private static final Map<Block, Boolean> ORE_CACHE = new ConcurrentHashMap<>();

    public XRay() { super("XRay"); }

    public static boolean isActive() { return active; }

    public static boolean isOre(Block block) {
        return ORE_CACHE.computeIfAbsent(block, b -> {
            String path = Registries.BLOCK.getId(b).getPath();
            return path.endsWith("_ore") || path.equals("ancient_debris");
        });
    }

    /** Вызывается из миксина при построении чанков. */
    public static boolean shouldDraw(BlockState state, BlockState neighbor) {
        if (!isOre(state.getBlock())) return false;
        return !safe || !neighbor.isOpaque();
    }

    @Override public String getDetails() { return safe ? "SAFE" : "NORMAL"; }

    @Override public void cycleMode() {
        safe = !safe;
        CheatLogger.log(name, "MODE", getDetails());
        if (active) reloadChunks();
    }

    @Override
    protected void onEnable() {
        active = true;
        var p = mc().player;
        if (p != null) p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1_000_000, 0, false, false));
        reloadChunks();
    }

    @Override
    protected void onDisable() {
        active = false;
        var p = mc().player;
        if (p != null) p.removeStatusEffect(StatusEffects.NIGHT_VISION);
        reloadChunks();
    }

    private void reloadChunks() {
        if (mc().worldRenderer != null && mc().world != null) mc().worldRenderer.reload();
    }
}
