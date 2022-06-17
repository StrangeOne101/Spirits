package me.numin.spirits.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.PKListener;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import me.numin.spirits.Spirits;
import me.numin.spirits.config.Config;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.utilities.SpiritElement;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;


public class PKEvents implements Listener {

    private final Spirits plugin = Spirits.plugin;

    public PKEvents() {
        //Unregister the PK listener for the Async chat event, since we are inserting our own prefixes.
        // **laughs manically in hackiness**
        for (RegisteredListener listener : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) {
            if (listener.getListener().getClass().equals(PKListener.class)) {
                AsyncPlayerChatEvent.getHandlerList().unregister(listener.getListener());
            }
        }
    }

    @EventHandler
    public void elementChange(PlayerChangeElementEvent event) {
        //We want to add the Spirit element to players if they choose light or dark elements
        if (event.getElement() == SpiritElement.DARK_SPIRIT || event.getElement() == SpiritElement.LIGHT_SPIRIT) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getTarget());

            if (event.getResult() == Result.REMOVE) {
                boolean removeSpirit = true;
                for (Element existing : bPlayer.getElements()) {
                    if (existing != event.getElement() && (existing == SpiritElement.DARK_SPIRIT || existing == SpiritElement.LIGHT_SPIRIT)) {
                        removeSpirit = false;
                        break;
                    }
                }
                if (removeSpirit) {
                    bPlayer.getElements().remove(SpiritElement.SPIRIT);
                }
            } else if (event.getResult() == Result.ADD || event.getResult() == Result.CHOOSE) {
                boolean addSpirit = true;
                for (Element existing : bPlayer.getElements()) {
                    if (existing != event.getElement() && (existing == SpiritElement.DARK_SPIRIT || existing == SpiritElement.LIGHT_SPIRIT)) {
                        addSpirit = false;
                        break;
                    }
                }
                if (addSpirit) {
                    bPlayer.getElements().add(SpiritElement.SPIRIT);
                }
            }

            GeneralMethods.removeUnusableAbilities(bPlayer.getName());
            GeneralMethods.saveElements(bPlayer);


        } else if (event.getElement() == SpiritElement.SPIRIT && event.getResult() == Result.CHOOSE) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getTarget());
            bPlayer.getElements().remove(SpiritElement.SPIRIT);
            String s = Spirits.getInstance().getConfig().getString("Language.Errors.ChooseSpirit");
            if (!StringUtils.isEmpty(s)) GeneralMethods.sendBrandingMessage(event.getTarget(), s);
            GeneralMethods.saveElements(bPlayer);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        boolean avatar = false;
        ArrayList<Element> elements = new ArrayList<>();

        String e = "Nonbender";
        ChatColor c = ChatColor.WHITE;
        if (bPlayer != null) {
            avatar = Methods.isAvatar(bPlayer);
            elements = new ArrayList<>(bPlayer.getElements());
            elements.remove(SpiritElement.SPIRIT);
            if (avatar) {
                c = Element.AVATAR.getColor();
                e = Element.AVATAR.getName();
            } else if (elements.size() == 2 && bPlayer.hasElement(SpiritElement.DARK_SPIRIT)
                    && bPlayer.hasElement(SpiritElement.LIGHT_SPIRIT)) {
                c = SpiritElement.SPIRIT.getColor();
                e = SpiritElement.SPIRIT.getName();
            } else if (elements.size() > 0) {
                c = elements.get(0).getColor();
                e = elements.get(0).getName();
            }
        }
        final String element = ConfigManager.languageConfig.get().getString("Chat.Prefixes." + e);
        event.setFormat(event.getFormat().replace("{element}", c + element + ChatColor.RESET)
                .replace("{ELEMENT}", c + element + ChatColor.RESET)
                .replace("{elementcolor}", c + "").replace("{ELEMENTCOLOR}", c + ""));

        if (!ConfigManager.languageConfig.get().getBoolean("Chat.Enable")) {
            return;
        }

        if (bPlayer == null) {
            return;
        }

        if (avatar) {
            c = ChatColor.valueOf(ConfigManager.languageConfig.get().getString("Chat.Colors.Avatar"));
        }

        String format = ConfigManager.languageConfig.get().getString("Chat.Format");
        format = format.replace("<message>", "%2$s");
        format = format.replace("<name>", c + player.getDisplayName() + ChatColor.RESET);
        event.setFormat(format);
    }
}