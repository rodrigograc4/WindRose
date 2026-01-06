package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.module.ModuleType;
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

        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), module.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> module.enabled = newValue)
                .build());

        category.addEntry(entryBuilder.startStrField(Text.literal("Custom Label"), module.customLabel)
                .setDefaultValue(module.defaultLabel)
                .setSaveConsumer(newValue -> module.customLabel = newValue)
                .build());

        category.addEntry(entryBuilder.startColorField(
                Text.literal("Label Color"),
                module.labelColor
        )
        .setDefaultValue(WindRoseModule.defaultLabelColor(module.type))
        .setSaveConsumer(newValue -> module.labelColor = newValue)
        .build());

        category.addEntry(entryBuilder.startColorField(Text.literal("Value Color"), module.valueColor)
                .setDefaultValue(0xFFFFFF)
                .setSaveConsumer(newValue -> module.valueColor = newValue)
                .build());

        // === Module-specific options ===
        if (module.type == ModuleType.DAY) {
                category.addEntry(entryBuilder.startIntSlider(
                        Text.literal("Day Count Offset"), 
                        (int) WindRoseConfig.INSTANCE.dayCountOffset,
                        0,
                        1 
                )
                .setDefaultValue(1)
                .setSaveConsumer(newValue -> WindRoseConfig.INSTANCE.dayCountOffset = newValue)
                .build());



                category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Hours"), WindRoseConfig.INSTANCE.showHours)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> WindRoseConfig.INSTANCE.showHours = newValue)
                    .build());
        }

        if (module.type == ModuleType.DIRECTION) {
                category.addEntry(entryBuilder.startEnumSelector(Text.literal("Direction Mode"), WindRoseConfig.DirectionMode.class, WindRoseConfig.INSTANCE.directionMode)
                    .setDefaultValue(WindRoseConfig.DirectionMode.CARDINAL)
                    .setSaveConsumer(newValue -> WindRoseConfig.INSTANCE.directionMode = newValue)
                    .build());
        }

        builder.setSavingRunnable(WindRoseConfig::save);

        return builder.build();
    }
}
