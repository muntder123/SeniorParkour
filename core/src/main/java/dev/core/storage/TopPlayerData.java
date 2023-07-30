package dev.core.storage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TopPlayerData {
    private final UUID uuid;
    private final String playerName;
    private final long time;
    private final int position;

    public TopPlayerData(UUID uuid,String playerName, long time, int position) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.time = time;
        this.position = position;
    }


}