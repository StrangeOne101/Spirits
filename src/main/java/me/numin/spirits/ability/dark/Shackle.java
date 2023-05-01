package me.numin.spirits.ability.dark;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.Manager;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.FlightHandler;
import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.ability.api.DarkAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Shackle extends DarkAbility {

    //TODO: Maybe smooth out the blast and entity detection logic, outdated.
    //TODO: Remove the checkEntities boolean and add a Entity variable instead.
    //TODO: Update sounds.

    private Entity target;
    private Location location, origin;
    private Vector direction;

    private boolean checkEntities;
    @Attribute(Attribute.RADIUS)
    private double radius;
    private float originWalkSpeed;
    private int currPoint;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    private long realStartTime;

    public Shackle(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) {
            return;
        }

        setFields();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, -1);
        start();
    }

    @Override
    public String getAbilityType() {
        return DEFENSE;
    }

    private void setFields() {
        this.cooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.DarkSpirit.Shackle.Cooldown");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.DarkSpirit.Shackle.Duration");
        this.range = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.DarkSpirit.Shackle.Range");
        this.radius = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.DarkSpirit.Shackle.Radius");
        this.origin = player.getLocation().clone().add(0, 1, 0);
        this.location = origin.clone();
        this.direction = player.getLocation().getDirection();
        this.checkEntities = true;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        bind();
    }

    private void bind() {
        if (checkEntities) {
            Location blast = location.add(direction.multiply(1).normalize());
            blastSpiral(blast);

            if (origin.distance(blast) > range) {
                remove();
                return;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(blast, radius)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    this.realStartTime = System.currentTimeMillis();
                    this.target = entity;
                    if (entity instanceof Player) this.originWalkSpeed = ((Player) entity).getWalkSpeed();
                    checkEntities = false;
                }
            }
        } else {
            if (this.target == null || this.target.isDead() || this.target.getWorld() != player.getWorld()) {
                remove();
                return;
            }
            if (System.currentTimeMillis() > realStartTime + duration) {
                target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 5, 0, 0, 0, 0.08);
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5F, 1.5F);
                remove();
            } else {
                holdSpiral(target.getLocation());

                if (this.target instanceof Player) {
                    Player playerTarget = (Player)this.target;
                    playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 5));
                    playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 128));
                } else {
                    LivingEntity livingTarget = (LivingEntity)this.target;
                    livingTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 128));
                    livingTarget.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 128));
                }
            }

        }
    }

    private void blastSpiral(Location location) {
        for (int i = 0; i < 6; i++) {
            currPoint += 360 / 200;
            if (currPoint > 360) {
                currPoint = 0;
            }
            double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
            double x = 0.04 * (Math.PI * 4 - angle) * Math.cos(angle + i);
            double z = 0.04 * (Math.PI * 4 - angle) * Math.sin(angle + i);
            location.add(x, 0.1F, z);
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 1, 0, 0, 0, 0);
            location.subtract(x, 0.1F, z);
        }
    }
    private void holdSpiral(Location location) {
        for (int t = 0; t < 2; t++) {
            currPoint += 360 / 30;
            if (currPoint > 360) {
                currPoint = 0;
            }
            double angle2 = currPoint * Math.PI / 180 * Math.cos(Math.PI);
            double x2 = 0.04 * (Math.PI * 5 - angle2) * Math.cos(angle2 + t);
            double z2 = 0.04 * (Math.PI * 5 - angle2) * Math.sin(angle2 + t);
            location.add(x2, 0.1F, z2);
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 1, 0, 0, 0, 0);
            location.subtract(x2, 0.1F, z2);
        }
    }

    @Override
    public void remove() {
        if (this.target != null) {
            if (this.target instanceof Player) {
                ((Player)this.target).setWalkSpeed(this.originWalkSpeed);
            }
            LivingEntity livingTarget = (LivingEntity)this.target;
            if (livingTarget.hasPotionEffect(PotionEffectType.JUMP)) livingTarget.removePotionEffect(PotionEffectType.JUMP);
            if (livingTarget.hasPotionEffect(PotionEffectType.SLOW)) livingTarget.removePotionEffect(PotionEffectType.SLOW);
        }
        bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public double getCollisionRadius() {
        return radius;
    }

    @Override
    public String getName() {
        return "Shackle";
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