package dev.parkour.menus;

import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.Storage;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.api.users.User;
import dev.parkour.api.users.UserManager;
import dev.parkour.records.MapRecord;
import dev.parkour.storage.TopPlayerData;
import dev.parkour.utils.TimeUtil;
import games.negative.framework.gui.GUI;
import games.negative.framework.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatsMenu extends GUI {

    private final ParkourMapManager parkourMapManager;
    private final UserManager userManager;
    private final Player player;
    private final Config config;
    private final Storage<?> storage;
    public StatsMenu(Player player, @NotNull String title, int rows) {
        super(title, rows);
        this.player = player;
        this.storage = Parkour.getInstance().getStorage();
        this.parkourMapManager = Parkour.getInstance().getManager();
        this.userManager = Parkour.getInstance().getUserManager();
        this.config = Parkour.getInstance().getMenus();
        setStatsItems(config);
    }

    public void setStatsItems(Config config){
        Collection<ParkourMap> maps = parkourMapManager.getMaps();
        int i = 0;
        for(ParkourMap parkourMap : maps){
            if(parkourMap == null)continue;
            User player1 = userManager.getPlayer(player.getUniqueId());

            MapRecord mapRecord = player1.getMapRecord(parkourMap);
            if(mapRecord == null)continue;
            String type = config.getConfig().getString("items.stats.type");
            String displayName = Objects.requireNonNull(config.getConfig().getString("items.stats.displayname")).replace("%map_id%",parkourMap.getDisplayName());
            ItemStack itemStack = new ItemStack(Material.valueOf(type));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null)continue;
            itemMeta.setDisplayName(Utils.color(displayName));
            List<String> lore = config.getConfig().
                    getStringList("items.stats.lore")
                    .stream().map(s -> s.replace("%time%",
                            TimeUtil.formatTime(mapRecord.getLowestRecord(), true)).replace("%position%",
                            String.valueOf(mapRecord.getPosition()))).collect(Collectors.toList());

            itemMeta.setLore(Utils.color(lore));
            itemStack.setItemMeta(itemMeta);
            setItem(i,player2 -> itemStack);
            i++;
        }
    }
}
