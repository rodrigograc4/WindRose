package com.rodrigograc4.windrose.util;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;

public final class WorldKey {
    private static final String UNKNOWN = "UnknownWorld";

    private static @Nullable String cached;

    private WorldKey() {}

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> cached = null);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> cached = null);
    }

    public static String current() {
        if (cached == null) {
            cached = resolve(Minecraft.getInstance());
        }
        return cached;
    }

    // Stable per-world key, cached per connection: sp:<save folder>, mp:<address> or realm:<name>.
    // Also migrates stats saved by versions up to 1.1.0, which keyed them by world / server list name.
    private static String resolve(Minecraft client) {
        String key;
        String legacyKey;

        IntegratedServer integrated = client.getSingleplayerServer();
        ServerData server = client.getCurrentServer();
        if (integrated != null) {
            Path saveDir = integrated.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
            key = "sp:" + saveDir.getFileName();
            legacyKey = integrated.getWorldData().getLevelName();
        } else if (server != null) {
            key = server.isRealm() ? "realm:" + server.name : "mp:" + server.ip.toLowerCase(Locale.ROOT);
            legacyKey = server.name;
        } else {
            return UNKNOWN;
        }

        WindRoseConfig.INSTANCE.migrateWorldKey(legacyKey, key);
        return key;
    }
}
