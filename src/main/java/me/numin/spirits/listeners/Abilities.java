package me.numin.spirits.listeners;

import com.projectkorra.projectkorra.ability.CoreAbility;
import me.numin.spirits.ability.dark.DarkBlast;
import me.numin.spirits.ability.light.LightBlast;
import me.numin.spirits.ability.spirit.*;
import me.numin.spirits.utilities.TempSpectator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;

import me.numin.spirits.ability.dark.Intoxicate;
import me.numin.spirits.ability.dark.Shackle;
import me.numin.spirits.ability.dark.Strike;
import me.numin.spirits.ability.light.Alleviate;
import me.numin.spirits.ability.light.Orb;
import me.numin.spirits.ability.light.Shelter;
import me.numin.spirits.ability.light.Shelter.ShelterType;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Abilities implements Listener {

    @EventHandler
    public void onClick(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        Possess.punchPossessing(event.getPlayer());

        if (event.isCancelled() || bPlayer == null) return;

        if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Agility")) {
            if (bPlayer.isOnCooldown("Dash")) return;
            new Dash(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shackle")) {
            new Shackle(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shelter") && !CoreAbility.hasAbility(player, Shelter.class)) {
            new Shelter(player, ShelterType.CLICK);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Strike")) {
            new Strike(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LightBlast") && !CoreAbility.hasAbility(player, LightBlast.class)) {
            new LightBlast(player, LightBlast.LightBlastType.CLICK);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DarkBlast") && !CoreAbility.hasAbility(player, DarkBlast.class)) {
            new DarkBlast(player, DarkBlast.DarkBlastType.CLICK);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("SpiritBlast")) {
            new SpiritBlast(player);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (event.isCancelled() || bPlayer == null) return;

        if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Alleviate")) {
            new Alleviate(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Intoxicate")) {
            new Intoxicate(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Agility") && event.isSneaking()) {
            if (bPlayer.isOnCooldown("Soar")) return;
            new Soar(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Possess")) {
            if (event.isSneaking()) {
                if (!CoreAbility.hasAbility(player, Possess.class)) new Possess(player);
                else if (Possess.stopSpectating(event.getPlayer())) event.setCancelled(true);
            }
        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shelter") && !CoreAbility.hasAbility(player, Shelter.class)) {
            new Shelter(player, ShelterType.SHIFT);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Vanish") && event.isSneaking()) {
            new Vanish(player);

        } else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Orb")) {
            new Orb(player);

        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (Possess.getPossessed(event.getEntity()) != null) {
            Possess possess = Possess.getPossessed(event.getEntity());
            possess.remove();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {

        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) {
            return;
        }
        if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Vanish")
                || bPlayer.getBoundAbilityName().equalsIgnoreCase("Possess")) {
            if (event.getCause() == TeleportCause.SPECTATE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof Player) {
            //event.getDismounted().sendMessage("Worked");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        TempSpectator.destroy(event.getPlayer());
    }
}