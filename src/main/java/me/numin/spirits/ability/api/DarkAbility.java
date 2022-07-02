package me.numin.spirits.ability.api;

import com.projectkorra.projectkorra.Element;
import me.numin.spirits.SpiritElement;
import org.bukkit.entity.Player;

public abstract class DarkAbility extends SpiritAbility {

    public DarkAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return SpiritElement.DARK;
    }
}
