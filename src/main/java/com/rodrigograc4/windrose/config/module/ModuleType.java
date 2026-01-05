package com.rodrigograc4.windrose.config.module;

public enum ModuleType {
    COORDS("Coordinates"),
    DAY("Day Counter"),
    DIRECTION("Direction"),
    TOTEMS("Totems Popped"),
    FPS("FPS"),
    SPACER("Separator");

    private final String name;
    ModuleType(String name) { this.name = name; }
    public String getName() { return name; }
}