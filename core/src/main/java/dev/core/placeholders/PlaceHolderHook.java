package dev.core.placeholders;

import dev.core.Parkour;
import dev.core.api.map.ParkourMap;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import games.negative.framework.util.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderHook extends PlaceholderExpansion {


    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        final Parkour api = Parkour.getInstance();
        final UserManager usermanager = api.getUserManager();
        if (identifier.startsWith("name")) {
            User parkourPlayer = usermanager.getPlayer(player.getUniqueId());
            ParkourMap map = parkourPlayer.Session().getCurrentMap();
            if (map != null) {
                String displayName = map.getDisplayName();
                return Utils.color(displayName);
            } else {
                return "NaN";
            }
        }
        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "parkour";
    }

    @Override
    public @NotNull String getAuthor() {
        return "m_iq";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}
