package dev.core.scoreboard.manager;

import dev.core.Parkour;
import dev.core.api.Config;
import dev.core.api.map.ParkourMap;
import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;
import dev.core.records.MapRecord;
import dev.core.utils.TimeUtil;
import fr.mrmicky.fastboard.FastBoard;
import games.negative.framework.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScoreManagerImpl implements ScoreManager{
    private final Config config;

    public ScoreManagerImpl(Config config){
        this.config = config;
    }
    private Map<UUID, FastBoard> boards = new HashMap<>();

    @Override
    public List<String> Replacable(MapRecord mapRecord, ParkourSession parkourSession) {
        List<String> score = config.getConfig().getStringList("score_lines");
        int playerPosition = (mapRecord != null) ? mapRecord.getPosition() : +9999;

        long currentTimeMillis = System.currentTimeMillis();
        int remainingTimeInSeconds = parkourSession.getCurrentMap().StartTimeLeft();
        long elapsedTimeInSeconds = (currentTimeMillis - parkourSession.getStartTime()) / 1000;

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

    @Override
    public Map<UUID, FastBoard> boards() {
        return boards;
    }

    @Override
    public void setAllData(Player player, @NotNull MapRecord mapRecord, @NotNull ParkourSession parkourSession) {
        List<String> scoreLines = Replacable(mapRecord, parkourSession);
        FastBoard fastBoard = boards.get(player.getUniqueId());
        for (int i = 0; i < scoreLines.size(); i++) {
            String scoreText = scoreLines.get(i);
            fastBoard.updateLine(i, Utils.color(scoreText));
        }
    }

    @Override
    public void addScoreBoard(@NotNull Player player, @NotNull MapRecord mapRecord, @NotNull User user, @NotNull ParkourSession parkourSession) {
        FastBoard fastBoard = new FastBoard(player);
        String title = config.getConfig().getString("title").replace("%map%",parkourSession.getCurrentMap().getDisplayName());
        fastBoard.updateTitle(title);
        boards.put(user.uuid(), fastBoard);
        setAllData(player,mapRecord,parkourSession);
    }

    @Override
    public void updateScoreboards() {
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
}
