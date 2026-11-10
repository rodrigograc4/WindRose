package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingsScreen {

    private static final int DEFAULT_BACKGROUND = 0x6F505050;

    public static Screen create(Screen parent) {
        WindRoseConfig config = WindRoseConfig.INSTANCE;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("windrose.settings.title"));

        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("windrose.settings.category"));

        cat.addEntry(eb.startBooleanToggle(Component.translatable("windrose.settings.showHud"), config.statsEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> config.statsEnabled = v)
                .build());

        cat.addEntry(eb.startBooleanToggle(Component.translatable("windrose.settings.hideWithDebugScreen"), config.hideWithDebugScreen)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("windrose.settings.hideWithDebugScreen.tooltip"))
                .setSaveConsumer(v -> config.hideWithDebugScreen = v)
                .build());

        cat.addEntry(eb.startBooleanToggle(Component.translatable("windrose.settings.background"), config.backgroundEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(v -> config.backgroundEnabled = v)
                .build());

        // Color and opacity are saved into the same ARGB value, each keeping the other's part.
        cat.addEntry(eb.startColorField(Component.translatable("windrose.settings.backgroundColor"), config.backgroundColor & 0xFFFFFF)
                .setDefaultValue(DEFAULT_BACKGROUND & 0xFFFFFF)
                .setSaveConsumer(rgb -> config.backgroundColor = (config.backgroundColor & 0xFF000000) | (rgb & 0xFFFFFF))
                .build());

        cat.addEntry(eb.startIntSlider(Component.translatable("windrose.settings.backgroundOpacity"), alphaToPercent(config.backgroundColor >>> 24), 0, 100)
                .setDefaultValue(alphaToPercent(DEFAULT_BACKGROUND >>> 24))
                .setTextGetter(percent -> Component.literal(percent + "%"))
                .setSaveConsumer(percent -> config.backgroundColor = (percentToAlpha(percent) << 24) | (config.backgroundColor & 0xFFFFFF))
                .build());

        cat.addEntry(eb.startIntSlider(Component.translatable("windrose.settings.margin"), Math.round(config.margin), 0, 5)
                .setDefaultValue(1)
                .setSaveConsumer(v -> config.margin = v)
                .build());

        cat.addEntry(eb.startIntSlider(Component.translatable("windrose.settings.linePadding"), Math.round(config.linePadding), 0, 5)
                .setDefaultValue(0)
                .setSaveConsumer(v -> config.linePadding = v)
                .build());

        builder.setSavingRunnable(WindRoseConfig::save);

        return builder.build();
    }

    private static int alphaToPercent(int alpha) {
        return Math.round(alpha * 100 / 255.0F);
    }

    private static int percentToAlpha(int percent) {
        return Math.round(percent * 255 / 100.0F);
    }
}
