package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModulesScreen extends Screen {
    private final Screen parent;
    private ModuleListWidget list;
    private ButtonWidget editButton;
    private ButtonWidget deleteButton;

    public ModulesScreen(Screen parent) {
        super(Text.literal("WindRose"));
        this.parent = parent;
    }

    @Override
    public void onDisplayed() {
        if (this.list != null) {
            this.list.refreshList();
            updateButtonStates();
        }
    }

    @Override
    protected void init() {
        int listBottom = this.height - 93;

        this.list = new ModuleListWidget(
                this,
                this.client,
                this.width,
                listBottom,
                33,
                36
        );

        this.list.setX(0);
        this.list.setY(33);
        
        this.list.refreshList();
        this.addDrawableChild(this.list);

        int buttonY = this.height - 52;

        this.deleteButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), b -> {
            if (list.getSelectedOrNull() != null) {
                WindRoseConfig.INSTANCE.activeModules.remove(list.getSelectedOrNull().module);
                list.refreshList();
                updateButtonStates();
            }
        }).dimensions(this.width / 2 - 154, buttonY, 100, 20).build());

        this.editButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit"), b -> {
            var entry = list.getSelectedOrNull();
            if (entry != null) {
                this.client.setScreen(EditModuleScreen.create(this, entry.module));
            }
        }).dimensions(this.width / 2 - 50, buttonY, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), b -> {
            this.client.setScreen(new AddModuleScreen(this));
        }).dimensions(this.width / 2 + 54, buttonY, 100, 20).build());

        int totalWidth = 310;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> {
            WindRoseConfig.save();
            this.client.setScreen(this.parent);
        }).dimensions(
                this.width / 2 - totalWidth / 2,
                this.height - 28,
                totalWidth,
                20
        ).build());

        updateButtonStates();
    }

    public void updateButtonStates() {
        var entry = list.getSelectedOrNull();

        boolean canEdit =
                entry != null &&
                entry.module.type != com.rodrigograc4.windrose.config.module.ModuleType.SPACER;

        this.editButton.active = canEdit;
        this.deleteButton.active = entry != null;
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFFFF);
    }
}