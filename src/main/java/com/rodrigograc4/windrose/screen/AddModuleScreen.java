package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AddModuleScreen extends Screen {
    private final Screen parent;

    public AddModuleScreen(Screen parent) {
        super(Text.literal("Add New Module"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = 40;
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;

        for (ModuleType type : ModuleType.values()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal(type.getName()), b -> {
                WindRoseConfig.INSTANCE.activeModules.add(new WindRoseModule(type));
                WindRoseConfig.save();
                this.client.setScreen(this.parent);
            }).dimensions(x, y, buttonWidth, 20).build());
            
            y += 24;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 75, this.height - 30, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}