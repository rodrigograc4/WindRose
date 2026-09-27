package com.rodrigograc4.windrose.renderer;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.WindRoseConfig.LabelPosition;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import com.rodrigograc4.windrose.util.WorldKey;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;

public class WindRoseHud implements HudElement {

    private static final String LABEL_VALUE_SEPARATOR = " ";

    private static int opaque(int rgb) { return 0xFF000000 | rgb; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        WindRoseConfig c = WindRoseConfig.INSTANCE;
        if (!c.statsEnabled || client.player == null || client.level == null) return;

        Font font = client.font;
        int x = (int) c.margin;
        int y = (int) c.margin;
        int lineHeight = font.lineHeight + (int) c.linePadding;

        for (WindRoseModule module : c.activeModules) {
            if (!module.enabled) continue;

            if (module.type == ModuleType.SPACER) {
                y += lineHeight;
                continue;
            }

            String value = getValue(client, c, module.type);
            if (!value.isEmpty()) {
                drawModule(ctx, font, module, value, x, y);
                y += lineHeight;
            }
        }
    }

    // The day counter uses the overworld clock, which keeps counting in other dimensions
    private static String getValue(Minecraft client, WindRoseConfig c, ModuleType type) {
        return switch (type) {
            case COORDS -> {
                BlockPos p = client.player.blockPosition();
                yield p.getX() + "  " + p.getY() + "  " + p.getZ();
            }
            case DAY -> {
                long totalTicks = client.level.getOverworldClockTime();
                long days = (totalTicks / 24000L) + c.dayCountOffset;
                if (!c.showHours) yield String.valueOf(days);

                long dayTime = totalTicks % 24000L;
                int hours = (int) ((dayTime / 1000 + 6) % 24);
                int minutes = (int) ((dayTime % 1000) * 60 / 1000);
                yield days + "   " + String.format("%02d:%02d", hours, minutes);
            }
            case FPS -> String.valueOf(client.getFps());
            case DIRECTION -> {
                float yaw = client.player.getYRot();
                yield switch (c.directionMode) {
                    case CARDINAL -> getCardinalFull(yaw);
                    case AXIS -> getAxisFull(yaw);
                };
            }
            case TOTEMS -> String.valueOf(c.getTotemsForWorld(WorldKey.current()));
            case SPACER -> "";
        };
    }

    // Draws a "label value" line ("value label" for FPS, depending on config) with the optional
    // background (1px padding on the sides and above the text). Shared with the module list preview.
    public static void drawModule(GuiGraphicsExtractor ctx, Font font, WindRoseModule module, String value, int x, int y) {
        WindRoseConfig c = WindRoseConfig.INSTANCE;
        String sep = LABEL_VALUE_SEPARATOR;
        boolean valueFirst = module.type == ModuleType.FPS && c.labelPosition == LabelPosition.AFTER_VALUE;

        String first = valueFirst ? value : module.customLabel;
        String second = valueFirst ? module.customLabel : value;
        int firstColor = opaque(valueFirst ? module.valueColor : module.labelColor);
        int secondColor = opaque(valueFirst ? module.labelColor : module.valueColor);

        int firstWidth = font.width(first + sep);
        int textWidth = firstWidth + font.width(second);

        if (c.backgroundEnabled) {
            ctx.fill(x, y, x + 1 + textWidth + 1, y + font.lineHeight, c.backgroundColor);
        }

        ctx.text(font, first + sep, x + 1, y + 1, firstColor);
        ctx.text(font, second, x + 1 + firstWidth, y + 1, secondColor);
    }

    private static String getCardinalFull(float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        if (yaw >= 315 || yaw < 45) return "South";
        if (yaw < 135) return "West";
        if (yaw < 225) return "North";
        return "East";
    }

    private static String getAxisFull(float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        if (yaw >= 315 || yaw < 45) return "Positive Z";
        if (yaw < 135) return "Negative X";
        if (yaw < 225) return "Negative Z";
        return "Positive X";
    }
}
