package com.rodrigograc4.windrose;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.renderer.WindRoseHud;
import com.rodrigograc4.windrose.util.WorldKey;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindRose implements ClientModInitializer {
    public static final String MOD_ID = "windrose";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Registers the HUD just below the chat, so chat messages stay readable on top of the stats
    @Override
    public void onInitializeClient() {
        WindRoseConfig.init();
        WorldKey.register();

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "stats"),
                new WindRoseHud()
        );

        LOGGER.info("WindRose initialized successfully!");
    }
}
