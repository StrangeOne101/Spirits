package me.numin.spirits.utilities;

import java.util.ArrayList;
import java.util.List;

import me.numin.spirits.SpiritElement;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;

public class Methods {

    /**
     * Used to move a location in a certain direction.
     *
     * @param direction The direction in which to move.
     * @param point The starting point from which the location moves.
     * @param speed The speed of the point.
     * @return The new location.
     */
    public static Location advanceLocationToDirection(Vector direction, Location point, double speed) {
        point.add(direction.multiply(speed).normalize().clone());
        return point;
    }

    /**
     * Moves a location from one point to another.
     *
     * @param vector The vector used to move the points.
     * @param from The starting point.
     * @param to The end point.
     * @param speed The speed of the point.
     * @return The new location.
     */
    public static Location advanceLocationToPoint(Vector vector, Location from, Location to, double speed) {
        vector.add(to.toVector()).subtract(from.toVector()).multiply(speed).normalize();
        from.add(vector.clone().multiply(speed));
        return from;
    }

    /**
     * A general animation to display when making a
     * Spirit/Player invisible.
     *
     * @param player The player being vanished.
     */
    public static void animateVanish(Player player) {
        Location location = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 20, 0, 0, 0, 0.09);
        player.getWorld().spawnParticle(Particle.PORTAL, location, 30, 0, 0, 0, 2);
        playSpiritParticles(player, location, 0.5, 0.5, 0.5, 0, 5);
    }

    /**
     * Used to create some polygon at a location.
     *
     * @param location Center point for the polygon.
     * @param points How many vertices the polygon will have.
     * @param radius The radius the polygon will have.
     * @param height The height from location.
     * @param particle The type of particle that spawns at each point.
     */
    public static void createPolygon(Location location, int points, double radius, double height, Particle particle) {
        for (int i = 0; i < points; i++) {
            double angle = 360.0 / points * i;
            angle = Math.toRadians(angle);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            location.add(x, height, z);
            location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0);
            location.subtract(x, height, z);
        }
    }

    /**
     * Gets the type of spirit a player may be.
     *
     * @param player The player being tested.
     * @return The type of spirit they are, null if they aren't a spirit.
     */
    public static SpiritElement getSpiritType(Player player) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return null;

        if (bPlayer.hasElement(SpiritElement.LIGHT) && bPlayer.hasElement(SpiritElement.DARK)) {
            return SpiritElement.NEUTRAL;
        } else if (bPlayer.hasElement(SpiritElement.LIGHT)) {
            return SpiritElement.LIGHT;
        } else if (bPlayer.hasElement(SpiritElement.DARK)) {
            return SpiritElement.DARK;
        } else if (bPlayer.hasElement(SpiritElement.NEUTRAL)) {
            return SpiritElement.NEUTRAL;
        } else {
            return null;
        }
    }

    /**
     * Plays particles based on SpiritType.
     *
     * @param spiritType The type of particles to display.
     * @param location The location where the particles will spawn.
     * @param x The off-set of the X axis.
     * @param y The off-set of the Y axis.
     * @param z The off-set of the Z axis.
     * @param speed The particle speed.
     * @param amount The amount of particles to display.
     */
    public static void playSpiritParticles(SpiritElement spiritType, Location location, double x, double y, double z, double speed, int amount) {
        /*DustOptions teal = new DustOptions(Color.fromRGB(0, 176, 180), 1),
                white = new DustOptions(Color.fromRGB(255, 255,255), 1),
                black = new DustOptions(Color.fromRGB(0, 0, 0), 1);*/

        if (spiritType == SpiritElement.NEUTRAL) {
            location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, amount, x, y, z, speed);
        } else if (spiritType == SpiritElement.DARK) {
            location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, amount, x, y, z, speed);
        } else if (spiritType == SpiritElement.LIGHT) {
            location.getWorld().spawnParticle(Particle.SPELL_INSTANT, location, amount, x, y, z, speed);
        }
        location.getWorld().spawnParticle(Particle.REDSTONE, location, amount, x, y, z, speed, new DustOptions(spiritType.getDustColor(), 2));
    }

    /**
     * Used to play particles based on the type of Spirit a
     * player is.
     *
     * @param player The player being tested.
     * @param location The location of the particles.
     * @param x The off-set of the X axis.
     * @param y The off-set of the Y axis.
     * @param z The off-set of the Z axis.
     * @param speed The particle speed.
     * @param amount The amount of particles to display.
     */
    public static void playSpiritParticles(Player player, Location location, double x, double y, double z, double speed, int amount) {
        SpiritElement spiritType = getSpiritType(player);
        if (spiritType == null) return;
        playSpiritParticles(spiritType, location, x, y, z, speed, amount);
    }

    /**
     * Sets the velocity of a player.
     *
     * @param target The target being manipulated.
     * @param speed The speed of the player.
     * @return The players new velocity.
     */
    public static Vector setVelocity(LivingEntity target, float speed) {
        Location location = target.getLocation();
        return location.getDirection().normalize().multiply(speed);
    }

    /**
     * Sets the velocity of a player with a height
     * addition.
     *
     * @param target The target being manipulated.
     * @param speed The speed of the player.
     * @param height The height of the player.
     * @return The players new velocity.
     */
    public static Vector setVelocity(LivingEntity target, float speed, double height) {
        Vector direction = setVelocity(target, speed);
        direction.setY(height);
        return direction;
    }

    /**
     * Checks if the player is the avatar. This is a hotfix so players with both
     * a spirit element and a light/dark spirit element don't appear as the avatar
     * because they "have more than one element"
     * @param player The player to check
     * @return True if they are the avatar
     */
    public static boolean isAvatar(BendingPlayer player) {
        if (player.getPlayer().hasPermission("bending.avatar")) return true;
        if (player.getElements().size() > 1) {
            List<Element> clonedElements = new ArrayList<>(player.getElements());
            clonedElements.remove(SpiritElement.NEUTRAL);

            //People with both shouldnt return true
            if (clonedElements.size() == 2 && player.hasElement(SpiritElement.DARK)
                    && player.hasElement(SpiritElement.LIGHT)) return false;

            return clonedElements.size() > 1;
        }
        return false;
    }
}