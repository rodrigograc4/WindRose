package com.rodrigograc4.windrose.util;

import com.rodrigograc4.windrose.config.WindRoseConfig.DirectionMode;
import net.minecraft.network.chat.Component;

/**
 * Turns the player's yaw into the text of the Direction module.
 *
 * <p>Minecraft's yaw is 0 facing south (+Z), 90 west (-X), 180 north (-Z) and 270 east (+X).
 * The compass has 8 directions, 45 degrees each; the axis is the one of 4 (90 degrees each)
 * that grows fastest where the player looks.
 */
public final class Compass {

    // Clockwise from yaw 0.
    private static final String[] DIRECTIONS = {
            "south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"
    };
    private static final String[] AXES = {"positive_z", "negative_x", "negative_z", "positive_x"};

    private Compass() {
    }

    public static String describe(float yaw, DirectionMode mode) {
        String direction = DIRECTIONS[sector(yaw, 8)];
        String axis = AXES[sector(yaw, 4)];

        return switch (mode) {
            case CARDINAL -> text("windrose.direction." + direction);
            case AXIS -> text("windrose.axis." + axis);
            case AXIS_CARDINAL -> text("windrose.axis." + axis) + " (" + text("windrose.direction.short." + direction) + ")";
            case CARDINAL_AXIS -> text("windrose.direction." + direction) + " (" + text("windrose.axis.short." + axis) + ")";
        };
    }

    // Which of `count` equal slices of the circle the yaw falls in; slice 0 is centred on yaw 0.
    static int sector(float yaw, int count) {
        double size = 360.0 / count;
        double normalized = ((yaw + size / 2) % 360 + 360) % 360;
        return (int) (normalized / size) % count;
    }

    private static String text(String key) {
        return Component.translatable(key).getString();
    }
}
