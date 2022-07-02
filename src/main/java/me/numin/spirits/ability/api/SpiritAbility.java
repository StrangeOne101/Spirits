package me.numin.spirits.ability.api;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import jdk.internal.joptsimple.internal.Strings;
import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.utilities.SpiritElement;
import org.bukkit.entity.Player;

public abstract class SpiritAbility extends ElementalAbility implements AddonAbility {

    public SpiritAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return SpiritElement.SPIRIT;
    }

    @Override
    public boolean isEnabled() {
        return Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral." + getName() + ".Enabled");
    }

    @Override
    public String getAuthor() {
        return Methods.getSpiritColor(Methods.SpiritType.NEUTRAL) + "" + Spirits.plugin.getDescription().getAuthors();
    }

    @Override
    public String getVersion() {
        return Methods.getSpiritColor(Methods.SpiritType.NEUTRAL) + Spirits.plugin.getDescription().getVersion();
    }

    @Override
    public void load() {}
    @Override
    public void stop() {}
}
