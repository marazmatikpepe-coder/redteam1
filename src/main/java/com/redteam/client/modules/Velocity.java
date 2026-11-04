package com.redteam.client.modules;

import com.redteam.client.CheatLogger;

/** Антиоткидывание: после получения удара гасит часть горизонтальной скорости. */
public class Velocity extends Module {
    private boolean full = true;

    public Velocity() { super("Velocity"); }

    @Override public void cycleMode() { full = !full; CheatLogger.log(name, "MODE", getDetails()); }
    @Override public String getDetails() { return full ? "reduce=100%" : "reduce=50%"; }

    @Override
    public void onTick() {
        var p = mc.player;
        if (p == null) return;
        if (p.hurtTime == 9) {
            double f = full ? 0.0 : 0.5;
            var v = p.getVelocity();
            p.setVelocity(v.x * f, v.y, v.z * f);
        }
    }
}
