package com.rodrigograc4.windrose.config;

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
    public float margin = 1.0F;
    public float linePadding = 0.0F;
    public boolean backgroundEnabled = true;
    public int backgroundColor = 0x6F505050;
    public long dayCountOffset = 0L;
    public boolean showHours = false;
    public DirectionMode directionMode = DirectionMode.CARDINAL;


    public List<WindRoseModule> activeModules = new ArrayList<>();

    @ConfigEntry.Gui.Excluded
    public Map<String, Integer> totemsPerWorld = new HashMap<>();

    public static void init() {
        AutoConfig.register(WindRoseConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(WindRoseConfig.class).getConfig();
        
        if (INSTANCE.activeModules == null) {
            INSTANCE.activeModules = new ArrayList<>();
        }
        
        if (INSTANCE.activeModules.isEmpty()) {
            System.out.println("[WindRose] List empty detected. Adding default modules...");
            INSTANCE.activeModules.add(new WindRoseModule(ModuleType.DAY));
            INSTANCE.activeModules.add(new WindRoseModule(ModuleType.COORDS));
            INSTANCE.activeModules.add(new WindRoseModule(ModuleType.DIRECTION));
            save();
        }
    }

    public void incrementTotems(String worldName) {
        totemsPerWorld.merge(worldName, 1, Integer::sum);
        save();
    }

    public int getTotemsForWorld(String worldName) {
        return totemsPerWorld.getOrDefault(worldName, 0);
    }

    public static void save() {
        AutoConfig.getConfigHolder(WindRoseConfig.class).save();
    }

    public enum DirectionMode {
        CARDINAL,   // North / South / East / West
        AXIS        // +X / -X / +Z / -Z
    }
}