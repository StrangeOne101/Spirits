package me.numin.spirits.ability.api;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import me.numin.spirits.Spirits;
import me.numin.spirits.SpiritElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class SpiritAbility extends ElementalAbility implements AddonAbility {

    public static final String OFFENSE = "Offense";
    public static final String DEFENSE = "Defense";
    public static final String MOBILITY = "Mobility";
    public static final String UTILITY = "Utility";


    public SpiritAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return SpiritElement.NEUTRAL;
    }

    @Override
    public boolean isEnabled() {
        String combo = this instanceof ComboAbility ? ".Combo" : "";
        SpiritElement se = ((SpiritElement)this.getElement());
        if (se == null) {
            System.out.println("Null on " + getName());
            return false;
        }
        //System.out.println("Abilities.Spirits." + se.getConfigName() + combo + "." + getName() + ".Enabled");
        return Spirits.plugin.getConfig().getBoolean("Abilities.Spirits." + se.getConfigName() + combo + "." + getName() + ".Enabled");
    }

    @Override
    public String getAuthor() {
        return this.getElement().getColor().toString() + "" + Spirits.plugin.getDescription().getAuthors();
    }

    @Override
    public String getVersion() {
        return this.getElement().getColor().toString() + Spirits.plugin.getDescription().getVersion();
    }

    @Override
    public String getDescription() {
        String combo = this instanceof ComboAbility ? " Combo" : "";
        return ChatColor.BOLD + getAbilityType() + combo + ": " + ChatColor.WHITE +
                Spirits.plugin.getConfig().getString("Language.Abilities." + getElement().getName() + "." + getName() + ".Description");
    }

    @Override
    public String getInstructions() {
        return this.getElement().getColor().toString() +
                Spirits.plugin.getConfig().getString("Language.Abilities." + this.getElement().getName() + "." + getName() + ".Instructions");
    }

    @Override
    public void load() {}
    @Override
    public void stop() {}

    public abstract String getAbilityType();
}
