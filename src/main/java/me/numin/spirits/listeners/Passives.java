package me.numin.spirits.listeners;

import com.projectkorra.projectkorra.ability.CoreAbility;
import me.numin.spirits.SpiritElement;
import me.numin.spirits.ability.spirit.passive.SpiritualBody;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;

public class Passives implements Listener {

    private SpiritualBody spiritualBody;

    @EventHandler(priority = EventPriority.HIGH) //HIGH will make sure air and earth passives have already run
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (spiritualBody == null) spiritualBody = (SpiritualBody) CoreAbility.getAbility(SpiritualBody.class);

            Player player = (Player) event.getEntity();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(SpiritElement.NEUTRAL)
                    && bPlayer.canUsePassive(spiritualBody) && bPlayer.canBendPassive(spiritualBody)) {
                event.setDamage(0D);
                event.setCancelled(true);
            }
        }
    }

}