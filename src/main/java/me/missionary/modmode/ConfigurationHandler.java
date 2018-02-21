package me.missionary.modmode;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Created by Missionary (missionarymc@gmail.com) on 10/10/2017.
 */
@Getter
public class ConfigurationHandler {

    private final ModMode plugin;

    private String requestMessage;
    private String reportMessage;
    private String staffChatMessage;
    private String serverID;
    private String joinMessage;
    private String jedisAddress, jedisPassword;
    private int jedisPort;
    private boolean isJedisAuth;


    public ConfigurationHandler(ModMode plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        jedisAddress = plugin.getConfig().getString("jedis-address");
        jedisPort = plugin.getConfig().getInt("jedis-port", 6379);
        isJedisAuth = plugin.getConfig().getBoolean("jedis-isAuth", false);
        if (isJedisAuth) jedisPassword = plugin.getConfig().getString("jedis-password");
        joinMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("staffJoinMessage"));
        serverID = plugin.getServer().getServerName().equalsIgnoreCase("Unknown Server") ? "?" : plugin.getServer().getServerName();
        requestMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("request"));
        reportMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("report"));
        staffChatMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("staffChat"));
    }
}
