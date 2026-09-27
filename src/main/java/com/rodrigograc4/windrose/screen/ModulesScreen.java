package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.ModuleType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModulesScreen extends Screen {
    private final Screen parent;
    private ModuleListWidget list;
    private Button editButton;
    private Button deleteButton;

    public ModulesScreen(Screen parent) {
        super(Component.literal("WindRose"));
        this.parent = parent;
    }

    @Override
    public void added() {
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
                this.minecraft,
                this.width,
                listBottom,
                33,
                36
        );

        this.list.setX(0);
        this.list.setY(33);

        this.list.refreshList();
        this.addRenderableWidget(this.list);

        int buttonY = this.height - 52;

        this.deleteButton = this.addRenderableWidget(Button.builder(Component.literal("Delete"), b -> {
            var entry = list.getSelected();
            if (entry != null) {
                WindRoseConfig.INSTANCE.activeModules.remove(entry.module);
                list.refreshList();
                updateButtonStates();
            }
        }).bounds(this.width / 2 - 154, buttonY, 100, 20).build());

        this.editButton = this.addRenderableWidget(Button.builder(Component.literal("Edit"), b -> {
            var entry = list.getSelected();
            if (entry != null) {
                this.minecraft.gui.setScreen(EditModuleScreen.create(this, entry.module));
            }
        }).bounds(this.width / 2 - 50, buttonY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Add"),
                b -> this.minecraft.gui.setScreen(new AddModuleScreen(this))
        ).bounds(this.width / 2 + 54, buttonY, 100, 20).build());

        int bottomY = this.height - 28;
        int doneWidth = 153;
        int settingsWidth = 153;
        int gap = 4;

        int totalBottomWidth = settingsWidth + gap + doneWidth;
        int startX = this.width / 2 - totalBottomWidth / 2;

        this.addRenderableWidget(Button.builder(
                Component.literal("Settings"),
                b -> this.minecraft.gui.setScreen(SettingsScreen.create(this))
        ).bounds(startX, bottomY, settingsWidth, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                b -> this.onClose()
        ).bounds(startX + settingsWidth + gap, bottomY, doneWidth, 20).build());

        updateButtonStates();
    }

    // Also reached via ESC: saves the order/removals and returns to the parent screen
    @Override
    public void onClose() {
        WindRoseConfig.save();
        this.minecraft.gui.setScreen(this.parent);
    }

    public void updateButtonStates() {
        var entry = list.getSelected();

        this.editButton.active = entry != null && entry.module.type != ModuleType.SPACER;
        this.deleteButton.active = entry != null;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
    }
}
