package com.rodrigograc4.windrose.renderer;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.WindRoseConfig.LabelPosition;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;

public class WindRoseHud implements HudRenderCallback {

    private static int opaque(int rgb) { return 0xFF000000 | rgb; }
    private static final String LABEL_VALUE_SEPARATOR = " ";

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null || !WindRoseConfig.INSTANCE.statsEnabled) return;

        TextRenderer tr = client.textRenderer;
        WindRoseConfig c = WindRoseConfig.INSTANCE;

        int hudMargin = (int) c.margin;
        int x = hudMargin;
        int y = hudMargin;

        int linePadding = (int) c.linePadding;
        int lineHeight = tr.fontHeight + linePadding;

        String sep = LABEL_VALUE_SEPARATOR;


        for (WindRoseModule module : c.activeModules) {
            if (!module.enabled) continue;

            String value = "";
            switch (module.type) {
                case COORDS -> {
                    BlockPos p = client.player.getBlockPos();
                    value = p.getX() + "  " + p.getY() + "  " + p.getZ();
                }
                case DAY -> {
                    long totalTicks = client.world.getTimeOfDay();
                    long days = (totalTicks / 24000L) + c.dayCountOffset;

                    if (c.showHours) {
                        long dayTime = totalTicks % 24000L;
                        int hours = (int) ((dayTime / 1000 + 6) % 24);
                        int minutes = (int) ((dayTime % 1000) * 60 / 1000);
                        value = days + "   " + String.format("%02d:%02d", hours, minutes);
                    } else {
                        value = String.valueOf(days);
                    }
                }
                case FPS -> value = String.valueOf(client.getCurrentFps());
                case DIRECTION -> {
                    float yaw = client.player.getYaw();
                    value = switch (c.directionMode) {
                        case CARDINAL -> getCardinalFull(yaw);
                        case AXIS -> getAxisFull(yaw);
                    };
                }
                case TOTEMS -> value = String.valueOf(c.getTotemsForWorld(getWorldKey(client)));
                case SPACER -> {
                    y += lineHeight;
                    continue;
                }
            }

            if (!value.isEmpty()) {
                int textWidth = tr.getWidth(module.customLabel) + tr.getWidth(value);
                if (c.backgroundEnabled) {
                    ctx.fill(
                            x,                          // left
                            y,                          // top
                            x + textWidth + 2 + 4,      // right (1px padding each side)
                            y + tr.fontHeight,          // bottom (1px above, 0px below)
                            c.backgroundColor
                    );
                }

                if (module.type == ModuleType.FPS) {
                    if (c.labelPosition == LabelPosition.AFTER_VALUE) {
                        ctx.drawTextWithShadow(tr, value + sep, x + 1, y + 1, opaque(module.valueColor));
                        ctx.drawTextWithShadow(tr, module.customLabel,x + 1 + tr.getWidth(value), y + 1, opaque(module.labelColor));
                    } else {
                        ctx.drawTextWithShadow(tr, module.customLabel + sep, x + 1, y + 1, opaque(module.labelColor));
                        ctx.drawTextWithShadow(tr, value, x + 1 + tr.getWidth(module.customLabel + sep), y + 1, opaque(module.valueColor));
                    }

                } else {
                    ctx.drawTextWithShadow(tr, module.customLabel + sep, x + 1, y + 1, opaque(module.labelColor));
                    ctx.drawTextWithShadow(tr, value, x + 1 + tr.getWidth(module.customLabel + sep), y + 1, opaque(module.valueColor));
                }

                y += lineHeight;
            }
        }
    }

    private String getWorldKey(MinecraftClient client) {
        if (client.getCurrentServerEntry() != null) return client.getCurrentServerEntry().name;
        if (client.getServer() != null) return client.getServer().getSaveProperties().getLevelName();
        return "UnknownWorld";
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
