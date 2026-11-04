package com.redteam.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import net.fabricmc.loader.api.FabricLoader;

/** Пишет размеченный лог: время (UTC, мс) ; модуль ; событие ; детали. */
public final class CheatLogger {
    private static BufferedWriter out;

    public static synchronized void log(String module, String event, String details) {
        try {
            if (out == null) {
                Path file = FabricLoader.getInstance().getGameDir().resolve("redteam_cheatlog.csv");
                boolean isNew = !Files.exists(file);
                out = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                if (isNew) out.write("utc_ms;module;event;details\n");
            }
            out.write(Instant.now().toEpochMilli() + ";" + module + ";" + event + ";" + details + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println("[RedTeam] не удалось записать лог: " + e);
        }
    }
}
