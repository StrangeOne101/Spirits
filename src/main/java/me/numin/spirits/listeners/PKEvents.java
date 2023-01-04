package me.numin.spirits.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.PKListener;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.event.BendingPlayerCreationEvent;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.storage.DBConnection;
import me.numin.spirits.Spirits;
import me.numin.spirits.utilities.Methods;
import me.numin.spirits.SpiritElement;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
        if (event.getElement() == SpiritElement.DARK || event.getElement() == SpiritElement.LIGHT) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getTarget());

            if (event.getResult() == Result.REMOVE || event.getResult() == Result.PERMAREMOVE) {
                boolean removeSpirit = true;
                for (Element existing : bPlayer.getElements()) {
                    if (existing != event.getElement() && (existing == SpiritElement.DARK || existing == SpiritElement.LIGHT)) {
                        removeSpirit = false;
                        break;
                    }
                }
                if (removeSpirit) {
                    bPlayer.getElements().remove(SpiritElement.NEUTRAL);
                }
            } else if (event.getResult() == Result.ADD || event.getResult() == Result.CHOOSE) {
                boolean addSpirit = true;
                for (Element existing : bPlayer.getElements()) {
                    if (existing != event.getElement() && (existing == SpiritElement.DARK || existing == SpiritElement.LIGHT)) {
                        addSpirit = false;
                        break;
                    }
                }
                if (addSpirit) {
                    bPlayer.getElements().add(SpiritElement.NEUTRAL);
                }
            }

            bPlayer.removeUnusableAbilities();
            saveElements(bPlayer); //Use our own method due to async syncing causing the player's normal spirit element not to be saved


        } else if (event.getElement() == SpiritElement.NEUTRAL && event.getResult() == Result.CHOOSE) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getTarget());
            bPlayer.getElements().remove(SpiritElement.NEUTRAL);
            String s = Spirits.getInstance().getConfig().getString("Language.Errors.ChooseSpirit");
            if (!StringUtils.isEmpty(s)) GeneralMethods.sendBrandingMessage(event.getTarget(), s);
            saveElements(bPlayer);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (!ConfigManager.languageConfig.get().getBoolean("Chat.Enable") || bPlayer == null) {
            return;
        }

        boolean avatar = Methods.isAvatar(bPlayer);;
        ArrayList<Element> elements = new ArrayList<>(bPlayer.getElements());
        elements.remove(SpiritElement.NEUTRAL);
        String name = "Nonbender";
        ChatColor c = ChatColor.WHITE;

        if (avatar) {
            c = Element.AVATAR.getColor();
            name = Element.AVATAR.getName();
        } else if (elements.size() == 2 && bPlayer.hasElement(SpiritElement.DARK)
                && bPlayer.hasElement(SpiritElement.LIGHT)) {
            c = SpiritElement.NEUTRAL.getColor();
            name = SpiritElement.NEUTRAL.getName();
        } else if (elements.size() > 0) {
            c = elements.get(0).getColor();
            name = elements.get(0).getName();
        }
        final String element = ConfigManager.languageConfig.get().getString("Chat.Prefixes." + name);
        event.setFormat(event.getFormat().replace("{element}", c + element + ChatColor.RESET)
                .replace("{ELEMENT}", c + element + ChatColor.RESET)
                .replace("{elementcolor}", c + "").replace("{ELEMENTCOLOR}", c + ""));

        if (avatar) {
            c = ChatColor.of(ConfigManager.languageConfig.get().getString("Chat.Colors.Avatar"));
        }

        String format = ConfigManager.languageConfig.get().getString("Chat.Format");
        format = format.replace("<message>", "%2$s");
        format = format.replace("<name>", c + player.getDisplayName() + ChatColor.RESET);
        event.setFormat(format);
    }

    @EventHandler
    public void onPKReload(BendingReloadEvent event) {
        Bukkit.getScheduler().runTaskLater(Spirits.plugin, () -> {
            try {
                Spirits.plugin.getConfig().load(new File(Spirits.plugin.getDataFolder(), "config.yml"));
                HandlerList.unregisterAll(Spirits.plugin);
                Bukkit.getPluginManager().registerEvents(new Abilities(), Spirits.plugin);
                Bukkit.getPluginManager().registerEvents(new Passives(), Spirits.plugin);
                Bukkit.getPluginManager().registerEvents(new PKEvents(), Spirits.plugin);
                event.getSender().sendMessage(ChatColor.BLUE + "Reloaded Spirits v" + Spirits.plugin.getDescription().getVersion() + "!");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
                event.getSender().sendMessage(ChatColor.RED + "Failed to load the Spirits config: " + e.getLocalizedMessage());
            }
        }, 1L);
    }

    @EventHandler
    public void onBendingPlayerLoad(BendingPlayerCreationEvent event) {
        BendingPlayer bPlayer = event.getBendingPlayer();
        if (bPlayer.hasElement(SpiritElement.LIGHT) || bPlayer.hasElement(SpiritElement.DARK)) {
            if (!bPlayer.hasElement(SpiritElement.NEUTRAL)) {
                bPlayer.getElements().add(SpiritElement.NEUTRAL);
                saveElements(bPlayer);
            }
        }
    }

    /**
     * Forcefully saves elements this instant. Makes the database commit
     * @param bPlayer The bending player
     */
    private void saveElements(BendingPlayer bPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(Spirits.plugin, () -> {
            try {
                DBConnection.sql.getConnection().setAutoCommit(false);
                DBConnection.sql.getConnection().commit(); //Force the existing async stuff to commit
                bPlayer.saveElements();
                DBConnection.sql.getConnection().commit(); //Commit our element change
                DBConnection.sql.getConnection().setAutoCommit(true); //Turn autosync back on
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}