package com.rodrigograc4.windrose.config.module;

public class WindRoseModule {
    public ModuleType type;
    public boolean enabled = true;
    public int labelColor = 0xE7544D;
    public int valueColor = 0xFFFFFF;
    public String customLabel = "";

    public WindRoseModule() {} 

    public WindRoseModule(ModuleType type) {
        this.type = type;
        this.customLabel = switch (type) {
            case COORDS -> "XYZ: ";
            case DAY -> "Day: ";
            case DIRECTION -> "↑ ";
            case TOTEMS -> "Totems: ";
            case FPS -> "FPS: ";
            default -> "";
        };
    }
}