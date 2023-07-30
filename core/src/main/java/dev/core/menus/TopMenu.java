package dev.core.menus;

import dev.core.Parkour;
import dev.core.api.Storage;
import dev.core.api.map.ParkourMap;
import dev.core.api.structure.ParkourMapManager;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import dev.core.storage.TopPlayerData;
import dev.core.utils.TimeUtil;
import games.negative.framework.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TopMenu extends GUI {
    private final ParkourMapManager manager;
    private final UserManager userManager;
    private final Storage<?> storage;
    public TopMenu(ParkourMap map,int page) {
        super("Top users", 1);
        this.manager = Parkour.getInstance().getManager();
        this.userManager = Parkour.getInstance().getUserManager();
            this.storage = Parkour.getInstance().getStorage();
        int playersPerPage = 9;
        int startIndex = (page - 1) * playersPerPage;
        int endIndex = startIndex + playersPerPage;

        Map<UUID, TopPlayerData> topPlayers = storage.getTopPlayers(map);
        List<TopPlayerData> sortedTopPlayers = topPlayers.values().stream()
                .sorted(Comparator.comparingInt(TopPlayerData::getPosition))
                .collect(Collectors.toList());
        for (int i = startIndex; i < Math.min(endIndex, sortedTopPlayers.size()); i++) {
            TopPlayerData topUser = sortedTopPlayers.get(i);
            UUID uuid = topUser.getUuid();
            User user = userManager.getPlayer(uuid);

            String playerName = (user != null) ? topUser.getPlayerName() : Bukkit.getOfflinePlayer(uuid).getName();
            long time = topUser.getTime();
            int position = topUser.getPosition();

            ItemStack head = getPlayerHead(uuid, playerName, TimeUtil.formatTime(time, true), position);
            this.setItem(i - startIndex, player -> head);
        }
        if (endIndex < sortedTopPlayers.size()) {
            ItemStack nextPageItem = getNextPageItem();
            this.setItem(8, player -> nextPageItem);
        }
    }
    private ItemStack getPlayerHead(UUID playerUUID, String playerName, String formattedTime, int position) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(playerName);
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        List<String> lore = new ArrayList<>();
        lore.add(formattedTime + " - Position: " + position);
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack getNextPageItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Next Page");
        List<String> lore = new ArrayList<>();
        lore.add("Click to view the next page");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
