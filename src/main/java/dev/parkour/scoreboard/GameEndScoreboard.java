package dev.parkour.scoreboard;

import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.events.UserGameEndEvent;
import dev.parkour.api.map.events.UserGameStartEvent;
import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;
import dev.parkour.records.MapRecord;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class GameEndScoreboard implements Listener {
    private ScoreBoardManager scoreBoardManager;
    private final Config config;

    public GameEndScoreboard(Config config) {
        this.config = config;
        String title = config.getConfig().getString("title");
        String name = config.getConfig().getString("name");
        scoreBoardManager = new ScoreBoardManager(title, name);
        scoreBoardManager.setTitle(title);

    }

    @EventHandler
    public void ScoreBoardStartArena(UserGameStartEvent event) {
        // if(parkourSession != null){
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.uuid());
        ParkourSession parkourSession = event.getParkourSession();
        ParkourMap currentMap = parkourSession.getCurrentMap();
        MapRecord mapRecord = user.getMapRecord(currentMap);
        setAllData(player,mapRecord,user,parkourSession);
    }


    public void setAllData(Player player,MapRecord mapRecord,User user, ParkourSession parkourSession){
        List<String> scoreLines = Replacable(mapRecord, user, parkourSession);
        for (int i = 0; i < scoreLines.size(); i++) {
            String scoreText = scoreLines.get(i);
            System.out.println("Score : " + scoreText);
            scoreBoardManager.setScore(player, i, scoreText);
        }
        Bukkit.getScheduler().runTask(Parkour.getInstance(), () -> {
            scoreBoardManager.showScoreboard(player);

        });
    }

    @EventHandler
    public void ScoreBoardArenaEnd(UserGameEndEvent event) {
        ParkourSession parkourSession = event.getParkourSession();
        if (parkourSession != null) {
            Player player = Bukkit.getPlayer(event.getUser().uuid());
            System.out.println("removing seassion");
            assert player != null;
            scoreBoardManager.hideScoreboard(player);
        }
    }

    @SneakyThrows
    private List<String> Replacable(MapRecord mapRecord, User user, ParkourSession parkourSession) {
        List<String> score = config.getConfig().getStringList("score_lines");

        int playerPosition = user.getPlayerPosition(
                parkourSession.getCurrentMap().getKey(),
                mapRecord.getLowestRecord()
        ).get();
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