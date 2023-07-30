package dev.core.scoreboard;

import dev.core.Parkour;
import dev.core.api.Config;
import dev.core.api.map.ParkourMap;
import dev.core.api.map.events.UserGameEndEvent;
import dev.core.api.map.events.UserGameStartEvent;
import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;
import dev.core.records.MapRecord;
import dev.core.scoreboard.manager.ScoreManager;
import dev.core.scoreboard.manager.ScoreManagerImpl;
import dev.core.utils.TimeUtil;
import fr.mrmicky.fastboard.FastBoard;
import games.negative.framework.util.Utils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameScoreboard implements Listener {
    private final ScoreManager scoreManager;

    public GameScoreboard(Config config) {
        this.scoreManager = new ScoreManagerImpl(config);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Parkour.getInstance(), scoreManager::updateScoreboards,20L,5L);
    }

    @EventHandler
    public void ScoreBoardStartArena(UserGameStartEvent event) {
        User user = event.getUser();
        ParkourSession parkourSession = event.getParkourSession();
        ParkourMap currentMap = parkourSession.getCurrentMap();
        MapRecord mapRecord = user.getMapRecord(currentMap);
        Player player = Bukkit.getPlayer(user.uuid());
        scoreManager.addScoreBoard(player,mapRecord,user,parkourSession);
    }




    @EventHandler
    public void ScoreBoardArenaEnd(UserGameEndEvent event) {
        ParkourSession parkourSession = event.getParkourSession();
        if (parkourSession != null) {
            Player player = Bukkit.getPlayer(event.getUser().uuid());
            FastBoard remove = scoreManager.boards().get(player.getUniqueId());
            if(remove != null){
                remove.delete();
            }
        }
    }
}