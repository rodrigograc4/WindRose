package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class EditModuleScreen {

    public static Screen create(Screen parent, WindRoseModule module) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Edit Module: " + module.type.getName()));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Settings"));

        // Corrigido: Uso de addEntry em vez de add
        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), module.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> module.enabled = newValue)
                .build());

        category.addEntry(entryBuilder.startStrField(Text.literal("Custom Label"), module.customLabel)
                .setDefaultValue("")
                .setSaveConsumer(newValue -> module.customLabel = newValue)
                .build());

        category.addEntry(entryBuilder.startColorField(Text.literal("Label Color"), module.labelColor)
                .setDefaultValue(0xE7544D)
                .setSaveConsumer(newValue -> module.labelColor = newValue)
                .build());

        category.addEntry(entryBuilder.startColorField(Text.literal("Value Color"), module.valueColor)
                .setDefaultValue(0xFFFFFF)
                .setSaveConsumer(newValue -> module.valueColor = newValue)
                .build());

        builder.setSavingRunnable(WindRoseConfig::save);

        return builder.build();
    }
}