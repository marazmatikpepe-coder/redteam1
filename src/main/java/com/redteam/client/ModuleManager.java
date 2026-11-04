package com.redteam.client;

import java.util.List;

import com.redteam.client.modules.*;
import com.redteam.client.modules.Module;

public final class ModuleManager {
    public static final List<Module> MODULES = List.of(
            new KillAura(), new AutoFight(), new AutoTotem(), new Velocity(), new NoFall(),
            new Fly(), new Speed(), new FastPlace(), new FastBreak(), new AntiFire());

    public static void tick() {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }
}
