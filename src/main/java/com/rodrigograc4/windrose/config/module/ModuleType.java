package com.rodrigograc4.windrose.config.module;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum ModuleType {
    DAY,
    COORDS,
    DIRECTION,
    TOTEMS,
    FPS,
    SPACER;

    /** The module's name from the lang file: windrose.module.<name in lower case>. */
    public Component displayName() {
        return Component.translatable("windrose.module." + name().toLowerCase(Locale.ROOT));
    }
}
