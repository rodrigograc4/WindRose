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
    public HudPosition hudPosition = HudPosition.TOP_LEFT;
    public float padding = 3.0F;
    public float linepadding = 3.0F;

    // Mudança importante: Garantir que a lista é inicializada corretamente
    public List<WindRoseModule> activeModules = new ArrayList<>();

    @ConfigEntry.Gui.Excluded
    public Map<String, Integer> totemsPerWorld = new HashMap<>();

    public enum HudPosition { TOP_LEFT, HOTBAR_TOP }

    public static void init() {
        AutoConfig.register(WindRoseConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(WindRoseConfig.class).getConfig();
        
        // Verifica se a lista está nula (erro de carregamento) ou vazia
        if (INSTANCE.activeModules == null) {
            INSTANCE.activeModules = new ArrayList<>();
        }
        
        if (INSTANCE.activeModules.isEmpty()) {
            System.out.println("[WindRose] Lista vazia detetada. A adicionar módulos padrão...");
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
}