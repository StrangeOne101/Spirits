package me.numin.spirits.utilities;

import me.numin.spirits.ability.api.SpiritAbility;
import me.numin.spirits.ability.spirit.Possess;
import org.bukkit.Location;

public class PossessRecoil extends SpiritAbility {

    public PossessRecoil(Possess possess) {
        super(possess.getPlayer());
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }

    @Override
    public String getAbilityType() {
        return null;
    }

    @Override
    public void progress() {}

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
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
        return "PossessRecoil";
    }

    @Override
    public Location getLocation() {
        return null;
    }
}