package dev.parkour.scoreboard;

import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.events.UserGameEndEvent;
import dev.parkour.api.map.events.UserGameStartEvent;
import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;
import dev.parkour.records.MapRecord;
import dev.parkour.utils.TimeUtil;
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
    private final Config config;
    @NonNull
    private Map<UUID, FastBoard> boards = new HashMap<>();

    public GameScoreboard(Config config) {
        this.config = config;
        Bukkit.getScheduler().runTaskTimerAsynchronously(Parkour.getInstance(), this::updateScoreboards,20L,5L);

    }

    @EventHandler
    public void ScoreBoardStartArena(UserGameStartEvent event) {
        User user = event.getUser();
        ParkourSession parkourSession = event.getParkourSession();
        ParkourMap currentMap = parkourSession.getCurrentMap();
        MapRecord mapRecord = user.getMapRecord(currentMap);
        Player player = Bukkit.getPlayer(user.uuid());
        addScoreBoard(player,mapRecord,user,parkourSession);
    }


    private void addScoreBoard(@NotNull Player player, @NotNull MapRecord mapRecord,@NotNull User user,@NotNull ParkourSession parkourSession){
        FastBoard fastBoard = new FastBoard(player);
        String title = config.getConfig().getString("title").replace("%map%",parkourSession.getCurrentMap().getDisplayName());
        fastBoard.updateTitle(title);
        boards.put(user.uuid(), fastBoard);
        setAllData(player,mapRecord,parkourSession);
    }

    private void updateScoreboards() {
        for (FastBoard board : boards.values()) {
            Player player = board.getPlayer();
            if(board == null)continue;
            if (player == null) continue;
            User user = Parkour.getInstance().getUserManager().getPlayer(player.getUniqueId());
            if (user == null) continue;

            ParkourSession session = user.Session();
            if(session == null)continue;
            ParkourMap currentMap = session.getCurrentMap();
            if (currentMap == null) continue;
            MapRecord mapRecord = user.getMapRecord(currentMap);
            setAllData(player, mapRecord, session);
        }
    }



    public void setAllData(Player player,@NotNull MapRecord mapRecord,@NotNull ParkourSession parkourSession){
        List<String> scoreLines = Replacable(mapRecord, parkourSession);
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
    private List<String> Replacable(MapRecord mapRecord, ParkourSession parkourSession) {
        List<String> score = config.getConfig().getStringList("score_lines");
        int playerPosition = (mapRecord != null) ? mapRecord.getPosition() : +9999;

        long currentTimeMillis = System.currentTimeMillis();
        int remainingTimeInSeconds = parkourSession.getCurrentMap().StartTimeLeft();
        long elapsedTimeInSeconds = (currentTimeMillis - parkourSession.getStartTime()) / 1000;

        // Get the total time configured for the current map in seconds

        // Calculate the remaining time in seconds
       long remaining_time = Math.max(0, remainingTimeInSeconds - elapsedTimeInSeconds);
        List<String> replacedLines = new ArrayList<>();
        for (String s : score) {
            s = s.replace("%map_name%", parkourSession.getCurrentMap().getDisplayName()).replace("%parkour_time%",
                    TimeUtil.formatTime(remaining_time, true)).replace("%parkour_position%",
                    String.valueOf(playerPosition));
            replacedLines.add(s);
        }
        return replacedLines;
    }
}