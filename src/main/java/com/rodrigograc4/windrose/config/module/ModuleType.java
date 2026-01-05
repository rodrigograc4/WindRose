package com.rodrigograc4.windrose.config.module;

public enum ModuleType {
    DAY("Day Counter"),
    COORDS("Coordinates"),
    DIRECTION("Direction"),
    TOTEMS("Totems Used"),
    FPS("FPS"),
    SPACER("Separator");

    private final String name;
    ModuleType(String name) { this.name = name; }
    public String getName() { return name; }
}