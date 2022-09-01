package me.numin.spirits.utilities;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.numin.spirits.SpiritElement;
import me.numin.spirits.Spirits;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpiritPlaceholder extends PlaceholderExpansion {

    public SpiritPlaceholder() {

    }

    @Override
    public String onPlaceholderRequest(final Player player, final String params) {
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) {
            return "";
        }

        List<Element> elements = new ArrayList<>(bPlayer.getElements());
        elements.remove(SpiritElement.NEUTRAL);

        if (params.equals("element") || params.equals("elementcolor")) {
            String e = "Nonbender";
            String c = ChatColor.WHITE + ""; //Changed to String to when the ChatColor is changed to the bungee one, it doesn't break

            if (Methods.isAvatar(bPlayer)) {
                c = Element.AVATAR.getColor() + "";
                e = Element.AVATAR.getName();
            } else if (elements.size() == 2 && bPlayer.hasElement(SpiritElement.DARK)
                    && bPlayer.hasElement(SpiritElement.LIGHT)) {
                c = SpiritElement.NEUTRAL.getColor() + "";
                e = SpiritElement.NEUTRAL.getName();
            } else if (elements.size() > 0) {
                c = elements.get(0).getColor() + "";
                e = elements.get(0).getName();
            }
            if (params.equals("element")) {
                return e;
            } else {
                return c;
            }
        } else if (params.equals("elements")) {
            return elements.stream().map(item -> item.getColor() + item.getName()).collect(Collectors.joining(" "));
        }

        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return Spirits.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "Spirits";
    }

    @Override
    public String getVersion() {
        return Spirits.getInstance().getDescription().getVersion();
    }
}
