package me.missionary.modmode.actions;

import me.missionary.modmode.ModMode;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Missionary on 7/13/2017.
 */
public class JedisSubscriberImpl extends JedisPubSub {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final String STAFF_PERMISSION = "staff";

    private final ModMode plugin;

    public JedisSubscriberImpl(ModMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessage(String channel, String message) {
        JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
        Actions action = Actions.valueOf(object.get("action").getAsString());
        if (action == Actions.STAFFCHAT) {
            JsonObject payload = object.get("payload").getAsJsonObject();
            String msg = payload.get("message").getAsString();
            String sender = payload.get("sender").getAsString();
            String server = payload.get("server").getAsString();

            Runnable runnable = () -> plugin.getServer().getOnlinePlayers().stream().filter(o -> o.hasPermission(STAFF_PERMISSION)).forEach(o -> o.sendMessage(plugin.getConfigurationHandler().getStaffChatMessage().replace("%server%", server).replace("%message%", msg).replace("%sender%", sender)));
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        } else if (action == Actions.REPORT) {
            JsonObject payload = object.get("payload").getAsJsonObject();
            String sender = payload.get("sender").getAsString();
            String reportmessage = payload.get("message").getAsString();
            String reported = payload.get("reported").getAsString();
            String server = payload.get("server").getAsString();

            Runnable runnable = () -> plugin.getServer().getOnlinePlayers().stream().filter(o -> o.hasPermission(STAFF_PERMISSION)).forEach(o -> o.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[Report] " + reported + "&e has been reported by &3" + sender + "&e for &c" + reportmessage + "&e on &3" + server + "&e.")));
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        } else if (action == Actions.REQUEST) {
            JsonObject payload = object.get("payload").getAsJsonObject();
            String sender = payload.get("sender").getAsString();
            String reqmsg = payload.get("message").getAsString();
            String server = payload.get("server").getAsString();

            Runnable runnable = () -> plugin.getServer().getOnlinePlayers().stream().filter(o -> o.hasPermission(STAFF_PERMISSION)).forEach(o -> o.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3[Request] &c" + sender + "&e has requested &c" + reqmsg + "&e on &3" + server + "&e.")));
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        } else if (action == Actions.STAFF_JOIN) {
            JsonObject payload = object.get("payload").getAsJsonObject();
            String player = payload.get("player").getAsString();
            String server = payload.get("server").getAsString();

            Runnable runnable = () -> plugin.getServer().getOnlinePlayers().stream().filter(o -> o.hasPermission(STAFF_PERMISSION)).forEach(o -> o.sendMessage(plugin.getConfigurationHandler().getJoinMessage().replace("%player%", player).replace("%server%", server)));
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        } else if (action == Actions.DISABLE_HELP) {
            JsonObject payload = object.get("payload").getAsJsonObject();
            boolean status = payload.get("status").getAsBoolean();
            Actions.DisableHelpActions actions = Actions.DisableHelpActions.valueOf(payload.get("type").getAsString());
            Runnable runnable = () -> {
                switch (actions) {
                    case REPORTS: {
                        plugin.getActionManager().getIsRequests().set(status);
                        break;
                    }
                    case REQUESTS: {
                        plugin.getActionManager().getIsRequests().set(status);
                        break;
                    }
                }
            };
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        }
    }
}
