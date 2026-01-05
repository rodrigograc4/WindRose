package com.rodrigograc4.windrose.config.module;

public class WindRoseModule {
    public ModuleType type;
    public boolean enabled = true;
    public int labelColor = 0xE7544D;
    public int valueColor = 0xFFFFFF;
    public String defaultLabel = "";
    public String customLabel = "";

    public WindRoseModule() {} 

    public WindRoseModule(ModuleType type) {
        this.type = type;
        this.defaultLabel = switch (type) {
            case DAY -> "Day: ";
            case COORDS -> "XYZ: ";
            case DIRECTION -> "↑ ";
            case TOTEMS -> "Totems Used: ";
            case FPS -> "FPS: ";
            default -> "";
        };
        this.customLabel = this.defaultLabel;
    }
}