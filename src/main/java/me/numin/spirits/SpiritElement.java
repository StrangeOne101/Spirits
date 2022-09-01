package me.numin.spirits;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

public class SpiritElement extends Element {

    public static final SpiritElement NEUTRAL = new SpiritElement("Spirit", ChatColor.DARK_AQUA, "Neutral", 0x408fff);
    public static final SpiritElement LIGHT = new SpiritElement("LightSpirit", ChatColor.AQUA, "LightSpirit", 0xfffa63);
    public static final SpiritElement DARK = new SpiritElement("DarkSpirit", ChatColor.BLUE, "DarkSpirit", 0x4f00cf);

    private ChatColor defaultColor;
    private String configName;
    private int dust;
    private Color dustColor;

    public SpiritElement(String name, ChatColor defaultColor, String configName, int dustColor) {
        super(name, ElementType.NO_SUFFIX, Spirits.getInstance());
        this.defaultColor = defaultColor;
        this.configName = configName;
        this.dust = dustColor;
        this.dustColor = Color.fromRGB(dustColor);
    }

    public ChatColor getColor() {
        String color = ConfigManager.languageConfig.get().getString("Chat.Colors." + getName());
        return (color != null) ? ChatColor.of(color) : getDefaultColor();
    }

    public ChatColor getSubColor() {
        String color = ConfigManager.languageConfig.get().getString("Chat.Colors." + getColor() + "Sub");
        return (color != null) ? ChatColor.of(color) : ChatColor.WHITE;
    }

    public ChatColor getDefaultColor() {
        return defaultColor;
    }

    public int getDustHexColor() {
        return dust;
    }

    public String getConfigName() {
        return configName;
    }

    public Color getDustColor() {
        return dustColor;
    }
}
