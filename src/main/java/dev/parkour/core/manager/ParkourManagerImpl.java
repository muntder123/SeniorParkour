package dev.parkour.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.LocationTypeAdapter;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.holograms.Holo;
import dev.parkour.holograms.HoloManager;
import dev.parkour.maps.MapImpl;
import dev.parkour.maps.points.CheckPointMap;
import dev.parkour.maps.points.PointAdapter;
import dev.parkour.maps.points.PointMap;
import games.negative.framework.util.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ParkourManagerImpl implements ParkourMapManager {

    private final Gson gson;

    private final Map<String, ParkourMap> maps;
    private Parkour instance;
    private final HoloManager<?> hologramManager;


    public ParkourManagerImpl(Parkour parkour) {
        this.instance = parkour;
        this.hologramManager = parkour.getHologramManager();
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(
                        PointMap.class, new LocationTypeAdapter()).registerTypeAdapter(CheckPointMap.class, new PointAdapter()).create();
        this.maps = new HashMap<>();
    }

    @Override
    public Map<String, ParkourMap> maps() {
        return maps;
    }

    @Override
    public ParkourMap getParkourMap(@NotNull String key) {
        return maps.getOrDefault(key, null);
    }

    @Override
    public void registerMap(@NotNull ParkourMap parkourMap) {
        maps.put(parkourMap.id(), parkourMap);
    }

    @Override
    public void unRegisterMap(@NotNull ParkourMap parkourMap) {
        maps.remove(parkourMap.id(), parkourMap);
    }

    @Override
    public boolean isRegistered(ParkourMap parkourMap) {
        return maps.containsKey(parkourMap.id());
    }

    @SneakyThrows
    @Override
    public void loadMaps(Config config) {
        this.maps.clear();
        File dataFolder = instance.getDataFolder();
        File mapsFolder = new File(dataFolder, "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        File[] files = mapsFolder.listFiles();

        if (files == null)
            return;

        for (File file : files) {
            if (file.getName().endsWith(".json") && !file.getName().contains("config")) {
                try (Reader reader = new FileReader(file)) {
                    ParkourMap parkourMap = gson.fromJson(reader, MapImpl.class);
                    registerMap(parkourMap);
                    System.out.println("Loading map : " +parkourMap.id());
                }
            }
        }
    }

    @Override
    public void setupHolo(Config config){

        double checkPointDouble = config.getConfig().getDouble("checkpoint-hologram.y-offset");
        double startPointDouble = config.getConfig().getDouble("start-hologram.y-offset");
        double endPointDouble = config.getConfig().getDouble("end-hologram.y-offset");
        for (ParkourMap map : getMaps()) {
            if(map == null)continue;
            PointMap startLocation = map.getStartLocation();
            PointMap endLocation = map.getEndLocation();
            if (hologramManager != null) {

                Bukkit.getScheduler().runTask(Parkour.getInstance(),() -> {
                    map.getPoints().forEach(checkPointMap -> {
                        int order = checkPointMap.getOrder();
                        List<String> holoorderslist = config.getConfig().getStringList("checkpoint-hologram.hologram-name")
                                .stream().map(s -> s.replace("%order%",String.valueOf(order))).collect(Collectors.toList());
                        Location clone = checkPointMap.getLocation().clone();
                        Holo orders = createHolo("orders-"+map.getId() +""+ order, clone.add(0,checkPointDouble,0));
                        orders.updateLines(holoorderslist);
                        orders.showAll();
                    });

                    List<String> stringList = config.getConfig().getStringList("start-hologram.hologram-name");
                    Location startloc = startLocation.getLocation().clone();
                    Holo start = createHolo(Utils.color("start-holo"+map.getId()),
                            startloc.add(0,startPointDouble,0));
                    start.updateLines(stringList);
                    start.showAll();

                    Location clone = endLocation.getLocation().clone();
                    Holo end = createHolo(Utils.color("End-location"+map.getId()),
                            clone.add(0,endPointDouble,0));
                    List<String> endList = config.getConfig().getStringList("end-hologram.hologram-name");
                    end.updateLines(endList);
                    end.showAll();
                });

            }
        }
    }
    private Holo createHolo(String name, Location location){
        return hologramManager.createHologram(name, location);
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
    public void deleteMap(ParkourMap parkourMap, boolean deleteFile) {
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
