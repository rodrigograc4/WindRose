package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.config.WindRoseConfig.DirectionMode;
import com.rodrigograc4.windrose.config.WindRoseConfig.LabelPosition;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class EditModuleScreen {

    public static Screen create(Screen parent, WindRoseModule module) {
        WindRoseConfig config = WindRoseConfig.INSTANCE;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("windrose.edit.title", module.type.displayName()));

        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("windrose.settings.category"));

        category.addEntry(eb.startBooleanToggle(Component.translatable("windrose.edit.enabled"), module.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> module.enabled = v)
                .build());

        category.addEntry(eb.startStrField(Component.translatable("windrose.edit.customLabel"), module.customLabel)
                .setDefaultValue(module.defaultLabel)
                .setSaveConsumer(v -> module.customLabel = v)
                .build());

        category.addEntry(eb.startColorField(Component.translatable("windrose.edit.labelColor"), module.labelColor)
                .setDefaultValue(WindRoseModule.defaultLabelColor(module.type))
                .setSaveConsumer(v -> module.labelColor = v)
                .build());

        category.addEntry(eb.startColorField(Component.translatable("windrose.edit.valueColor"), module.valueColor)
                .setDefaultValue(0xFFFFFF)
                .setSaveConsumer(v -> module.valueColor = v)
                .build());

        if (module.type == ModuleType.DAY) {
            // Vanilla's first day is day 0; most players expect it to be day 1.
            category.addEntry(eb.startBooleanToggle(Component.translatable("windrose.edit.startAtDayOne"), config.dayCountOffset > 0)
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("windrose.edit.startAtDayOne.tooltip"))
                    .setSaveConsumer(v -> config.dayCountOffset = v ? 1L : 0L)
                    .build());

            category.addEntry(eb.startBooleanToggle(Component.translatable("windrose.edit.showHours"), config.showHours)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> config.showHours = v)
                    .build());
        }

        if (module.type == ModuleType.DIRECTION) {
            category.addEntry(eb.startEnumSelector(Component.translatable("windrose.edit.directionMode"), DirectionMode.class, config.directionMode)
                    .setDefaultValue(DirectionMode.CARDINAL)
                    .setEnumNameProvider(mode -> enumName("windrose.directionMode.", mode))
                    .setSaveConsumer(v -> config.directionMode = v)
                    .build());
        }

        if (module.type == ModuleType.FPS) {
            category.addEntry(eb.startEnumSelector(Component.translatable("windrose.edit.labelPosition"), LabelPosition.class, config.labelPosition)
                    .setDefaultValue(LabelPosition.AFTER_VALUE)
                    .setEnumNameProvider(position -> enumName("windrose.labelPosition.", position))
                    .setSaveConsumer(v -> config.labelPosition = v)
                    .build());
        }

        builder.setSavingRunnable(WindRoseConfig::save);

        return builder.build();
    }

    private static Component enumName(String prefix, Enum<?> value) {
        return Component.translatable(prefix + value.name().toLowerCase(Locale.ROOT));
    }
}
