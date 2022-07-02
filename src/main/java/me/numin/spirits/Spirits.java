package me.numin.spirits;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.CollisionInitializer;
import me.numin.spirits.ability.dark.OldDarkBlast;
import me.numin.spirits.ability.dark.Shackle;
import me.numin.spirits.ability.dark.Strike;
import me.numin.spirits.ability.light.OldLightBlast;
import me.numin.spirits.ability.light.Shelter;
import me.numin.spirits.config.Config;
import me.numin.spirits.listeners.Abilities;
import me.numin.spirits.listeners.Passives;
import me.numin.spirits.listeners.PKEvents;
import me.numin.spirits.utilities.SpiritPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spirits extends JavaPlugin {

    public static Spirits plugin;

    @Override
    public void onEnable() {
        plugin = this;

        new Config(this);

        CoreAbility.registerPluginAbilities(plugin, "me.numin.spirits.ability");

        registerListeners();
        registerCollisions();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SpiritPlaceholder().register();
        }

        plugin.getLogger().info("Successfully enabled Spirits.");
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Successfully disabled Spirits.");
    }

    public static Spirits getInstance() {
        return plugin;
    }

    //TODO: collision system needs testing
    private void registerCollisions() {
        CollisionInitializer collisionInitializer = ProjectKorra.getCollisionInitializer();
        collisionInitializer.addSmallAbility(CoreAbility.getAbility(OldDarkBlast.class));
        collisionInitializer.addSmallAbility(CoreAbility.getAbility(Shackle.class));
        collisionInitializer.addSmallAbility(CoreAbility.getAbility(Strike.class));
        collisionInitializer.addSmallAbility(CoreAbility.getAbility(OldLightBlast.class));
        collisionInitializer.addLargeAbility(CoreAbility.getAbility(Shelter.class));
        collisionInitializer.initializeDefaultCollisions();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new Abilities(), this);
        getServer().getPluginManager().registerEvents(new Passives(), this);
        getServer().getPluginManager().registerEvents(new PKEvents(), this);
    }
}
