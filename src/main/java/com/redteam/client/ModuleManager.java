package com.redteam.client;

import java.util.List;

import com.redteam.client.modules.*;
import com.redteam.client.modules.Module;

public final class ModuleManager {
    public static final List<Module> MODULES = List.of(
            new KillAura(), new InstaKill(), new AutoFight(), new AutoTotem(), new Velocity(),
            new NoFall(), new Fly(), new Speed(), new Jesus(), new FastPlace(), new FastBreak(),
            new AntiFire(), new AntiDrown(), new XRay());

    private static boolean defaultsApplied = false;

    /** Включает читы, помеченные «по умолчанию» (один раз, когда игрок вошёл в мир). */
    public static void applyDefaults() {
        if (defaultsApplied) return;
        defaultsApplied = true;
        for (Module m : MODULES) if (m.enabledByDefault()) m.setEnabled(true);
    }

    public static void tick() {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }
}
