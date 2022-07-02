package me.numin.spirits.ability.spirit;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.projectkorra.projectkorra.attribute.Attribute;

import com.projectkorra.projectkorra.util.ActionBar;
import me.numin.spirits.utilities.DummyAbility;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.DamageHandler;

import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.ability.api.SpiritAbility;
import org.bukkit.util.Vector;

public class Possess extends SpiritAbility {

    //TODO: Test how it interacts with sudden change in pathway (like the spawning of a RaiseEarth that obstructs it's path)
    //TODO: Test how it interacts with abilities like AirShield and Shelter.
    //TODO: Add configurable speed for the armor stand/blast feature.

    private static Map<UUID, Possess> VICTIMS = new HashMap<>();

    private ArmorStand armorStand;
    private DustOptions purple = new DustOptions(Color.fromRGB(130, 0, 193), 1);
    private LivingEntity target;
    private GameMode originalGameMode;
    private Location blast, playerOrigin;
    private Vector vector = new Vector(1, 0, 0);

    private boolean hasStarted = false, traveling, wasFlying;
    private double minDamage;
    @Attribute("SelfDamage")
    private double selfDamage;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("Durability")
    private int durability;

    private int breakingDurability; //The counter used to determine when the possession is broken.

    //Sound effect
    private final float pitch = 0F;
    private final float volume = 0.1F;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    private long possessStartTime, chargeStartTime;

    private String possessString;
    private String possessLeaveString;

    public Possess(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) {
            return;
        }

        setFields();
        start();
    }

    private void setFields() {
        this.cooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Possess.Cooldown");
        this.range = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.Range");
        this.damage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.MaxDamage");
        this.minDamage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.MinDamage");
        this.selfDamage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.FailureSelfDamage");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Possess.Duration");
        this.chargeTime = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Possess.ChargeTime");
        this.durability = Spirits.plugin.getConfig().getInt("Abilities.Spirits.Neutral.Possess.Durability", 8);
        this.playerOrigin = player.getLocation().add(0, 1, 0);
        this.possessString = ChatColor.translateAlternateColorCodes('&',
                Spirits.plugin.getConfig().getString("Language.Abilities.Spirit.Possess.Possessed"));
        this.possessLeaveString = ChatColor.translateAlternateColorCodes('&',
                Spirits.plugin.getConfig().getString("Language.Abilities.Spirit.Possess.PossessionBroken"));
        this.traveling = true;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (!hasStarted) {
            if (!player.isSneaking()) {
                remove();
                return;
            }

            //We do this here so the range can be changed with attributes AND so they don't have to re-press
            //sneak to select an entity again if they fail to select one.
            Entity entity = GeneralMethods.getTargetedEntity(player, range);
            if (entity instanceof LivingEntity) {
                this.target = (LivingEntity) entity;

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.3F, -1);
                Methods.animateVanish(player);

                this.armorStand = this.createArmorStand();
                this.originalGameMode = player.getGameMode();
                this.wasFlying = player.isFlying();
                this.chargeStartTime = System.currentTimeMillis();

                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(this.armorStand);

                hasStarted = true;
                traveling = true;
            }
        }

        if (hasStarted) {
            if (player.isSneaking()) {
                if (System.currentTimeMillis() > possessStartTime + duration) {
                    this.animateFinalBlow(target.getEyeLocation());
                }
            } else {
                this.possession();
            }
        }
    }

    private void possession() {
        Location targetLocation = target.getEyeLocation();


        this.animateTargetEffects(targetLocation);
        if (this.traveling) {
            this.animateEssence(targetLocation);
        } else {
            target.getWorld().spawnParticle(Particle.DRAGON_BREATH, targetLocation, 1, 0.3, 1, 0.3, 0.02);

            if (System.currentTimeMillis() > possessStartTime + chargeTime) {
                this.animateFinalBlow(targetLocation);
            }
        }
    }

    private void animateEssence(Location targetLocation) {
        this.blast = Methods.advanceLocationToPoint(vector, this.playerOrigin, targetLocation, 0.6);
        this.armorStand.teleport(this.blast);

        if (getRunningTicks() % 5 == 0) {
            player.getWorld().playSound(targetLocation, Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
            Methods.playSpiritParticles(player, this.blast, 0.5, 0.5, 0.5, 0, 1);
        }

        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, this.blast, 10, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().spawnParticle(Particle.PORTAL, this.blast, 5, 0, 0, 0, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, this.blast, 5, 0, 0, 0, 1, this.purple);

        if (target.getLocation().distanceSquared(this.blast) < 1) {
            this.traveling = false;
            this.possessStartTime = System.currentTimeMillis();
            player.setSpectatorTarget(this.target);
            this.armorStand.remove();
            if (target instanceof Player) {
                if (VICTIMS.containsKey(target.getUniqueId())) { //From another player
                    Possess other = VICTIMS.get(target.getUniqueId());
                    other.remove(); //Force them out, damage the player
                }
                VICTIMS.put(target.getUniqueId(), this);
                ActionBar.sendActionBar(this.possessString, (Player) target);
            }
        } else {
            player.setSpectatorTarget(this.armorStand);
        }
    }

    private void animateFinalBlow(Location targetLocation) {
        long currentDuration = System.currentTimeMillis() - possessStartTime;
        currentDuration = Math.max(currentDuration, duration); //Make sure the duration is maxed at 100%
        double multiplier = (double)currentDuration / (double) duration;
        double extraDamage = (damage - minDamage) * multiplier; //Calculate the extra damage to give based on the duration possessed
        DamageHandler.damageEntity(target, minDamage + extraDamage, this);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_HURT, 0.2F, 0F);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetLocation, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.CRIT, targetLocation, 5, 0.3, 1, 0.3, 0);
        remove();
    }

    private void animateTargetEffects(Location targetLocation) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 2), true);
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 2), true);


        if (new Random().nextInt(5) == 0) {
            player.getWorld().playSound(targetLocation, Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
            if (this.blast != null) Methods.playSpiritParticles(player, this.blast, 0.4, 1, 0.4, 0, 1);
        }
    }

    private void displayCharge() {
        long charge = System.currentTimeMillis() - this.chargeStartTime;
        if (charge >= chargeTime) {
            this.traveling = true;
            return;
        }

        for (int i = 0; i < 7; i++) {
            double angle = 360D / 7D * i;
            double angleDrift = 360D / 7D / 2; //The max an angle can shift from the center
            angleDrift *= Math.sin(Math.toRadians(charge / 40D));
            angle += angleDrift;

            double x = Math.cos(Math.toRadians(angle)) * 0.8;
            double z = Math.sin(Math.toRadians(angle)) * 0.8;
            double y = (double)charge / (double)chargeTime * 1.6;

            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x, y, z), 1, this.purple);
        }
    }

    private ArmorStand createArmorStand() {
        ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCollidable(false);
        return stand;
    }

    @Override
    public void remove() {
        player.setSpectatorTarget(null);
        player.setGameMode(this.originalGameMode);
        player.setFlying(wasFlying);

        if (this.armorStand != null) this.armorStand.remove();

        if (traveling) player.teleport(this.blast);
        else player.teleport(player.getLocation().add(0, 2, 0));

        bPlayer.addCooldown(this);

        if (target instanceof Player) {
            VICTIMS.remove(target.getUniqueId());
            ActionBar.sendActionBar(possessLeaveString);
        }

        super.remove();
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
        return "Possess";
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public boolean isExplosiveAbility() {
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
    public boolean isSneakAbility() {
        return false;
    }

    public boolean breakDurability() {
        breakingDurability++;

        if (breakingDurability >= durability) {
            remove();
            DamageHandler.damageEntity(player, (Player) target, selfDamage, new DummyAbility(this).name("PossessRecoil"), true);
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 1F, 1.5F);
            return true;
        } else {
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_PANDA_BITE, 1F, 1.5F);
            return false;
        }
    }

    /**
     * Makes the player punch any players currently possessing them
     * @param player The player punching
     * @return True if the player managed to punch a possessor
     */
    public static boolean punchPossessing(Player player) {
        Possess instance = VICTIMS.get(player.getUniqueId());
        if (instance != null) {
            instance.breakDurability();
            return true;
        }
        return false;
    }
}