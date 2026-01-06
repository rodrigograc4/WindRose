package com.rodrigograc4.windrose.config.module;

public class WindRoseModule {

    public ModuleType type;
    public boolean enabled = true;
    public int labelColor = 0xE7544D;
    public int valueColor = 0xFFFFFF;
    public String defaultLabel = "";
    public String customLabel = "";

    public WindRoseModule() {
    }

    public WindRoseModule(ModuleType type) {
        this.type = type;
        applyDefaults();
    }

    public void applyDefaults() {
        if (type == null) return;

        if (defaultLabel == null || defaultLabel.isEmpty()) {
            defaultLabel = switch (type) {
                case DAY -> "Day:";
                case COORDS -> "XYZ:";
                case DIRECTION -> "↑";
                case TOTEMS -> "Totems Used:";
                case FPS -> "fps";
                default -> "";
            };
        }

        if (customLabel == null || customLabel.isEmpty()) {
            customLabel = defaultLabel;
        }

        if (labelColor == 0xE7544D || labelColor == 0x000000) {
            labelColor = defaultLabelColor(type);
        }

        if (valueColor == 0) {
            valueColor = 0xFFFFFF;
        }
    }

    public void resetToDefaults() {
        defaultLabel = "";
        customLabel = "";
        labelColor = 0xE7544D;
        valueColor = 0xFFFFFF;
        enabled = true;

        applyDefaults();
    }

    public static int defaultLabelColor(ModuleType type) {
        return switch (type) {
            case TOTEMS -> 0xF9D472;      // #f9d472
            case DAY -> 0xFF5555;         // #ff5555
            case COORDS -> 0xFFFFFF;      // #ffffff
            case DIRECTION -> 0x8FD3FE;   // #8fd3fe
            case FPS -> 0xFFFFFF;         // #ffffff
            default -> 0xFFFFFF;
        };
    }
}
