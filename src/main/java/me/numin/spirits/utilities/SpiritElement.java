package me.numin.spirits.utilities;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.ChatColor;
import me.numin.spirits.Spirits;

public class SpiritElement extends Element {

    public static final SpiritElement SPIRIT = new SpiritElement("Spirit", ChatColor.DARK_AQUA);
    public static final SpiritElement LIGHT_SPIRIT = new SpiritElement("LightSpirit", ChatColor.AQUA);
    public static final SpiritElement DARK_SPIRIT = new SpiritElement("DarkSpirit", ChatColor.BLUE);

    private ChatColor defaultColor;

    public SpiritElement(String name, ChatColor defaultColor) {
        super(name, ElementType.NO_SUFFIX, Spirits.getInstance());
        this.defaultColor = defaultColor;
    }

    public ChatColor getColor() {
        String color = ConfigManager.languageConfig.get().getString("Chat.Colors." + getName());
        return (color != null) ? ChatColor.valueOf(color) : getDefaultColor();
    }

    public ChatColor getSubColor() {
        String color = ConfigManager.languageConfig.get().getString("Chat.Colors." + getColor() + "Sub");
        return (color != null) ? ChatColor.valueOf(color) : ChatColor.WHITE;
    }

    public ChatColor getDefaultColor() {
        return defaultColor;
    }
}
