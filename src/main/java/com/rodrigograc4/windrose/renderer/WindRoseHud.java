package com.rodrigograc4.windrose.renderer;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;

public class WindRoseHud implements HudRenderCallback {

    private static int opaque(int rgb) { return 0xFF000000 | rgb; }

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || !WindRoseConfig.INSTANCE.statsEnabled) return;

        TextRenderer tr = client.textRenderer;
        WindRoseConfig c = WindRoseConfig.INSTANCE;

        int hudMargin = (int) c.margin;
        int x = hudMargin;
        int y = hudMargin;

        int linePadding = (int) c.linePadding;
        int lineHeight = tr.fontHeight + linePadding;

        for (WindRoseModule module : c.activeModules) {
            if (!module.enabled) continue;

            String value = "";
            switch (module.type) {
                case COORDS -> {
                    BlockPos p = client.player.getBlockPos();
                    value = p.getX() + ", " + p.getY() + ", " + p.getZ();
                }
                case DAY -> value = String.valueOf((client.world.getTimeOfDay() / 24000L) + 1);
                case FPS -> value = String.valueOf(client.getCurrentFps());
                case DIRECTION -> value = getCardinalFull(client.player.getYaw());
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
                            x,           // 1px left of the text
                            y,           // 1px above the text
                            x + textWidth + 2, // width = text + 2px (1px each side)
                            y + tr.fontHeight, // height = text (1px above and -1px below)
                            c.backgroundColor
                    );
                }

                ctx.drawTextWithShadow(tr, module.customLabel, x + 1, y + 1, opaque(module.labelColor));
                ctx.drawTextWithShadow(tr, value, x + 1 + tr.getWidth(module.customLabel), y + 1, opaque(module.valueColor));

                y += lineHeight; 
            }
        }
    }

    private String getWorldKey(MinecraftClient client) {
        if (client.getCurrentServerEntry() != null) return client.getCurrentServerEntry().name;
        if (client.getServer() != null) return client.getServer().getSaveProperties().getLevelName();
        return "UnknownWorld";
    }

    private String getCardinalFull(float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        if (yaw >= 315 || yaw < 45) return "South";
        if (yaw < 135) return "West";
        if (yaw < 225) return "North";
        return "East";
    }
}
