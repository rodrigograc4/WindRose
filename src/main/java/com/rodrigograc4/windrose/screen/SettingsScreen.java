package com.rodrigograc4.windrose.screen;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SettingsScreen {

    public static Screen create(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("WindRose Settings"));

        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory cat = builder.getOrCreateCategory(Text.literal("Settings"));

        cat.addEntry(
                eb.startBooleanToggle(
                        Text.literal("Deactivate mod"),
                        !WindRoseConfig.INSTANCE.statsEnabled
                )
                .setDefaultValue(false)
                .setSaveConsumer(v -> WindRoseConfig.INSTANCE.statsEnabled = !v)
                .build()
        );

        cat.addEntry(
                eb.startBooleanToggle(
                        Text.literal("Enable background"),
                        WindRoseConfig.INSTANCE.backgroundEnabled
                )
                .setDefaultValue(false)
                .setSaveConsumer(v -> WindRoseConfig.INSTANCE.backgroundEnabled = v)
                .build()
        );

        cat.addEntry(
        eb.startColorField(Text.literal("Background color"), WindRoseConfig.INSTANCE.backgroundColor & 0xFFFFFF) // pega só RGB
                .setDefaultValue(0x505050)
                .setSaveConsumer(rgb -> {
                WindRoseConfig.INSTANCE.backgroundColor = 0x6F000000 | (rgb & 0xFFFFFF);
                })
                .build()
        );

        cat.addEntry(
                eb.startFloatField(
                        Text.literal("Margin"),
                        WindRoseConfig.INSTANCE.margin
                )
                .setDefaultValue(1.0F)
                .setMin(0.0F)
                .setMax(5.0F)
                .setSaveConsumer(v -> WindRoseConfig.INSTANCE.margin = v)
                .build()
        );

        cat.addEntry(
                eb.startFloatField(
                        Text.literal("Line Padding"),
                        WindRoseConfig.INSTANCE.linePadding
                )
                .setDefaultValue(0.0F)
                .setMin(0.0F)
                .setMax(5.0F)
                .setSaveConsumer(v -> WindRoseConfig.INSTANCE.linePadding = v)
                .build()
        );

        builder.setSavingRunnable(WindRoseConfig::save);

        return builder.build();
    }
}
