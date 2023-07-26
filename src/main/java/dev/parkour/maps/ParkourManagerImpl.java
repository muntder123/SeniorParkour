package dev.parkour.maps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.parkour.Parkour;
import dev.parkour.api.LocationTypeAdapter;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.maps.points.PointMap;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class ParkourManagerImpl implements ParkourMapManager {

    private final Gson gson;

    private final Map<String,ParkourMap> maps;
    private Parkour instance;


    public ParkourManagerImpl(Parkour parkour){
        this.instance = parkour;
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(
                        PointMap.class, new LocationTypeAdapter()).create();
        this.maps = new HashMap<>();
    }

    @Override
    public Map<String, ParkourMap> maps() {
        return maps;
    }

    @Override
    public ParkourMap getParkourMap(@NotNull String key) {
        return maps.getOrDefault(key,null);
    }

    @Override
    public void registerMap(@NotNull ParkourMap parkourMap) {
        maps.put(parkourMap.id(),parkourMap);
    }

    @Override
    public void unRegisterMap(@NotNull ParkourMap parkourMap) {
        maps.remove(parkourMap.id(),parkourMap);
    }

    @Override
    public boolean isRegistered(ParkourMap parkourMap) {
        return maps.containsKey(parkourMap.id());
    }

    @SneakyThrows
    @Override
    public void loadMaps() {
        this.maps.clear();
        File dataFolder = instance.getDataFolder();
        File mapsFolder = new File(dataFolder, "maps");
        if(!mapsFolder.exists()){
            mapsFolder.mkdirs();
        }
        File[] files = mapsFolder.listFiles();

        if (files == null)
            return;

        for (File file : files) {
            if (file.getName().endsWith(".json") && !file.getName().contains("config")) {
                try (Reader reader = new FileReader(file)) {
                    ParkourMap parkourMap = gson.fromJson(reader,MapImpl.class);
                    registerMap(parkourMap);
                    System.out.println("Loaded maps .. " );
                    System.out.println(parkourMap.id());
                }
            }
        }
    }

    @Override
    public void saveMap(@NotNull ParkourMap parkourMap) {
        File dataFolder = instance.getDataFolder();
        File mapsfolder = new File(dataFolder, "maps");
        if (!mapsfolder.exists()) {
            mapsfolder.mkdirs();
        }

        File file = new File(mapsfolder, parkourMap.getKey() + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(parkourMap, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAllMaps() {
        File dataFolder = instance.getDataFolder();
        File mapsFolder = new File(dataFolder, "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        for (ParkourMap map : getMaps()) {
            saveMap(map);
        }
    }

    @Override
    public void DeleteMap(ParkourMap parkourMap, boolean deleteFile) {
        File dataFolder = instance.getDataFolder();
        File maps = new File(dataFolder, "maps");
        if (!maps.exists()) {
            maps.mkdirs();
        }

        File[] files = maps.listFiles();
        if (files == null)
            return;

        File parkourFile = Arrays.stream(files)
                .filter(file -> file.getName().equalsIgnoreCase(parkourMap.getKey() + ".json"))
                .findFirst().orElse(null);

        if (parkourFile == null)
            return;

        if (deleteFile) {
            parkourFile.delete();
        } else {
            parkourFile.renameTo(new File(maps, parkourMap.getKey() + ".json.disabled"));
        }
        unRegisterMap(parkourMap);
    }

    @Override
    public Collection<ParkourMap> getMaps() {
        return Collections.unmodifiableCollection(maps().values());
    }
}
