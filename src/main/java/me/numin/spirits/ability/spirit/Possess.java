package me.numin.spirits.ability.spirit;

import java.util.Random;

import com.projectkorra.projectkorra.attribute.Attribute;

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

    private ArmorStand armorStand;
    private DustOptions purple = new DustOptions(Color.fromRGB(130, 0, 193), 1);
    private LivingEntity target;
    private GameMode originalGameMode;
    private Location blast, playerOrigin;
    private Vector vector = new Vector(1, 0, 0);

    private boolean hasStarted = false, playEssence, wasFlying;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;

    //Sound effect
    private final float pitch = 0F;
    private final float volume = 0.1F;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    private long realStartTime;

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
        this.damage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.Damage");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.Possess.Duration");
        this.playerOrigin = player.getLocation().add(0, 1, 0);
        this.playEssence = true;
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
                this.realStartTime = System.currentTimeMillis();

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.3F, -1);
                Methods.animateVanish(player);

                this.armorStand = this.createArmorStand();
                this.originalGameMode = player.getGameMode();
                this.wasFlying = player.isFlying();

                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(this.armorStand);
                hasStarted = true;
            }
        }

        if (hasStarted) {
            if (player.isSneaking()) {
                remove(); //TODO Change this to damage the player based on the built up duration
            } else {
                this.possession();
            }
        }
    }

    private void possession() {
        Location targetLocation = target.getEyeLocation();

        if (System.currentTimeMillis() > realStartTime + duration) {
            this.animateFinalBlow(targetLocation);
        } else {
            this.animateTargetEffects(targetLocation);
            if (this.playEssence) {
                this.animateEssence(targetLocation);
            } else {
                target.getWorld().spawnParticle(Particle.DRAGON_BREATH, targetLocation, 1, 0.3, 1, 0.3, 0.02);
            }
        }
    }

    private void animateEssence(Location targetLocation) {
        this.blast = Methods.advanceLocationToPoint(vector, this.playerOrigin, targetLocation, 0.6);
        this.armorStand.teleport(this.blast);

        if (new Random().nextInt(5) == 0) {
            player.getWorld().playSound(targetLocation, Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
            Methods.playSpiritParticles(player, this.blast, 0.5, 0.5, 0.5, 0, 1);
        }

        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, this.blast, 10, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().spawnParticle(Particle.PORTAL, this.blast, 5, 0, 0, 0, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, this.blast, 5, 0, 0, 0, 1, this.purple);

        if (target.getLocation().distanceSquared(this.blast) < 1) {
            this.playEssence = false;
            player.setSpectatorTarget(this.target);
            this.armorStand.remove();
        } else {
            player.setSpectatorTarget(this.armorStand);
        }
    }

    private void animateFinalBlow(Location targetLocation) {
        DamageHandler.damageEntity(target, damage, this);
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

        if (playEssence) player.teleport(this.blast);
        else player.teleport(player.getLocation().add(0, 2, 0));

        bPlayer.addCooldown(this);
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
}