package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import com.rodrigograc4.windrose.renderer.WindRoseHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class ModuleListWidget
        extends ObjectSelectionList<ModuleListWidget.ModuleEntry> {

    private static final Identifier MOVE_UP_SPRITE = Identifier.withDefaultNamespace("server_list/move_up");
    private static final Identifier MOVE_DOWN_SPRITE = Identifier.withDefaultNamespace("server_list/move_down");

    private final ModulesScreen parent;

    public ModuleListWidget(ModulesScreen parent, Minecraft client,
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
    protected int scrollBarX() {
        return getRowLeft() + getRowWidth() + 6;
    }


    public class ModuleEntry
            extends ObjectSelectionList.Entry<ModuleEntry> {

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
        public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY,
                                   boolean hovered, float delta) {
            int x = getRowLeft();
            int y = getY();

            ctx.text(minecraft.font, module.type.getName(), x + 40, y + 4, 0xFFFFFFFF);

            String value = getPreviewValue();
            if (!value.isEmpty()) {
                WindRoseHud.drawModule(ctx, minecraft.font, module, value, x + 40, y + 18);
            }

            if (hovered) {
                int index = children().indexOf(this);

                if (index > 0) {
                    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_UP_SPRITE, x + 4, y, 36, 36);
                }

                if (index < getItemCount() - 1) {
                    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_SPRITE, x + 4, y, 36, 36);
                }
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            int index = children().indexOf(this);
            double relX = click.x() - getRowLeft();
            double relY = click.y() - getY();

            if (relX <= 24) {
                if (relY < 16 && index > 0) {
                    swap(index, index - 1);
                    return true;
                }
                if (relY >= 16 && index < getItemCount() - 1) {
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
            parent.updateButtonStates();
        }

        @Override
        public Component getNarration() {
            return Component.literal(module.type.getName());
        }
    }
}
