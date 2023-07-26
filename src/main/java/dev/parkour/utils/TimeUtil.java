package dev.parkour.utils;

import org.bukkit.ChatColor;

public class TimeUtil {
    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    /**
     * @param millis Milliseconds to format.
     * @return Formatted time into string.
     * @author ChatGPT
     */
    public static String formatTime(long millis, boolean shortened) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        long days = millis / DAY;
        millis = millis % DAY;
        long hours = millis / HOUR;
        millis = millis % HOUR;
        long minutes = millis / MINUTE;
        millis = millis % MINUTE;
        long seconds = millis / SECOND;
        millis = millis % SECOND;

        StringBuilder sb = new StringBuilder();
        if (shortened) {
            boolean started = false;
            if (days > 0) {
                sb.append(days).append("d");
                started = true;
            }
            if (hours > 0) {
                sb.append(started ? " " : "").append(hours).append("h");
                started = true;
            }
            if (minutes > 0) {
                sb.append(started ? " " : "").append(minutes).append("m");
                started = true;
            }
            if (seconds > 0) {
                sb.append(started ? " " : "").append(seconds).append("s");
                started = true;
            }
            if (millis > 0) {
                sb.append(started ? " " : "").append(millis).append("ms");
            }
        } else {
            boolean isStarted = false;
            if (days > 0) {
                sb.append(days).append(" day").append(days > 1 ? "s" : "");
                isStarted = true;
            }
            if (hours > 0) {
                if (isStarted) {
                    sb.append(", ");
                }
                sb.append(hours).append(" hour").append(hours > 1 ? "s" : "");
                isStarted = true;
            }
            if (minutes > 0) {
                if (isStarted) {
                    sb.append(", ");
                }
                sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
                isStarted = true;
            }
            if (seconds > 0) {
                if (isStarted) {
                    sb.append(", ");
                }
                sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
                isStarted = true;
            }
            if (millis > 0) {
                if (isStarted) {
                    sb.append(" and ");
                }
                sb.append(millis).append(" millisecond").append(millis > 1 ? "s" : "");
            }
        }

        return sb.toString();
    }

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
