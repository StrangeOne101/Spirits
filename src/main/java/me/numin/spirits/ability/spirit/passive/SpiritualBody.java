package me.numin.spirits.ability.spirit.passive;

import com.projectkorra.projectkorra.ability.PassiveAbility;
import me.numin.spirits.Spirits;
import me.numin.spirits.ability.api.SpiritAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpiritualBody extends SpiritAbility implements PassiveAbility {

    public SpiritualBody(Player player) {
        super(player);
    }

    @Override
    public String getAbilityType() {
        return PASSIVE;
    }

    @Override
    public void progress() {}

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "SpiritualBody";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isInstantiable() {
        return false;
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    public static double getFallDamageModifier() {
        return Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.SpiritualBody.FallDamageModifier", 0.0);
    }
}
