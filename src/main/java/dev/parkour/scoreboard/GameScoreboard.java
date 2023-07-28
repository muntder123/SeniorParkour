package dev.parkour.scoreboard;

import dev.parkour.api.Config;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.events.UserGameEndEvent;
import dev.parkour.api.map.events.UserGameStartEvent;
import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;
import dev.parkour.records.MapRecord;
import fr.mrmicky.fastboard.FastBoard;
import games.negative.framework.util.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class GameScoreboard implements Listener {
    private final Config config;
    private Map<UUID, FastBoard> boards = new HashMap<>();

    public GameScoreboard(Config config) {
        this.config = config;

    }

    @EventHandler
    public void ScoreBoardStartArena(UserGameStartEvent event) {
        // if(parkourSession != null){
        String title = config.getConfig().getString("title");
        User user = event.getUser();
        Player player1 = Bukkit.getPlayer(user.uuid());
        FastBoard fastBoard = new FastBoard(player1);
        fastBoard.updateTitle(title);
        Player player = Bukkit.getPlayer(user.uuid());
        ParkourSession parkourSession = event.getParkourSession();
        ParkourMap currentMap = parkourSession.getCurrentMap();
        MapRecord mapRecord = user.getMapRecord(currentMap);
        boards.put(event.getUser().uuid(), fastBoard);
        setAllData(player,mapRecord,user,parkourSession);
    }


    public void setAllData(Player player,MapRecord mapRecord,User user, ParkourSession parkourSession){

        List<String> scoreLines = Replacable(mapRecord, user, parkourSession);
        if(scoreLines == null){
            return;
        }
        FastBoard fastBoard = boards.get(player.getUniqueId());
        for (int i = 0; i < scoreLines.size(); i++) {
            String scoreText = scoreLines.get(i);
            fastBoard.updateLine(i, Utils.color(scoreText));
        }
    }

    @EventHandler
    public void ScoreBoardArenaEnd(UserGameEndEvent event) {
        ParkourSession parkourSession = event.getParkourSession();
        if (parkourSession != null) {
            Player player = Bukkit.getPlayer(event.getUser().uuid());
            FastBoard remove = boards.get(player.getUniqueId());
            if(remove != null){
                remove.delete();
            }
        }
    }

    @SneakyThrows
    private List<String> Replacable(MapRecord mapRecord, User user, ParkourSession parkourSession) {
        List<String> score = config.getConfig().getStringList("score_lines");
        if(user == null){
            return null;
        }
        if(parkourSession == null){
            return null;
        }
        if(mapRecord == null){
            return null;
        }
        int playerPosition = user.getPlayerPosition(
                parkourSession.getCurrentMap().getKey(),
                mapRecord.getLowestRecord()
        ).get();
        if(playerPosition <= 0){
            return null;
        }
        List<String> replacedLines = new ArrayList<>();

        for (String s : score) {
            s = s.replace("%map_name%", parkourSession.getCurrentMap().getDisplayName()).replace("%user_best_time%",
                    String.valueOf(mapRecord.getLowestRecord())).replace("%parkour_position%",
                    String.valueOf(playerPosition));
            replacedLines.add(s);
        }
        return replacedLines;
    }
}