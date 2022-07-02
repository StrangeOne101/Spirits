package me.numin.spirits.utilities;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DummyAbility extends ElementalAbility {

    private CoreAbility parent;
    private String name;

    public DummyAbility(CoreAbility parent) {
        super(parent.getPlayer());

        this.parent = parent;
        this.name = parent.getName();
    }

    @Override
    public void progress() {}

    @Override
    public boolean isSneakAbility() {
        return parent.isSneakAbility();
    }

    @Override
    public boolean isHarmlessAbility() {
        return parent.isSneakAbility();
    }

    @Override
    public boolean isIgniteAbility() {
        return parent.isIgniteAbility();
    }

    @Override
    public boolean isExplosiveAbility() {
        return parent.isExplosiveAbility();
    }

    @Override
    public long getCooldown() {
        return parent.getCooldown();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Element getElement() {
        return parent.getElement();
    }

    @Override
    public Location getLocation() {
        return parent.getLocation();
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }

    public DummyAbility name(String name) {
        this.name = name;
        return this;
    }
}
