package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gl.RenderPipelines;

import java.util.List;

public class ModuleListWidget
        extends AlwaysSelectedEntryListWidget<ModuleListWidget.ModuleEntry> {

    private final ModulesScreen parent;

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
        private final MinecraftClient client = MinecraftClient.getInstance();

        public ModuleEntry(WindRoseModule module) {
            this.module = module;
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY,
                           boolean hovered, float delta) {

            int x = getRowLeft();
            int y = getY();

            ctx.drawTextWithShadow(
                    client.textRenderer,
                    Text.literal(module.type.getName()),
                    x + 40,
                    y + 4,
                    0xFFFFFFFF
            );

            String preview = getPreviewValue();
            ctx.drawTextWithShadow(
                    client.textRenderer,
                    Text.literal(preview),
                    x + 40,
                    y + 18,
                    0xFFAAAAAA
            );

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

        private String getPreviewValue() {


            return switch (module.type) {
                case COORDS -> "XYZ: 100 200 300";
                case DIRECTION -> "↑ North";
                case FPS -> "FPS: 120";
                case DAY -> "Day 100";
                case TOTEMS -> "Totems Popped: 5";
                case SPACER -> " ";
            };
        }

        @Override
        public Text getNarration() {
            return Text.literal(module.type.getName());
        }
    }
}
