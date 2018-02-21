package me.missionary.modmode.utils;

import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/25/2017.
 */
public class Constants {
    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("E MMM dd h:mm:ssa z", TimeZone.getTimeZone("America/New_York"), Locale.ENGLISH);
    public static final String SERVER_NAME = Bukkit.getServerName();
    public static String currentTime = DATE_FORMAT.format(System.currentTimeMillis());
}
