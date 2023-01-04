package me.numin.spirits.utilities;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.Cooldown;
import me.numin.spirits.ability.spirit.Possess;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TempSpectator {

    private static Map<UUID, TempSpectator> MAP = new HashMap<>();

    private Player player;
    private BendingPlayer bPlayer;

    private GameMode gamemode;
    private float flySpeed;
    private boolean canFly;
    private boolean wasFlying;

    private TempSpectator(Player player) {
        this.player = player;
        this.bPlayer = BendingPlayer.getBendingPlayer(player);

        MAP.put(player.getUniqueId(), this);
    }

    public void spectator() {
        if (this.player.getGameMode() == GameMode.SPECTATOR) return;

        this.gamemode = player.getGameMode();
        this.canFly = player.getAllowFlight();
        this.wasFlying = player.isFlying();
        this.flySpeed = player.getFlySpeed();

        this.player.setGameMode(GameMode.SPECTATOR);
    }

    public void revert() {
        if (!isSpectator()) return;
        this.player.setSpectatorTarget(null);
        this.player.setGameMode(this.gamemode);
        this.player.setFlying(this.wasFlying);
        this.player.setAllowFlight(this.canFly);
        this.player.setFlySpeed(this.flySpeed);

        this.player.showPlayer(ProjectKorra.plugin, this.player); //Fixes a vanilla bug where the player is invisible after switching from
                                                                  //spectator to survival
    }

    public boolean isSpectator() {
        return this.player.getGameMode() == GameMode.SPECTATOR;
    }

    public boolean canBend(CoreAbility ability, boolean ignoreBinds, boolean ignoreCooldowns) {
        List<String> disabledWorlds = ConfigManager.getConfig().getStringList("Properties.DisabledWorlds");
        Location playerLoc = this.player.getLocation();

        if (!this.player.isOnline() || this.player.isDead())
            return false;
        if (!ignoreBinds && !bPlayer.canBind(ability))
            return false;
        if (ability.getPlayer() != null && ability.getLocation() != null && !ability.getLocation().getWorld().equals(this.player.getWorld()))
            return false;
        if (!ignoreCooldowns && bPlayer.isOnCooldown(ability.getName()))
            return false;
        if (!ignoreBinds && !ability.getName().equals(bPlayer.getBoundAbilityName()))
            return false;
        if (disabledWorlds.contains(this.player.getWorld().getName()))
            return false;
        if (Commands.isToggledForAll || !bPlayer.isToggled() || !bPlayer.isElementToggled(ability.getElement()))
            return false;

        if (!ignoreCooldowns && bPlayer.getCooldowns().containsKey(ability.getName())) {
            if (((Cooldown)bPlayer.getCooldowns().get(ability.getName())).getCooldown() + ConfigManager.getConfig().getLong("Properties.GlobalCooldown") >= System.currentTimeMillis()) {
                return false;
            }

            bPlayer.getCooldowns().remove(ability.getName());
        }

        if (bPlayer.isChiBlocked() || bPlayer.isParalyzed() || bPlayer.isBloodbent() || bPlayer.isControlledByMetalClips())
            return false;
        if (RegionProtection.isRegionProtected(this.player, playerLoc, ability)) {
            return false;
        }

        return true;
    }

    public boolean canBend(CoreAbility ability) {
        return canBend(ability, false, false);
    }

    public boolean canBendIgnoreBinds(CoreAbility ability) {
        return canBend(ability, true, false);
    }

    public boolean canBendIgnoreBindsCooldowns(CoreAbility ability) {
        return canBend(ability, true, true);
    }

    public boolean canBendIgnoreCooldowns(CoreAbility ability) {
        return canBend(ability, false, true);
    }

    public static TempSpectator create(Player player) {
        if (MAP.containsKey(player.getUniqueId())) {
            return MAP.get(player.getUniqueId());
        }

        return new TempSpectator(player);
    }

    public static void destroy(Player player) {
        TempSpectator spectator = MAP.get(player.getUniqueId());
        if (spectator != null && spectator.isSpectator()) {
            spectator.revert();
        }
        MAP.remove(player.getUniqueId());
    }
}
