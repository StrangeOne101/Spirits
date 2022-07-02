package me.numin.spirits.ability.light;

import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.ability.api.LightAbility;

public class Shelter extends LightAbility {

    //TODO: Add ability collisions
    //TODO: Update sounds.

    public enum ShelterType {
        CLICK, SHIFT
    }
    private Entity target;
    private Location blast, location, origin;
    private ShelterType shelterType;
    private Vector direction;

    private boolean blockArrowsSelf, blockArrowsOthers, moveBlast, removeIfFar, removeOnDamage;
    private double othersRadius;
    private double selfRadius;
    @Attribute(Attribute.RADIUS)
    private double radius;
    private double startHealth;
    private int currPoint;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.RANGE)
    private double removeRange;
    @Attribute(Attribute.DURATION)
    private long duration;
    private long othersCooldown;
    private long selfCooldown;

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    private long realStartTime;

    public Shelter(Player player, ShelterType shelterType) {
        super(player);

        if (!bPlayer.canBend(this)) {
            return;
        }
        this.shelterType = shelterType;
        setFields();

        realStartTime = System.currentTimeMillis();

        startHealth = player.getHealth();
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 2);

        start();
    }

    private void setFields() {
        this.othersCooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.LightSpirit.Shelter.Others.Cooldown");
        this.selfCooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.LightSpirit.Shelter.Self.Cooldown");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.LightSpirit.Shelter.Duration");
        this.removeOnDamage = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirits.Shelter.RemoveOnDamage");
        this.range = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Shelter.Others.Range");
        this.removeIfFar = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Enabled");
        this.removeRange = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Range");
        this.othersRadius = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Shelter.Others.Radius");
        this.selfRadius = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Shelter.Self.Radius");
        this.blockArrowsSelf = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirit.Shelter.Self.BlockArrows");
        this.blockArrowsOthers = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirit.Shelter.Others.BlockArrows");

        this.origin = player.getLocation().clone().add(0, 1, 0);
        this.location = origin.clone();
        this.direction = player.getLocation().getDirection();
        this.moveBlast = true;

        if (this.shelterType == ShelterType.CLICK) {
            this.cooldown = this.othersCooldown;
            this.radius = this.othersRadius;
        } else {
            this.cooldown = this.selfCooldown;
            this.radius = this.selfRadius;
        }
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (this.shelterType == ShelterType.CLICK) shieldOther();
        else if (this.shelterType == ShelterType.SHIFT && player.isSneaking()) shieldSelf();
    }

    private void shieldSelf() {
        if (System.currentTimeMillis() > realStartTime + duration) {
            remove();
        } else {
            rotateShield(player.getLocation(), 96, radius);
            for (Entity approachingEntity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), radius)) {
                if (approachingEntity instanceof LivingEntity && !approachingEntity.getUniqueId().equals(player.getUniqueId())) {
                    this.blockEntity((LivingEntity)approachingEntity);
                } else if (approachingEntity instanceof Projectile && blockArrowsSelf) {
                    Projectile projectile = (Projectile)approachingEntity;
                    projectile.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, projectile.getLocation(), 20, 0, 0, 0, 0.09);
                    projectile.remove();
                }
            }
        }
    }

    private void shieldOther() {
        if (moveBlast) {
            blast = location.add(direction.multiply(1).normalize());
            progressBlast(blast);
            if (origin.distance(blast) > range) {
                remove();
                return;
            }
            for (Entity target : GeneralMethods.getEntitiesAroundPoint(blast, 2)) {
                if (target instanceof LivingEntity && !target.getUniqueId().equals(player.getUniqueId())) {
                    this.target = target;
                    this.realStartTime = System.currentTimeMillis();
                    this.moveBlast = false;
                }
            }
        } else {
            if (System.currentTimeMillis() > realStartTime + duration) {
                remove();
            } else {
                rotateShield(this.target.getLocation(), 100, radius);
                if (removeIfFar && (player.getLocation().distance(target.getLocation()) > removeRange)) {
                    remove();
                    return;
                }
                if (removeOnDamage && (player.getHealth() <= startHealth)) {
                    remove();
                    return;
                }
                for (Entity approachingEntity : GeneralMethods.getEntitiesAroundPoint(this.target.getLocation(), radius)) {
                    if (approachingEntity instanceof LivingEntity && !approachingEntity.getUniqueId().equals(this.target.getUniqueId()) && !approachingEntity.getUniqueId().equals(player.getUniqueId())) {
                        this.blockEntity((LivingEntity)approachingEntity);
                    } else if (approachingEntity instanceof Projectile && blockArrowsOthers) {
                        Projectile projectile = (Projectile)approachingEntity;
                        projectile.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, projectile.getLocation(), 20, 0, 0, 0, 0.09);
                        projectile.remove();
                    }
                }
            }
        }
    }

    private void rotateShield(Location location, int points, double size) {
        for (int t = 0; t < 6; t++) {
            currPoint += 360 / points;
            if (currPoint > 360) {
                currPoint = 0;
            }
            double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
            double x2 = size * Math.cos(angle);
            double y = 0.9 * (Math.PI * 5 - t) - 10;
            double z2 = size * Math.sin(angle);
            location.add(x2, y, z2);
            ParticleEffect.SPELL_INSTANT.display(location, 1, 0.5F, 0.5F, 0.5F, 0);
            location.subtract(x2, y, z2);
        }
    }
    private void progressBlast(Location location) {
        for (int i = 0; i < 6; i++) {
            currPoint += 360 / 100;
            if (currPoint > 360) {
                currPoint = 0;
            }
            double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
            double x = 0.04 * (Math.PI * 4 - angle) * Math.cos(angle + i);
            double z = 0.04 * (Math.PI * 4 - angle) * Math.sin(angle + i);
            location.add(x, 0.1F, z);
            ParticleEffect.SPELL_INSTANT.display(location, 1, 0, 0, 0, 0);
            location.subtract(x, 0.1F, z);
        }
    }

    private void blockEntity(LivingEntity entity) {
        Vector velocity = entity.getLocation().toVector().subtract(player.getLocation().toVector()).multiply(0.1);
        velocity.setY(-0.5);
        entity.setVelocity(velocity);
    }

    @Override
    public void remove() {
        bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return shelterType == ShelterType.CLICK ? blast : player.getLocation();
    }

    @Override
    public double getCollisionRadius() {
        return radius;
    }

    @Override
    public String getName() {
        return "Shelter";
    }

    @Override
    public String getAbilityType() {
        return DEFENSE;
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