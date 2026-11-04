package com.redteam.client;

import java.util.List;

import com.redteam.client.modules.AutoTotem;
import com.redteam.client.modules.KillAura;
import com.redteam.client.modules.Module;

public final class ModuleManager {
    public static final List<Module> MODULES = List.of(new KillAura(), new AutoTotem());

    public static void tick() {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }
}
