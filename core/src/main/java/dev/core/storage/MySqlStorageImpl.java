package dev.core.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.core.Parkour;
import dev.core.api.Config;
import dev.core.api.Storage;
import dev.core.api.map.ParkourMap;
import dev.core.api.structure.ParkourMapManager;
import dev.core.api.users.User;
import dev.core.core.users.UserImpl;
import dev.core.records.MapRecord;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySqlStorageImpl implements Storage<HikariDataSource> {

    private HikariDataSource hikariDataSource;


    @SneakyThrows
    @Override
    public void init(Config config) {
        YamlConfiguration yml = config.getConfig();
        String host = yml.getString("host");
        int port = yml.getInt("port");
        int poolsize = yml.getInt("pool-size");
        String password = yml.getString("password");
        String username = yml.getString("username");
        String database = yml.getString("database");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("Parkour");
        hikariConfig.setMaximumPoolSize(poolsize);
        hikariConfig.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

        Properties properties = new Properties();
        properties.put("serverName", host);
        properties.put("port", port);
        properties.put("databaseName", database);
        properties.put("user", username);
        properties.put("password", password);
        hikariConfig.setDataSourceProperties(properties);
        this.hikariDataSource = new HikariDataSource(hikariConfig);
        try (Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.addBatch(
                    "CREATE TABLE IF NOT EXISTS " + "park_"
                            + "completed_maps (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, uuid VARCHAR(36) NOT NULL," +
                            " map_name VARCHAR(32) NOT NULL, best_time_ms BIGINT, completion_count INT, games_played_count INT)"
            );
            statement.executeBatch();
        }
    }

    @Override
    public HikariDataSource getConnections() {
        return hikariDataSource;
    }
    @SneakyThrows
    @Override
    public CompletableFuture<User> fetchRecordNow(UUID uuid) {
     return CompletableFuture.supplyAsync(() -> {
        UserImpl user = new UserImpl(uuid);
            try (final Connection connection = getConnections().getConnection();
                 final PreparedStatement statement = connection.prepareStatement(
                         "SELECT map_name, best_time_ms, completion_count, games_played_count FROM " + "park_" + "completed_maps WHERE uuid=?"
                 )) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        final String mapId = resultSet.getString("map_name");
                        final int lowestRecord = resultSet.getInt("best_time_ms");
                        final int gamesPlayed = resultSet.getInt("games_played_count"),
                                completions = resultSet.getInt("completion_count");
                        final ParkourMapManager mapRegistry = Parkour.getInstance().getManager();
                        final ParkourMap map = mapRegistry.getParkourMap(mapId);
                        if (map != null) {
                            CompletableFuture<Integer> playersWithBetterTimeCount = getPlayersWithBetterTimeCount(mapId, lowestRecord);
                            try {
                                user.cacheData(map, lowestRecord, completions, gamesPlayed, playersWithBetterTimeCount.get());
                            }catch (Exception exception){
                                throw new RuntimeException("could not load the betterCount" + exception);
                            }
                        }
                    }
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Could not load data for " + uuid, exception);
            }
            return user;
        });
    }

    @Override
    public CompletableFuture<Void> updateRecord(UUID uuid, MapRecord record) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = hikariDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE " + "park_"
                                 + "completed_maps SET best_time_ms=?, completion_count=?, games_played_count=? WHERE map_name=? AND uuid=?"
                 )) {
                statement.setLong(1, record.getLowestRecord());
                statement.setInt(2, record.getCompletions());
                statement.setInt(3, record.getGamesPlayed());
                statement.setString(4, record.getMap().getKey());
                statement.setString(5, uuid.toString());
                statement.executeUpdate();
                if (statement.getUpdateCount() == 0) {
                    PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO " + "park_"
                                    + "completed_maps (uuid, map_name, best_time_ms, completion_count, games_played_count) VALUES (?, ?, ?, ?, ?)"
                    );
                    insertStatement.setString(1, uuid.toString());
                    insertStatement.setString(2, record.getMap().getKey());
                    insertStatement.setLong(3, record.getLowestRecord());
                    insertStatement.setInt(4, record.getCompletions());
                    insertStatement.setInt(5, record.getGamesPlayed());
                    insertStatement.executeUpdate();
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Could not update record for " + uuid, exception);
            }
        });
    }
    @Override
    public CompletableFuture<Integer> getPlayersWithBetterTimeCount(String mapName, long playerBestTime) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = hikariDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT COUNT(*) FROM " + "park_" + "completed_maps WHERE map_name=? AND best_time_ms <= ?"
                 )) {
                statement.setString(1, mapName);
                statement.setLong(2, playerBestTime);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Could not retrieve players count for " + mapName, exception);
            }
            return 0;
        });
    }

    @Override
    public CompletableFuture<Void> clearRecords(ParkourMap map) {
        return null;
    }


    @SneakyThrows
    @Override
    public Map<UUID,TopPlayerData> getTopPlayers(ParkourMap mapName){
        Map<UUID,TopPlayerData> users = new HashMap<>();
            if (mapName == null) {
                return null;
            }

            try (Connection connection = hikariDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT uuid, best_time_ms " +
                                 "FROM park_completed_maps " +
                                 "WHERE map_name = ? " +
                                 "ORDER BY best_time_ms ASC LIMIT 200"
                 )) {
                statement.setString(1, mapName.id());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    // First, add the parkour name
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    long time = resultSet.getLong("best_time_ms");
                    UserImpl user = new UserImpl(uuid);
                    String name = Bukkit.getOfflinePlayer(user.uuid()).getName();
                    CompletableFuture<Integer> pos = getPlayersWithBetterTimeCount(mapName.id(), time);
                    TopPlayerData topPlayerData = new TopPlayerData(uuid,name,time,pos.join());
                    users.put(uuid,topPlayerData);
                }
            }catch (SQLException exception) {
                throw new RuntimeException("Could not retrieve players count for " + mapName, exception);
            }
            return users;
    }
}
