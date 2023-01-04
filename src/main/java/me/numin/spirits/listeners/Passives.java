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

public class Passives implements Listener {

    private final SpiritualBody spiritualBody;

    public Passives() {
        spiritualBody = (SpiritualBody) CoreAbility.getAbility(SpiritualBody.class);
    }

    @EventHandler(priority = EventPriority.HIGH) //HIGH will make sure air and earth passives have already run
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (spiritualBody == null) return;

            if (!spiritualBody.isEnabled()) return;

            Player player = (Player) event.getEntity();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(SpiritElement.NEUTRAL)
                    && bPlayer.canUsePassive(spiritualBody) && bPlayer.canBendPassive(spiritualBody)) {
                double newDamage = event.getDamage() * SpiritualBody.getFallDamageModifier();
                event.setDamage(newDamage);
                event.setCancelled(newDamage <= 0);
            }
        }
    }

}