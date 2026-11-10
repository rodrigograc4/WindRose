package com.rodrigograc4.windrose.gametest;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.WindRoseConfig.DirectionMode;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import com.rodrigograc4.windrose.screen.EditModuleScreen;
import com.rodrigograc4.windrose.screen.ModulesScreen;
import com.rodrigograc4.windrose.screen.SettingsScreen;
import com.rodrigograc4.windrose.util.Compass;
import com.rodrigograc4.windrose.util.WorldKey;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Checks the direction text, that deleting every module sticks, the totem counter and the
 * "Hide When F3 Is Open" option. Screenshots (2x) land in build/run/clientGameTest/screenshots.
 */
public class WindRoseClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            checkDirections();
            checkDeletingEveryModuleSticks();
        });

        context.getInput().resizeWindow(1708, 960);
        context.runOnClient(client -> {
            WindRoseConfig config = WindRoseConfig.INSTANCE;
            config.activeModules = new java.util.ArrayList<>(List.of(
                    new WindRoseModule(ModuleType.DAY),
                    new WindRoseModule(ModuleType.COORDS),
                    new WindRoseModule(ModuleType.DIRECTION),
                    new WindRoseModule(ModuleType.TOTEMS),
                    new WindRoseModule(ModuleType.FPS)));
            config.directionMode = DirectionMode.CARDINAL;
        });

        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            world.getServer().runCommand("time set 6000");
            world.getServer().runCommand("gamerule advance_time false");
            // Facing a bit east of north, so every direction mode has something to show.
            world.getServer().runCommand("tp @a 0.5 -60 0.5 -130 0");
            context.waitTicks(60);

            checkTotemCounter(context, world);
            // The totem's advancement toast and chat messages would clutter the screenshots.
            context.runOnClient(client -> {
                client.gui.toastManager().clear();
                client.gui.hud.getChat().clearMessages(false);
            });
            context.waitTicks(5);

            context.takeScreenshot("windrose-1-hud");

            context.runOnClient(client -> WindRoseConfig.INSTANCE.backgroundEnabled = true);
            context.waitTicks(2);
            context.takeScreenshot("windrose-2-hud-background");
            context.runOnClient(client -> WindRoseConfig.INSTANCE.backgroundEnabled = false);

            for (DirectionMode mode : DirectionMode.values()) {
                context.runOnClient(client -> WindRoseConfig.INSTANCE.directionMode = mode);
                context.waitTicks(2);
                context.takeScreenshot("windrose-3-direction-" + mode.name().toLowerCase());
            }
            context.runOnClient(client -> WindRoseConfig.INSTANCE.directionMode = DirectionMode.CARDINAL);

            checkHideWithDebugScreen(context);

            // The HUD would show through, blurred, behind the config screens.
            context.runOnClient(client -> WindRoseConfig.INSTANCE.statsEnabled = false);
            context.setScreen(() -> new ModulesScreen(null));
            context.waitTicks(5);
            context.takeScreenshot("windrose-5-modules");

            context.setScreen(() -> SettingsScreen.create(null));
            context.waitTicks(5);
            context.takeScreenshot("windrose-6-settings");

            context.setScreen(() -> EditModuleScreen.create(null, findModule(ModuleType.DIRECTION)));
            context.waitTicks(5);
            context.takeScreenshot("windrose-7-edit-direction");

            context.setScreen(() -> EditModuleScreen.create(null, findModule(ModuleType.DAY)));
            context.waitTicks(5);
            context.takeScreenshot("windrose-8-edit-day");
            context.setScreen(() -> null);
            context.runOnClient(client -> WindRoseConfig.INSTANCE.statsEnabled = true);
        }
    }

    // Yaw 0 faces south (+Z), 90 west (-X), 180 north (-Z), 270 east (+X).
    private static void checkDirections() {
        expect(Compass.describe(0, DirectionMode.CARDINAL), "South");
        expect(Compass.describe(45, DirectionMode.CARDINAL), "Southwest");
        expect(Compass.describe(90, DirectionMode.CARDINAL), "West");
        expect(Compass.describe(180, DirectionMode.CARDINAL), "North");
        expect(Compass.describe(-180, DirectionMode.CARDINAL), "North");
        expect(Compass.describe(230, DirectionMode.CARDINAL), "Northeast");
        expect(Compass.describe(270, DirectionMode.CARDINAL), "East");
        expect(Compass.describe(-45, DirectionMode.CARDINAL), "Southeast");
        expect(Compass.describe(359, DirectionMode.CARDINAL), "South");

        expect(Compass.describe(0, DirectionMode.AXIS), "Positive Z");
        expect(Compass.describe(90, DirectionMode.AXIS), "Negative X");
        expect(Compass.describe(180, DirectionMode.AXIS), "Negative Z");
        expect(Compass.describe(270, DirectionMode.AXIS), "Positive X");

        // Between northeast and east: x grows fastest.
        expect(Compass.describe(240, DirectionMode.AXIS_CARDINAL), "Positive X (NE)");
        expect(Compass.describe(240, DirectionMode.CARDINAL_AXIS), "Northeast (+X)");
        expect(Compass.describe(210, DirectionMode.CARDINAL_AXIS), "Northeast (-Z)");
    }

    private static void checkDeletingEveryModuleSticks() {
        WindRoseConfig config = WindRoseConfig.INSTANCE;
        check(config.defaultModulesAdded, "defaults should be marked as added after the first start");
        config.activeModules.clear();
        config.addDefaultModulesOnce();
        check(config.activeModules.isEmpty(), "deleting every module must not bring the defaults back");
    }

    private static void checkTotemCounter(ClientGameTestContext context, TestSingleplayerContext world) {
        int before = context.computeOnClient(client -> WindRoseConfig.INSTANCE.getTotemsForWorld(WorldKey.current()));
        world.getServer().runCommand("gamemode survival @a");
        world.getServer().runCommand("item replace entity @a weapon.offhand with minecraft:totem_of_undying");
        context.waitTicks(5);
        // Lethal damage that a totem can stop (unlike /kill).
        world.getServer().runOnServer(server -> {
            var player = server.getPlayerList().getPlayers().getFirst();
            player.hurtServer(player.level(), player.damageSources().generic(), 100.0F);
            check(player.isAlive() && player.getOffhandItem().isEmpty(), "the totem should have saved the player");
        });
        context.waitTicks(20);
        world.getServer().runCommand("gamemode creative @a");
        world.getServer().runCommand("effect clear @a");
        world.getServer().runCommand("tp @a 0.5 -60 0.5 -130 0");
        context.waitTicks(60);

        int after = context.computeOnClient(client -> WindRoseConfig.INSTANCE.getTotemsForWorld(WorldKey.current()));
        check(after == before + 1, "a totem saving the player should count once, went from " + before + " to " + after);
    }

    private static void checkHideWithDebugScreen(ClientGameTestContext context) {
        context.getInput().pressKey(InputConstants.KEY_F3);
        context.waitTicks(5);
        check(context.computeOnClient(client -> client.getDebugOverlay().showDebugScreen()), "F3 should open the debug screen");
        context.takeScreenshot("windrose-4-f3-shown");

        context.runOnClient(client -> WindRoseConfig.INSTANCE.hideWithDebugScreen = true);
        context.waitTicks(2);
        context.takeScreenshot("windrose-4-f3-hidden");
        context.runOnClient(client -> WindRoseConfig.INSTANCE.hideWithDebugScreen = false);

        context.getInput().pressKey(InputConstants.KEY_F3);
        context.waitTicks(5);
    }

    private static WindRoseModule findModule(ModuleType type) {
        return WindRoseConfig.INSTANCE.activeModules.stream().filter(m -> m.type == type).findFirst().orElseThrow();
    }

    private static void expect(String actual, String expected) {
        check(actual.equals(expected), "expected \"" + expected + "\" but got \"" + actual + "\"");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
