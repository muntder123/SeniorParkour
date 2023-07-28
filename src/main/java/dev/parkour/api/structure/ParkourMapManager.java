package dev.parkour.api.structure;

import dev.parkour.api.Config;
import dev.parkour.api.map.ParkourMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public interface ParkourMapManager {

    Map<String, ParkourMap> maps();

    ParkourMap getParkourMap(@NotNull String key);

    void registerMap(@NotNull ParkourMap parkourMap);
    void unRegisterMap(@NotNull ParkourMap parkourMap);

    boolean isRegistered(ParkourMap parkourMap);
    void setupHolo(Config config);
    void loadMaps(Config config);

    void saveMap(@NotNull  ParkourMap parkourMap);
    void saveAllMaps();

    void DeleteMap(ParkourMap parkourMap,boolean deleteFile);

    Collection<ParkourMap> getMaps();
}
