package com.rodrigograc4.windrose.util;

import com.rodrigograc4.windrose.screen.ModulesScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModulesScreen::new;
    }
}