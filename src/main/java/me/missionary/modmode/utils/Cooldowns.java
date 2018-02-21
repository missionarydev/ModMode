package me.missionary.modmode.utils;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/17/2017.
 */
public class Cooldowns {

    private static Map<String, Map<UUID, Long>> cooldown = Maps.newHashMap();

    public static void createCooldown(final String k) {
        if (cooldown.containsKey(k)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }

        cooldown.put(k, new HashMap<>());
    }

    public static Map<UUID, Long> getCooldownMap(final String k) {
        return cooldown.get(k);
    }

    public static void addCooldown(final String k, final Player p, final int seconds) {
        if (!cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }

        final long next = System.currentTimeMillis() + seconds * 1000L;
        cooldown.get(k).put(p.getUniqueId(), next);
    }

    public static boolean isOnCooldown(final String k, final Player p) {
        if (!cooldown.containsKey(k)) return false;

        Map<UUID, Long> map = cooldown.get(k);
        return map.containsKey(p.getUniqueId()) && System.currentTimeMillis() <= map.get(p.getUniqueId());
    }

    public static int getCooldownForPlayerInt(final String k, final Player p) {
        return (int) (cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForPlayerLong(final String k, final Player p) {
        return (int) (cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis());
    }

    public static void removeCooldown(final String k, final Player p) {
        if (!cooldown.containsKey(k)) {
            throw new IllegalArgumentException(String.valueOf(k) + " does not exist");
        }
        cooldown.get(k).remove(p.getUniqueId());
    }
}