package me.numin.spirits.ability.light.combo;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.utilities.Methods.SpiritType;
import me.numin.spirits.ability.api.LightAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class Rejuvenate extends LightAbility implements ComboAbility {

    //TODO: Add sounds.

    private Location circleCenter, location, location2, location3;

    private boolean damageDarkSpirits, damageMonsters;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RADIUS)
    private double radius;
    private double counter;
    private int currPoint, effectInt;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;

    public Rejuvenate(Player player) {
        super(player);

        if (!bPlayer.canBendIgnoreBinds(this)) {
            return;
        }
        setFields();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.07F, 5);
        start();
        bPlayer.addCooldown(this);
    }

    private void setFields() {
        this.cooldown = Spirits.plugin.getConfig().getLong("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Cooldown");
        this.duration = Spirits.plugin.getConfig().getLong("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Duration");
        this.radius = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Radius");
        this.effectInt = Spirits.plugin.getConfig().getInt("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.EffectInterval");
        this.damage = Spirits.plugin.getConfig().getDouble("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Damage");
        this.damageDarkSpirits = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtDarkSpirits");
        this.damageMonsters = Spirits.plugin.getConfig().getBoolean("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtMonsters");
        location = player.getLocation();
        location2 = player.getLocation();
        location3 = player.getLocation();
        circleCenter = player.getLocation();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }
        spawnCircle();
        grabEntities();
    }

    private void spawnCircle() {
        Methods.createPolygon(location, 8, radius, 0.2, Particle.SPELL_INSTANT);
        for (int i = 0; i < 6; i++) {
            this.currPoint += 360 / 300;
            if (this.currPoint > 360) {
                this.currPoint = 0;
            }
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = radius * Math.cos(angle);
            double x2 = radius * Math.sin(angle);
            double z = radius * Math.sin(angle);
            double z2 = radius * Math.cos(angle);
            location2.add(x, 0, z);
            ParticleEffect.END_ROD.display(location2, 0, 0, 0, 0, 1);
            location2.subtract(x, 0, z);

            location3.add(x2, 0, z2);
            ParticleEffect.END_ROD.display(location3, 0, 0, 0, 0, 1);
            location3.subtract(x2, 0, z2);
        }
        counter += Math.PI / 32;
        if (!(counter >= Math.PI * 4)) {
            for (double i = 0; i <= Math.PI * 2; i += Math.PI / 1.2) {
                double x = 0.5 * (Math.PI * 4 - counter) * Math.cos(counter - i);
                double y = 0.4 * counter;
                double z = 0.5 * (Math.PI * 4 - counter) * Math.sin(counter - i);
                location.add(x, y, z);
                Methods.playSpiritParticles(SpiritType.LIGHT, location, 0, 0, 0, 0, 1);
                player.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0.1, 0.1, 0.1, 0, new DustOptions(Color.fromBGR(255, 255, 255), 1));
                location.subtract(x, y, z);
            }
        }

        ParticleEffect.ENCHANTMENT_TABLE.display(location, 10, (float)(radius / 2), 0.4F, (float)(radius / 2), 0);
    }

    private void grabEntities() {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(circleCenter, radius)) {
            if (entity instanceof LivingEntity) {
                healEntities(entity);
            }
        }
    }

    private void healEntities(Entity entity) {
        if (new Random().nextInt(effectInt) == 0) {
            if (entity instanceof Player) {
                Player ePlayer = (Player) entity;
                BendingPlayer bEntity = BendingPlayer.getBendingPlayer(ePlayer);
                if (!bEntity.hasElement(Element.getElement("DarkSpirit"))) {
                    ePlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
                    ParticleEffect.HEART.display(ePlayer.getLocation().add(0, 2, 0), 0, 0, 0, 0, 1);
                } else {
                    if (damageDarkSpirits) {
                        DamageHandler.damageEntity(ePlayer, damage, this);
                    }
                }
            } else if (entity instanceof Monster && damageMonsters) {
                DamageHandler.damageEntity(entity, damage, this);

            } else {
                LivingEntity le = (LivingEntity)entity;
                le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
                ParticleEffect.HEART.display(entity.getLocation().add(0, 2, 0), 0, 0, 0, 0, 1);
            }
        }
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Rejuvenate(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        ArrayList<AbilityInformation> combo = new ArrayList<AbilityInformation>();
        combo.add(new AbilityInformation("Alleviate", ClickType.SHIFT_DOWN));
        combo.add(new AbilityInformation("Alleviate", ClickType.RIGHT_CLICK_BLOCK));
        combo.add(new AbilityInformation("Alleviate", ClickType.SHIFT_UP));
        return combo;
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
        return "Rejuvenate";
    }

    @Override
    public String getDescription() {
        return Methods.setSpiritDescription(SpiritType.LIGHT, "Combo") +
                Spirits.plugin.getConfig().getString("Language.Abilities.LightSpirit.Rejuvenate.Description");
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