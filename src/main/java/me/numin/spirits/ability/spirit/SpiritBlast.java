package me.numin.spirits.ability.spirit;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.numin.spirits.SpiritElement;
import me.numin.spirits.Spirits;
import me.numin.spirits.ability.api.SpiritAbility;
import me.numin.spirits.utilities.Methods;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Iterator;

public class SpiritBlast extends SpiritAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute("MaxBlasts")
    private int maxBlasts;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute("CanRedirect")
    private boolean canRedirect;
    @Attribute("CanAlwaysRedirect")
    private boolean canRedirectAllTheTime;

    private boolean redirected;
    private Location location;
    private Location target;
    private Vector direction;
    private long firstStartTime; //The time the first blast starts
    private boolean first; //If this blast is the initial one
    private int blasts = 1; //How many blasts are active right now
    private int rotation;
    public SpiritElement spiritElement = SpiritElement.NEUTRAL;

    public SpiritBlast(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) { //If the cooldown is already active
            return;
        }

        Collection<SpiritBlast> blastColl = CoreAbility.getAbilities(player, SpiritBlast.class);
        Iterator<SpiritBlast> it = blastColl.iterator();
        if (it.hasNext()) {
            SpiritBlast blast = it.next();

            if (blast.blasts >= blast.maxBlasts) {
                return;
            }

            blastColl.forEach(abil -> { //Make each blast know one more has been added
                abil.blasts++;
            });

            this.maxBlasts = blasts;
            this.firstStartTime = blast.firstStartTime;
            this.blasts = blast.blasts;
        }
        setFields();
        setTarget();
        start();


        if (this.blasts >= this.maxBlasts) {
            bPlayer.addCooldown(this);
        }

        if (this.firstStartTime == 0) {
            this.firstStartTime = System.currentTimeMillis();
            this.first = true;
        }
    }

    private void setFields() {
        this.damage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.SpiritBlast.Damage");
        this.cooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.SpiritBlast.Cooldown");
        this.range = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.SpiritBlast.Range");
        this.speed = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.SpiritBlast.Speed");
        this.radius = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.SpiritBlast.Radius");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.Neutral.SpiritBlast.Duration");
        this.maxBlasts = Spirits.plugin.getConfig().getInt("Abilities.Spirits.Neutral.SpiritBlast.MaxBlasts");
        this.canRedirect = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral.SpiritBlast.CanRedirect");
        this.canRedirectAllTheTime = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.Neutral.SpiritBlast.CanAlwaysRedirect");

        this.location = player.getEyeLocation();
        this.spiritElement = Methods.getSpiritType(player);
    }

    private void setTarget() {
        RayTraceResult result = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 50D, FluidCollisionMode.NEVER, false, 1D,
                entity -> entity instanceof LivingEntity && !entity.equals(player) && !(entity instanceof ArmorStand));

        if (result == null) {
            this.target = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(range));
        } else if (result.getHitBlock() != null) {
            this.target = result.getHitBlock().getLocation();
        } else if (result.getHitEntity() != null) {
            this.target = result.getHitEntity().getLocation();
        }

        this.direction = this.target.toVector().subtract(this.location.toVector()).normalize();
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }

        if (first && System.currentTimeMillis() - firstStartTime >= duration) {
            bPlayer.addCooldown(this);
        }

        //If the beam should redirect
        if (player.isSneaking() && ((canRedirect && !redirected) || canRedirectAllTheTime)) {
            setTarget();
            redirected = true;
        }

        advanceLocation();

    }

    private void advanceLocation() {
        if (location.distanceSquared(player.getLocation()) > range * range) {
            remove();
            return;
        }

        RayTraceResult rayTraceResult = this.player.getWorld().rayTrace(this.location, this.direction, speed, FluidCollisionMode.NEVER, true, radius,
                entity -> entity instanceof LivingEntity && entity != this.player && !(entity instanceof ArmorStand));

        double locationDistance = 0D;
        if (rayTraceResult != null) {
            if (rayTraceResult.getHitEntity() != null) {
                hit((LivingEntity) rayTraceResult.getHitEntity());
                locationDistance = Math.sqrt(rayTraceResult.getHitEntity().getLocation().distanceSquared(this.location));
            } else if (rayTraceResult.getHitBlock() != null) {
                this.target = null;
                remove();
                locationDistance = Math.sqrt(rayTraceResult.getHitBlock().getLocation().distanceSquared(this.location));
                //TODO Configure sound
                player.getWorld().playSound(target, Sound.ENTITY_ELDER_GUARDIAN_HURT, 1F, 1.5F);
            }
        }

        double d = 0;
        Location displayLoc = this.location.clone();
        Vector tempVec = direction.clone().multiply(0.2);

        do {
            for (int i = 0; i < 3; i++) {
                double x = Math.cos(i * 120 + rotation) * radius;
                double z = Math.sin(i * 120 + rotation) * radius;
                Vector vec = new Vector(x, 0, z);
                vec = direction.crossProduct(vec);
                player.getWorld().spawnParticle(Particle.REDSTONE, displayLoc.clone().add(vec), 1, 0, 0, 0, 0.1, new Particle.DustOptions(spiritElement.getDustColor(), 2));
            }

            displayLoc.add(tempVec);
            d += 0.2;
            rotation += 20;
        } while (d < speed && d <= locationDistance);

        this.location.add(direction.clone().multiply(speed));
        //TODO Sound
    }

    private void hit(LivingEntity entity) {
        DamageHandler.damageEntity(entity, player, damage, this);
        //TODO Sound
        remove();
    }

    @Override
    public void remove() {
        super.remove();

        //This is the final blast
        if (CoreAbility.getAbilities(player, SpiritBlast.class).size() == 0) {
            bPlayer.addCooldown(this);
        }
    }

    @Override
    public Element getElement() {
        return this.spiritElement == null ? SpiritElement.NEUTRAL : this.spiritElement;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "SpiritBlast";
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
    public boolean isEnabled() {
        //Must be redefined due to some stupid feken bugs
        return Spirits.plugin.getConfig().getBoolean("Abilities.Spirits." + SpiritElement.NEUTRAL.getConfigName() + "." + getName() + ".Enabled");
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
