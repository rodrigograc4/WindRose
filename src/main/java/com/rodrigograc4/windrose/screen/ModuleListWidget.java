package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.WindRoseConfig.LabelPosition;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.font.TextRenderer;


import java.util.List;

public class ModuleListWidget
        extends AlwaysSelectedEntryListWidget<ModuleListWidget.ModuleEntry> {

    private final ModulesScreen parent;
    private static final String LABEL_VALUE_SEPARATOR = " ";

    public ModuleListWidget(ModulesScreen parent, MinecraftClient client,
                            int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.parent = parent;
    }

    public void refreshList() {
        clearEntries();
        for (WindRoseModule module : WindRoseConfig.INSTANCE.activeModules) {
            addEntry(new ModuleEntry(module));
        }
    }

    @Override
    public int getRowWidth() {
        return 260;
    }

    @Override
    protected int getScrollbarX() {
        return getRowLeft() + getRowWidth() + 6;
    }


    public class ModuleEntry
            extends AlwaysSelectedEntryListWidget.Entry<ModuleEntry> {

        public final WindRoseModule module;

        public ModuleEntry(WindRoseModule module) {
            this.module = module;
        }

        private String getPreviewValue() {
            return switch (module.type) {
                case COORDS -> "100  64  200";
                case DIRECTION -> switch (WindRoseConfig.INSTANCE.directionMode) {
                    case CARDINAL -> "North";
                    case AXIS -> "Negative Z";
                };
                case FPS -> "120";
                case DAY -> WindRoseConfig.INSTANCE.showHours ? "51   14:30" : "51";
                case TOTEMS -> "7";
                case SPACER -> "";
            };
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY,
                        boolean hovered, float delta) {
            int x = getRowLeft();
            int y = getY();

            String sep = LABEL_VALUE_SEPARATOR;

            ctx.drawTextWithShadow(
                    client.textRenderer,
                    Text.literal(module.type.getName()),
                    x + 40,
                    y + 4,
                    0xFFFFFFFF
            );

            String label = module.customLabel;
            String value = getPreviewValue();

            TextRenderer tr = client.textRenderer;

            if (!value.isEmpty() && WindRoseConfig.INSTANCE.backgroundEnabled) {
                int width = tr.getWidth(label + value);
                int height = tr.fontHeight;
                ctx.fill(x + 40, y + 18, x + 40 + width + 2 + 4, y + 18 + height, WindRoseConfig.INSTANCE.backgroundColor);
            }

            if (module.type == ModuleType.FPS) {
                    if (WindRoseConfig.INSTANCE.labelPosition == LabelPosition.BEFORE_VALUE) {
                        ctx.drawTextWithShadow(tr, Text.literal(label + sep), x + 41, y + 19, opaque(module.labelColor));
                        ctx.drawTextWithShadow(tr, Text.literal(value), x + 41 + tr.getWidth(label + sep), y + 19, opaque(module.valueColor));
                    } else {
                        ctx.drawTextWithShadow(tr, Text.literal(value + sep), x + 41, y + 19, opaque(module.valueColor));
                        ctx.drawTextWithShadow(tr, Text.literal(label), x + 41 + tr.getWidth(value + sep), y + 19, opaque(module.labelColor));
                    }
            } else {
                ctx.drawTextWithShadow(tr, Text.literal(label + sep), x + 41, y + 19, opaque(module.labelColor));
                ctx.drawTextWithShadow(tr, Text.literal(value), x + 41 + tr.getWidth(label + sep), y + 19, opaque(module.valueColor));
            }

            if (hovered) {
                int index = children().indexOf(this);

                if (index > 0) {
                    ctx.drawGuiTexture(
                            RenderPipelines.GUI_TEXTURED,
                            Identifier.ofVanilla("server_list/move_up"),
                            x + 4, y, 36, 36
                    );
                }

                if (index < getEntryCount() - 1) {
                    ctx.drawGuiTexture(
                            RenderPipelines.GUI_TEXTURED,
                            Identifier.ofVanilla("server_list/move_down"),
                            x + 4, y, 36, 36
                    );
                }
            }
        }

        private int opaque(int rgb) {
            return 0xFF000000 | rgb;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            int index = children().indexOf(this);
            double relX = click.x() - getRowLeft();
            double relY = click.y() - getY();

            if (relX <= 24) {
                if (relY < 16 && index > 0) {
                    swap(index, index - 1);
                    return true;
                }
                if (relY >= 16 && index < getEntryCount() - 1) {
                    swap(index, index + 1);
                    return true;
                }
            }

            setSelected(this);
            parent.updateButtonStates();
            return true;
        }

        private void swap(int a, int b) {
            List<WindRoseModule> list = WindRoseConfig.INSTANCE.activeModules;
            WindRoseModule tmp = list.get(a);
            list.set(a, list.get(b));
            list.set(b, tmp);
            refreshList();
            setSelected(children().get(b));
        }

        @Override
        public Text getNarration() {
            return Text.literal(module.type.getName());
        }
    }
}
