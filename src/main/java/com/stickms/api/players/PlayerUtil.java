package com.stickms.api.players;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class PlayerUtil {
    public static @Nullable OfflinePlayer getOfflinePlayer(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return hasJoined(player) ? player : null;
    }
    public static @Nullable OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        return hasJoined(player) ? player : null;
    }
    public static boolean hasJoined(OfflinePlayer player){return player.isOnline() || player.hasPlayedBefore();}

    public static void banPlayer(OfflinePlayer player, String reason, String source, int time){
        banPlayer(player, reason, source, time, Calendar.HOUR);
    }
    public static void banPlayer(OfflinePlayer player, String reason, String source, int time, int timeUnit){
        if (time == 0){
            Bukkit.getBanList(BanList.Type.NAME).addBan(
                    player.getName(),
                    reason,
                    null, source);
        }else {
            Calendar cal = Calendar.getInstance();
            cal.add(timeUnit, time);
            Bukkit.getBanList(BanList.Type.NAME).addBan(
                    player.getName(),
                    reason,
                    cal.getTime(), source);
        }
        if (player.isOnline()) player.getPlayer().kickPlayer(reason);
    }
}
