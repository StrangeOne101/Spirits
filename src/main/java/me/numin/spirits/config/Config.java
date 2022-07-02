package me.numin.spirits.config;

import me.numin.spirits.SpiritElement;
import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.configuration.ConfigManager;

import me.numin.spirits.Spirits;

public class Config {

    private static Spirits plugin;

    public Config(Spirits plugin) {
        Config.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = Spirits.plugin.getConfig();
        FileConfiguration language = ConfigManager.languageConfig.get();

        //Rank configuration
        language.addDefault("Chat.Colors.Spirit", SpiritElement.NEUTRAL.getDefaultColor().name());
        language.addDefault("Chat.Colors.SpiritSub", "DARK_PURPLE");
        language.addDefault("Chat.Colors.LightSpirit", SpiritElement.LIGHT.getDefaultColor().name());
        language.addDefault("Chat.Colors.LightSpiritSub", "WHITE");
        language.addDefault("Chat.Colors.DarkSpirit", SpiritElement.DARK.getDefaultColor().name());
        language.addDefault("Chat.Colors.DarkSpiritSub", "DARK_GRAY");
        language.addDefault("Chat.Prefixes.Spirit", "[Spirit]");
        language.addDefault("Chat.Prefixes.LightSpirit", "[LightSpirit]");
        language.addDefault("Chat.Prefixes.DarkSpirit", "[DarkSpirit]");

        //Descriptions & Instructions
        config.addDefault("Language.Abilities.Spirit.Agility.Description", "This ability offers you 2 modes of mobility. The first being the ability to dash forward very quickly. The second being the ability to soar through the skies as if gravity is non-existant.");
        config.addDefault("Language.Abilities.Spirit.Agility.Instructions", "Left-Click: Dash ⏐ Hold shift: Soar");

        config.addDefault("Language.Abilities.Spirit.Levitation.Description", "Spirits lack any earthly tethers which allow themselves to detach and enter the void, empty and become wind. Using this combo takes a toll on your essence and may weaken your mobility for a time.");
        config.addDefault("Language.Abilities.Spirit.Levitation.Instructions", "Agility (Left-click) > Agility (Hold shift) > Vanish (Left-click)");

        config.addDefault("Language.Abilities.Spirit.Possess.Description", "A very ancient ability of Spirits is the ability to jump inside the body of a human. As a Spirit, you'll be able to possess a human for a short amount of time and do harm. While this ability is active, both the Spirits and the targets movement will be hindered.");
        config.addDefault("Language.Abilities.Spirit.Possess.Instructions", "Hold shift and look at a human.");

        config.addDefault("Language.Abilities.Spirit.Phase.Description", "This advanced combo allows a Spirit to dematerialize into a state where they can walk through walls and fly around a certain radius. They are able to do this because of their unqiue molecular makeup not seen in any other being!");
        config.addDefault("Language.Abilities.Spirit.Phase.Instructions", "Vanish (Left-click 2x) > Possess (Tap-shift) > Vanish (Left-click) ⏐ To Exit: Vanish (Tap-shift)");

        config.addDefault("Language.Abilities.Spirit.Vanish.Description", "Spirits are often seen disappearing into thin air and then reappearing somewhere different. With this ability, you can harness that power as well! However, there is a certain duration you are able to vanish for an a radius of how far away from your original location you're allowed to get!");
        config.addDefault("Language.Abilities.Spirit.Vanish.Instructions", "Hold shift: Disappear ⏐ Release shift: Reappear");

        config.addDefault("Language.Abilities.LightSpirit.Alleviate.Description", "The healing ability for LightSpirits, this allows you to heal yourself and others! When healing, whoever is being healed will be removed of ANY negative potion effects aswell as recieve regeneration for a period of time.");
        config.addDefault("Language.Abilities.LightSpirit.Alleviate.Instructions", "Hold Shift while lookat at a target: Heal them ⏐ Hold Shift while looking away: Heal yourself.");

        config.addDefault("Language.Abilities.LightSpirit.OldLightBlast.Description", "Shoot a radiating blast of light towards your opponent!");
        config.addDefault("Language.Abilities.LightSpirit.OldLightBlast.Instructions", "Left-click to shoot the blast ⏐ Tap-shift to select a target then left-click to shoot a different kind of blast.");

        config.addDefault("Language.Abilities.LightSpirit.Rejuvenate.Description", "After executing the combo sequence, you will mark the ground with positively charged spiritual energy for a duration of time. Entities can come to this location to heal themselves, but dark creatures must beware!");
        config.addDefault("Language.Abilities.LightSpirit.Rejuvenate.Instructions", "Alleviate (Shift down) > Alleviate (Right-Click block) > Alleviate (Release shift)");

        config.addDefault("Language.Abilities.LightSpirit.Orb.Description", "Plant an orb of positive energy on the ground which awaits for oncoming entities. If the orb detects something moving past it, it'll expand and harm anyone in its wake! More features to come...");
        config.addDefault("Language.Abilities.LightSpirit.Orb.Instructions", "Hold shift until you see particles. Release shift while looking at an area nearby on the ground to plant it there.");

        config.addDefault("Language.Abilities.DarkSpirit.Intoxicate.Description", "Sacrifice some of your energy to pour a bit of chaos into the souls of your nearby enemies by taking away their positive potion effects and adding negative ones. Then watch as it destroys them from the inside out! The great spirit Vaatu was known to have this influence over other unbalanced Spirits.");
        config.addDefault("Language.Abilities.DarkSpirit.Intoxicate.Instructions", "Hold shift");

        config.addDefault("Language.Abilities.LightSpirit.Shelter.Description", "A very useful tactic when group battling, a light spirit can temporarily shield a friend or even a foe from incoming enemies. Additionally, they have the options to shield themselves!");
        config.addDefault("Language.Abilities.LightSpirit.Shelter.Instructions", "Left click: Shield others ⏐ Hold shift: Shield yourself.");

        config.addDefault("Language.Abilities.DarkSpirit.OldDarkBlast.Description", "A basic offensive ability for DarkSpirits. You have the choice of shooting a basic blast for dealing quick damage. Or you can select a target and shoot a slower, powerful, homing blast. The blasts can be dodged and obstructed so be strategic!");
        config.addDefault("Language.Abilities.DarkSpirit.OldDarkBlast.Instructions", "Left click: Quick blast ⏐ Tap-shift while looking at an entity: Select entity > Hold shift: Move the homing blast.");

        config.addDefault("Language.Abilities.DarkSpirit.Infest.Description", "After executing the combo sequence, you will mark the ground with negatively charged spiritual energy for a duration of time. Monsters can come to this location for strength, but any other entities must beware!");
        config.addDefault("Language.Abilities.DarkSpirit.Infest.Instructions", "Intoxicate (Shift down) > Intoxicate (Right-Click block) > Intoxicate (Release shift)");

        config.addDefault("Language.Abilities.DarkSpirit.Shackle.Description", "With this technique a DarkSpirit is able to temporarily trap an anyone dead in their tracks, even if you can't see them! Useful for a quick get away...");
        config.addDefault("Language.Abilities.DarkSpirit.Shackle.Instructions", "Left click");

        config.addDefault("Language.Abilities.DarkSpirit.Strike.Description", "The most basic ability of an aggressive, unbalanced Spirit is to rush towards their enemy and try to bite them in 1 swift motion. When you activate this ability, you'll see your target zone. If your target zone gets in range of another entity, you'll be rushed over to them an deal damage.");
        config.addDefault("Language.Abilities.DarkSpirit.Strike.Instructions", "Left-Click to trigger target spectacle");
        config.addDefault("Language.Errors.ChooseSpirit", "You cannot choose the Spirit element on its own! Please select a Dark or Light Spirit instead!");

        //Ability configuration
        config.addDefault("Abilities.Spirits.Neutral.Agility.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Agility.Dash.Cooldown", 2000);
        config.addDefault("Abilities.Spirits.Neutral.Agility.Dash.Distance", 3);
        config.addDefault("Abilities.Spirits.Neutral.Agility.Soar.Cooldown", 4500);
        config.addDefault("Abilities.Spirits.Neutral.Agility.Soar.Duration", 1000);
        config.addDefault("Abilities.Spirits.Neutral.Agility.Soar.Speed", 0.8);

        config.addDefault("Abilities.Spirits.Neutral.Possess.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Possess.Cooldown", 5000);
        config.addDefault("Abilities.Spirits.Neutral.Possess.Range", 5);
        config.addDefault("Abilities.Spirits.Neutral.Possess.Damage", 5);
        config.addDefault("Abilities.Spirits.Neutral.Possess.Duration", 2000);

        config.addDefault("Abilities.Spirits.Neutral.Vanish.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.Cooldown", 7000);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.Duration", 10000);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.ChargeTime", 1500);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.Range", 20);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.Radius", 10);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.ParticleFrequency", 5);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.RemoveFire", true);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.DivideRange.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.DivideRange.HealthRequired", 10);
        config.addDefault("Abilities.Spirits.Neutral.Vanish.DivideRange.DivideFactor", 2);

        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.Cooldown", 10000);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.Duration", 6000);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.Range", 10);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AllowedHealthLoss", 4);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Multiplier", 2);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Multiplier", 4);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Multiplier", 3);

        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.Enabled", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.CooldownMultiplier", 4);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.Duration", 10000);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.Range", 10);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.MinHealth", 6);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.Vanish.ApplyCooldown", true);
        config.addDefault("Abilities.Spirits.Neutral.Combo.Phase.Vanish.CooldownMultiplier", 4);

        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Enabled", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Others.Cooldown", 5000);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Others.Range", 5);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Others.PotionInterval", 2000);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Others.HealInterval", 5000);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Others.SelfDamage", 6);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Self.Cooldown", 5000);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Self.ChargeTime", 2000);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Self.HealDuration", 1.5);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Self.NightVisionDuration", 1.5);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.Self.RemoveNegativePotionEffects", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Red", 255);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Green", 255);
        config.addDefault("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Blue", 255);

        //TODO: Re-enable when finalized
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.Enabled", false);
        //TODO: Define a cooldown
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.Cooldown", 0);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.Controllable", false);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.Damage", 2);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.Range", 10);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.SelectionDuration", 2000);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.PotionDuration", 10);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.PotionPower", 1);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.FirstBlastSpeed", 1);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.SecondBlastSpeed", 0.2);
        config.addDefault("Abilities.Spirits.LightSpirit.OldLightBlast.BlastRadius", 2);

        config.addDefault("Abilities.Spirits.LightSpirit.Orb.Enabled", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.Cooldown", 10000);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.ChargeTime", 2000);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.Duration", 30000);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.WarmUpTime", 500);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.Damage", 3);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.PlaceRange", 20);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.DetonateRange", 3);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.EffectRange", 5);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.BlindnessDuration", 120);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.NauseaDuration", 300);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.PotionPower", 2);
        config.addDefault("Abilities.Spirits.LightSpirit.Orb.RequireGround", true);

        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Enabled", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.RemoveOnDamage", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Duration", 7000);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.ClickDelay", 2000);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.Cooldown", 10000);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Self.Cooldown", 10000);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.Radius", 5);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Self.Radius", 4);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.KnockbackPower", 1);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Self.KnockbackPower", 1);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.Range", 10);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Enabled", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Range", 5);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Self.BlockArrows", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Shelter.Others.BlockArrows", true);

        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Enabled", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Cooldown", 15000);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Duration", 10000);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Radius", 5);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Damage", 1);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.EffectInterval", 10);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtDarkSpirits", true);
        config.addDefault("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtMonsters", true);

        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Enabled", true);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Cooldown", 5000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Range", 5);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.PotionInterval", 2000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.HarmInterval", 5000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.SelfDamage", 4);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.BlastSpeed", 0.5);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Red", 255);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Green", 0);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Blue", 0);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherDuration", 5);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherPower", 1);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerDuration", 50);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerPower", 1);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionDuration", 15);
        config.addDefault("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionPower", 1);

        //TODO: Re-enable when finalized
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.Enabled", false);
        //TODO: Define a cooldown amount.
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.Cooldown", 0);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.Controllable", false);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.Damage", 4);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.Range", 10);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.DurationOfSelection", 2000);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.PotionDuration", 5);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.PotionPower", 1);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.FirstBlastSpeed", 1);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.SecondBlastSpeed", 0.2);
        config.addDefault("Abilities.Spirits.DarkSpirit.OldDarkBlast.BlastRadius", 2);

        config.addDefault("Abilities.Spirits.DarkSpirit.Shackle.Enabled", true);
        config.addDefault("Abilities.Spirits.DarkSpirit.Shackle.Cooldown", 5000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Shackle.Duration", 2500);
        config.addDefault("Abilities.Spirits.DarkSpirit.Shackle.Range", 20);
        config.addDefault("Abilities.Spirits.DarkSpirit.Shackle.Radius", 2);

        config.addDefault("Abilities.Spirits.DarkSpirit.Strike.Enabled", true);
        config.addDefault("Abilities.Spirits.DarkSpirit.Strike.Cooldown", 4000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Strike.Range", 5);
        config.addDefault("Abilities.Spirits.DarkSpirit.Strike.Damage", 3);
        config.addDefault("Abilities.Spirits.DarkSpirit.Strike.Radius", 1);

        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.Enabled", true);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.Cooldown", 15000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.Duration", 10000);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.Radius", 8);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.EffectInterval", 10);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.Damage", 1);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.DamageEntities", true);
        config.addDefault("Abilities.Spirits.DarkSpirit.Combo.Infest.HealDarkSpirits", true);

        ConfigManager.languageConfig.save();
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}