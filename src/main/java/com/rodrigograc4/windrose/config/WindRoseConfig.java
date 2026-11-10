package com.rodrigograc4.windrose.config;

import com.rodrigograc4.windrose.WindRose;
import com.rodrigograc4.windrose.config.module.ModuleType;
import com.rodrigograc4.windrose.config.module.WindRoseModule;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Config(name = "windrose")
public class WindRoseConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static WindRoseConfig INSTANCE;

    public boolean statsEnabled = true;
    public boolean hideWithDebugScreen = false;
    // Whole pixels; floats only so configs saved by older versions still load.
    public float margin = 1.0F;
    public float linePadding = 0.0F;
    public boolean backgroundEnabled = false;
    // ARGB: the alpha is the background opacity.
    public int backgroundColor = 0x6F505050;
    public long dayCountOffset = 1L;
    public boolean showHours = true;
    public DirectionMode directionMode = DirectionMode.AXIS_CARDINAL;
    public LabelPosition labelPosition = LabelPosition.AFTER_VALUE;


    public List<WindRoseModule> activeModules = new ArrayList<>();

    // Set once the default modules have been added, so deleting every module sticks.
    @ConfigEntry.Gui.Excluded
    public boolean defaultModulesAdded = false;

    @ConfigEntry.Gui.Excluded
    public Map<String, Integer> totemsPerWorld = new HashMap<>();

    public static void init() {
        AutoConfig.register(WindRoseConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(WindRoseConfig.class).getConfig();
        
        if (INSTANCE.activeModules == null) {
            INSTANCE.activeModules = new ArrayList<>();
        }
        
        INSTANCE.addDefaultModulesOnce();
    }

    // Only on a fresh install (or the first start after updating to a version with this flag), so
    // a player who deletes every module doesn't get the defaults back on the next start.
    public void addDefaultModulesOnce() {
        if (defaultModulesAdded) return;

        if (activeModules.isEmpty()) {
            WindRose.LOGGER.info("Module list empty, adding default modules");
            activeModules.addAll(defaultModules());
        }
        defaultModulesAdded = true;
        save();
    }

    /** The HUD a fresh install starts with, top to bottom. */
    public static List<WindRoseModule> defaultModules() {
        return new ArrayList<>(List.of(
                new WindRoseModule(ModuleType.FPS),
                new WindRoseModule(ModuleType.TOTEMS),
                new WindRoseModule(ModuleType.SPACER),
                new WindRoseModule(ModuleType.DAY),
                new WindRoseModule(ModuleType.SPACER),
                new WindRoseModule(ModuleType.COORDS),
                new WindRoseModule(ModuleType.DIRECTION)));
    }

    public void incrementTotems(String worldName) {
        totemsPerWorld.merge(worldName, 1, Integer::sum);
        save();
    }

    // Moves stats saved under an old key to its new key, unless the new key already has stats
    public void migrateWorldKey(String legacyKey, String newKey) {
        if (legacyKey == null || totemsPerWorld.containsKey(newKey)) return;

        Integer totems = totemsPerWorld.remove(legacyKey);
        if (totems != null) {
            totemsPerWorld.put(newKey, totems);
            save();
        }
    }

    public int getTotemsForWorld(String worldName) {
        return totemsPerWorld.getOrDefault(worldName, 0);
    }

    public static void save() {
        AutoConfig.getConfigHolder(WindRoseConfig.class).save();
    }

    // Names are shown through the lang file: windrose.directionMode.<name in lower case>.
    public enum DirectionMode {
        CARDINAL,
        AXIS,
        AXIS_CARDINAL,
        CARDINAL_AXIS
    }

    public enum LabelPosition {
        BEFORE_VALUE,
        AFTER_VALUE
    }
}
