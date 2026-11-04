package com.redteam.client.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class Targets {
    private Targets() {}

    /** Подходит ли сущность как цель. onlyHostile=false: бьём всех живых, включая мирных мобов. */
    public static boolean valid(Entity self, Entity e, boolean onlyHostile) {
        if (!(e instanceof LivingEntity le) || e == self || !le.isAlive()) return false;
        if (e instanceof ArmorStandEntity) return false;
        if (e instanceof PlayerEntity pe && pe.isSpectator()) return false;
        return !onlyHostile || e instanceof HostileEntity || e instanceof PlayerEntity;
    }
}
