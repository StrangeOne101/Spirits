package me.numin.spirits.ability.spirit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;

import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.Cooldown;
import me.numin.spirits.SpiritElement;
import me.numin.spirits.utilities.DummyAbility;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Possess extends SpiritAbility {

    //TODO: Test how it interacts with sudden change in pathway (like the spawning of a RaiseEarth that obstructs it's path)
    //TODO: Test how it interacts with abilities like AirShield and Shelter.
    //TODO: Add configurable speed for the armor stand/blast feature.

    public enum State {
        SELECTING, CHARGING, TRAVELING, POSSESSING
    }

    private static Map<UUID, Possess> VICTIMS = new HashMap<>();

    private double minDamage;
    @Attribute("SelfDamage")
    private double selfDamage;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("Durability")
    private int durability;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;

    private int breakingDurability; //The counter used to determine when the possession is broken.
    private State state = State.SELECTING;

    //Sound effect
    private final float pitch = 0F;
    private final float volume = 0.1F;

    private long possessStartTime, chargeStartTime;
    private boolean releasedSneak, wasFlying;
    private int counter;
    private ArmorStand armorStand;
    private DustOptions purple = new DustOptions(Color.fromRGB(130, 0, 193), 1);
    private LivingEntity target;
    private GameMode originalGameMode;
    private Location blast;
    private SpiritElement spiritElement = SpiritElement.NEUTRAL;
    private final Vector none = new Vector(0, 0, 0);

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
        this.speed = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.Neutral.Possess.Speed");
        this.possessString = ChatColor.translateAlternateColorCodes('&',
                Spirits.plugin.getConfig().getString("Language.Abilities.Spirit.Possess.Possessed"));
        this.possessLeaveString = ChatColor.translateAlternateColorCodes('&',
                Spirits.plugin.getConfig().getString("Language.Abilities.Spirit.Possess.PossessionBroken"));
        this.wasFlying = player.isFlying();
        this.spiritElement = Methods.getSpiritType(player);
    }

    @Override
    public void progress() {
        if (!canBend()) {
            remove();
            return;
        }
        if (state == State.SELECTING) {
            if (!player.isSneaking()) {
                remove();
                return;
            }

            //We do this here so the range can be changed with attributes AND so they don't have to re-press
            //sneak to select an entity again if they fail to select one.
            RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), range,
                    ent -> ent instanceof LivingEntity && !ent.equals(player) && !(ent instanceof ArmorStand));
            //Entity entity = GeneralMethods.getTargetedEntity(player, range);
            if (result != null && result.getHitEntity() != null) {
                this.target = (LivingEntity) result.getHitEntity();
                Spirits.plugin.getLogger().info("Target found");

                this.chargeStartTime = System.currentTimeMillis();
                this.blast = this.player.getEyeLocation();
                state = State.CHARGING;
                Spirits.plugin.getLogger().info("State set to CHARGING");
                return;
            }
        } else if (state == State.CHARGING) {
            chargeDisplay();
        } else if (state == State.TRAVELING) {
            travelDisplay();
        } else {
            possessionTick();
        }
    }

    /**
     * Called every tick while charging. Displays particles and plays sounds.
     */
    private void chargeDisplay() {
        long charge = System.currentTimeMillis() - this.chargeStartTime;
        if (charge >= chargeTime) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.3F, -1);
            Methods.animateVanish(player);

            this.armorStand = this.createArmorStand();
            this.originalGameMode = player.getGameMode();
            this.wasFlying = player.isFlying();

            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(this.armorStand);

            this.state = State.TRAVELING;

            return;
        }

        double percent = (double) charge / (double) chargeTime;

        counter++;

        Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.5));

        for (int i = 0; i < 2; i++) {
            double angle = ((i * 180) + (counter * 20)) % 360;

            Vector vec = GeneralMethods.rotateVectorAroundVector(this.player.getEyeLocation().getDirection(), this.player.getEyeLocation().getDirection(), angle);

            player.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(vec), 1, 0, 0, 0, 0, new DustOptions(spiritElement.getDustColor(), 1));
        }

        /*for (int i = 0; i < 7; i++) {
            double angle = 360D / 7D * i;
            double angleDrift = 360D / 7D; //The max an angle can shift from the center
            angleDrift *= Math.sin(Math.toRadians(percent * 360D));
            angle += angleDrift;

            double x = Math.cos(Math.toRadians(angle)) * 0.8;
            double z = Math.sin(Math.toRadians(angle)) * 0.8;
            double y = (double)charge / (double)chargeTime * 1.6;

            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x, y, z), 1, this.purple);
        }*/


    }

    /**
     * Called every tick while traveling. Progresses the location and displays particles.
     */
    private void travelDisplay() {
        //vector.add(to.toVector()).subtract(from.toVector()).multiply(speed).normalize();
        //from.add(vector.clone().multiply(speed));
        //this.blast = Methods.advanceLocationToPoint(vector, this.playerOrigin, target.getEyeLocation(), 0.6);
        Vector vec = this.target.getEyeLocation().toVector().subtract(this.blast.toVector());

        RayTraceResult rayTraceResult = this.player.getWorld().rayTrace(this.blast, vec, speed, FluidCollisionMode.NEVER, true, 0.8D,
                entity -> entity instanceof LivingEntity && entity != this.player && !(entity instanceof ArmorStand));

        if (rayTraceResult != null) {
            Spirits.plugin.getLogger().info("Raytrace result found");
            if (rayTraceResult.getHitEntity() != null) {
                Spirits.plugin.getLogger().info("Raytrace result hit entity found");
                possess((LivingEntity) rayTraceResult.getHitEntity());
                return;
            } else if (rayTraceResult.getHitBlock() != null) {
                Spirits.plugin.getLogger().info("Raytrace result hit block found");
                this.target = null;
                remove();
                player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 1F, 1.5F);
            }
        } else { //Progress the blast if it didn't hit anything
            this.blast.add(vec.normalize().multiply(speed));
            Spirits.plugin.getLogger().info("Brrrrrr");
        }

        this.armorStand.teleport(this.blast);

        if (getRunningTicks() % 5 == 0) {
            player.getWorld().playSound(this.blast, Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
            Methods.playSpiritParticles(player, this.blast, 0.5, 0.5, 0.5, 0, 1);
        }

        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, this.blast, 10, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().spawnParticle(Particle.PORTAL, this.blast, 5, 0, 0, 0, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, this.blast, 5, 0, 0, 0, 1, this.purple);

        player.setSpectatorTarget(this.armorStand);

    }

    /**
     * Called every tick when the target has successfully been possessed.
     */
    private void possessionTick() {
        if (target.getWorld() != player.getWorld()) {
            remove();
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));

        double yy = target.getHeight() / 2D;
        double xx = (target.getWidth() * 1.2) / 2D;

        Methods.playSpiritParticles(player, target.getLocation().add(0, yy, 0), xx, yy, xx, 0.5, 1);
        target.getWorld().spawnParticle(Particle.DRAGON_BREATH, target.getLocation().add(0, yy, 0), 1, xx, yy, xx);

        if (getRunningTicks() % 5 == 0) {
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
        }

        /*if (!player.isSneaking()) releasedSneak = true;
        else if (releasedSneak && player.isSneaking()) {
            this.finalBlow();
            return;
        }*/

        if (System.currentTimeMillis() > possessStartTime + chargeTime) {
            this.finalBlow();
        }
    }

    /**
     * Possess the target
     * @param entity The target to possess
     */
    private void possess(LivingEntity entity) {
        this.target = entity;
        this.possessStartTime = System.currentTimeMillis();
        player.setSpectatorTarget(this.target);
        this.state = State.POSSESSING;
        if (!player.isSneaking()) releasedSneak = true;
        this.armorStand.remove();
        if (target instanceof Player) {
            if (VICTIMS.containsKey(target.getUniqueId())) { //From another player
                Possess other = VICTIMS.get(target.getUniqueId());
                other.remove(); //Force them out, damage the player
            }
            VICTIMS.put(target.getUniqueId(), this);
            ActionBar.sendActionBar(this.possessString, (Player) target);
        }
    }

    /**
     * Called when the target needs to be damaged
     */
    private void finalBlow() {
        long currentDuration = System.currentTimeMillis() - possessStartTime;
        currentDuration = Math.max(currentDuration, duration); //Make sure the duration is maxed at 100%
        double multiplier = (double)currentDuration / (double) duration;
        double extraDamage = (damage - minDamage) * multiplier; //Calculate the extra damage to give based on the duration possessed

        DamageHandler.damageEntity(target, minDamage + extraDamage, this);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_HURT, 0.2F, 0F);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getEyeLocation(), 1, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.CRIT, target.getEyeLocation(), 20, 0.3, 1, 0.3, 0);

        remove();
    }

    private ArmorStand createArmorStand() {
        ArmorStand stand = player.getWorld().spawn(player.getEyeLocation(), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setBasePlate(false);
        stand.setMarker(true);
        return stand;
    }

    /**
     * Call to damage the possessor. If they do this enough, the target can break possession
     * @return True if the target broke out
     */
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

    public static boolean stopSpectating(Player player) {
        Possess possess = CoreAbility.getAbility(player, Possess.class);
        if (possess != null) {
            if (possess.state == State.TRAVELING) return true;
            if (possess.state == State.POSSESSING) {
                if (!possess.releasedSneak) {
                    possess.releasedSneak = true;
                    return true; //Stop them dismounting still
                } else {
                    possess.finalBlow();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void remove() {
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setSpectatorTarget(null);
            player.setGameMode(this.originalGameMode);
        }

        player.setFlying(wasFlying);

        if (this.armorStand != null) this.armorStand.remove();

        if (state == State.TRAVELING) {
            player.teleport(this.blast);
            bPlayer.addCooldown(this);
        } else if (state == State.POSSESSING) {
            player.teleport(target.getLocation().add(0, 1, 0));
            bPlayer.addCooldown(this);
        }

        if (target instanceof Player) {
            VICTIMS.remove(target.getUniqueId());
            ActionBar.sendActionBar(possessLeaveString);
        }

        super.remove();
    }

    private boolean canBend() {

        List<String> disabledWorlds = getConfig().getStringList("Properties.DisabledWorlds");
        Location playerLoc = this.player.getLocation();

        if (!this.player.isOnline() || this.player.isDead())
            return false;
        if (!bPlayer.canBind(this))
            return false;
        if (getPlayer() != null && getLocation() != null && !getLocation().getWorld().equals(this.player.getWorld()))
            return false;
        if (bPlayer.isOnCooldown(getName()))
            return false;
        if (!getName().equals(bPlayer.getBoundAbilityName()))
            return false;
        if (disabledWorlds.contains(this.player.getWorld().getName()))
            return false;
        if (Commands.isToggledForAll || !bPlayer.isToggled() || !bPlayer.isElementToggled(getElement()))
            return false;
        if (this.player.getGameMode() == GameMode.SPECTATOR && (state != State.TRAVELING && state != State.POSSESSING)) {
            return false;
        }

        if (bPlayer.getCooldowns().containsKey(getName())) {
            if (((Cooldown)bPlayer.getCooldowns().get(getName())).getCooldown() + getConfig().getLong("Properties.GlobalCooldown") >= System.currentTimeMillis()) {
                return false;
            }

            bPlayer.getCooldowns().remove(getName());
        }

        if (bPlayer.isChiBlocked() || bPlayer.isParalyzed() || bPlayer.isBloodbent() || bPlayer.isControlledByMetalClips())
            return false;
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, getName(), playerLoc)) {
            return false;
        }

        return true;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return this.blast;
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

    @Override
    public double getCollisionRadius() {
        return 0.8;
    }
}