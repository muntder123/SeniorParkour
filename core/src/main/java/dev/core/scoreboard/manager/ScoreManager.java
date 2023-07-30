package dev.core.scoreboard.manager;

import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;
import dev.core.records.MapRecord;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ScoreManager {

    List<String> Replacable(MapRecord mapRecord, ParkourSession parkourSession);
     Map<UUID, FastBoard> boards();
     void setAllData(Player player, @NotNull MapRecord mapRecord, @NotNull ParkourSession parkourSession);

    void addScoreBoard(@NotNull Player player, @NotNull MapRecord mapRecord, @NotNull User user, @NotNull ParkourSession parkourSession);

    void updateScoreboards();
}
