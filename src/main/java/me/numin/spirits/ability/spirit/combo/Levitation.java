package me.numin.spirits.ability.spirit.combo;

import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.Spirits;
import me.numin.spirits.ability.api.SpiritAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Levitation extends SpiritAbility implements ComboAbility {

    //TODO: Implement sounds.

    private Location origin;

    @Attribute("EnableAgilityMultiplier")
    private boolean doAgilityMultiplier;
    @Attribute("EnableLevitationMultiplier")
    private boolean doLevitationMultiplier;
    @Attribute("EnablePhaseMultiplier")
    private boolean doPhaseMultiplier;
    private boolean wasFlying, canFly;
    private double allowedHealthLoss, initialHealth, oldFlyspeed;
    @Attribute("AgilityMultiplier")
    private double agilityMultiplier;
    @Attribute("LevitationMultiplier")
    private double levitationMultiplier;
    @Attribute("PhaseMultiplier")
    private double phaseMultiplier;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;

    public Levitation(Player player) {
        super(player);

        if (!bPlayer.canBendIgnoreBinds(this) || CoreAbility.hasAbility(player, Levitation.class)) return;

        player.teleport(player.getLocation().add(0, 0.3, 0));
        setFields();
        start();
    }

    private void setFields() {
        //Config
        this.cooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Combo.Levitation.Cooldown");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Combo.Levitation.Duration");
        this.range = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Combo.Levitation.Range");
        this.allowedHealthLoss = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Combo.Levitation.AllowedHealthLoss");
        this.doAgilityMultiplier = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Enabled");
        this.agilityMultiplier = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Multiplier");
        this.doPhaseMultiplier = false; //Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Enabled");
        this.phaseMultiplier = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Multiplier");
        this.doLevitationMultiplier = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Enabled");
        this.levitationMultiplier = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Multiplier");

        this.wasFlying = player.isFlying();
        this.canFly = player.getAllowFlight();
        this.origin = player.getLocation();
        this.initialHealth = player.getHealth();
        this.oldFlyspeed = player.getFlySpeed();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBinds(this) ||
                origin.distanceSquared(player.getLocation()) > range * range ||
                (player.getHealth() < (initialHealth - allowedHealthLoss)) ||
                System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.1F);
        this.playParticles();
    }

    private void playParticles() {
        Location location = player.getLocation().add(0, 1, 0);
        location.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 1, 0.3, 0.3, 0.3, 0.01);
        Methods.playSpiritParticles(player, location, 0.4, 0.6, 0.4, 0, 1);
    }

    @Override
    public void remove() {
        player.setFlying(wasFlying);
        player.setAllowFlight(canFly);
        player.setFlySpeed((float) oldFlyspeed);
        long duration = System.currentTimeMillis() - getStartTime();
        if (doAgilityMultiplier) {
            long agilityCooldown = (long) (duration * agilityMultiplier);
            bPlayer.addCooldown("Dash", agilityCooldown);
            bPlayer.addCooldown("Soar", agilityCooldown);
        }
        if (doPhaseMultiplier) {
            long phaseCooldown = (long) (duration * phaseMultiplier);
            bPlayer.addCooldown("Phase", phaseCooldown);
        }
        if (doLevitationMultiplier) {
            long levitationCooldown = (long) (duration * levitationMultiplier) + cooldown;
            bPlayer.addCooldown("Levitation", levitationCooldown);
        } else {
            bPlayer.addCooldown(this, cooldown);
        }
        super.remove();
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Levitation(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<AbilityInformation> combo = new ArrayList<AbilityInformation>();
        combo.add(new ComboManager.AbilityInformation("Agility", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("Agility", ClickType.SHIFT_DOWN));
        combo.add(new ComboManager.AbilityInformation("Vanish", ClickType.LEFT_CLICK));
        return combo;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public String getName() {
        return "Levitation";
    }

    @Override
    public String getAbilityType() {
        return UTILITY;
    }

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
}