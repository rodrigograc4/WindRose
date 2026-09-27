package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AddModuleScreen extends Screen {
    private final Screen parent;

    public AddModuleScreen(Screen parent) {
        super(Component.literal("Add New Module"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = 40;
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;

        for (ModuleType type : ModuleType.values()) {
            this.addRenderableWidget(Button.builder(Component.literal(type.getName()), b -> {
                WindRoseConfig.INSTANCE.activeModules.add(new WindRoseModule(type));
                WindRoseConfig.save();
                this.onClose();
            }).bounds(x, y, buttonWidth, 20).build());

            y += 24;
        }

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> this.onClose())
                .bounds(this.width / 2 - 75, this.height - 30, 150, 20).build());
    }

    // Goes back to the module list instead of closing every screen (e.g. when pressing ESC)
    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
    }
}
