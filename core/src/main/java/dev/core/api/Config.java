package dev.core.api;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private final File file;
    private final YamlConfiguration config;

    public Config(String name, Plugin plugin) {
        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            file.getParentFile().mkdir();
            plugin.saveResource(name, true);
        }

        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }


    public <T> List<T> get(String key, Class<T> type, List<T> defaultValue) {
        if (!config.contains(key)) {
            return defaultValue;
        }

        Object value = config.get(key);
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            List<T> result = new ArrayList<>();
            for (Object obj : list) {
                if (type.isInstance(obj)) {
                    result.add(type.cast(obj));
                } else {
                    result.add(defaultValue.get(0));
                }
            }
            return result;
        } else {
            return defaultValue;
        }
    }

}