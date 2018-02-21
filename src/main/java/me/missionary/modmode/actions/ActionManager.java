package me.missionary.modmode.actions;

import me.missionary.modmode.ModMode;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Missionary on 7/14/2017.
 */
public class ActionManager {

    private final ModMode plugin;
    @Getter
    public AtomicBoolean isRequests;
    @Getter
    public AtomicBoolean isReports;

    public ActionManager(ModMode plugin) {
        this.plugin = plugin;
        this.isReports = new AtomicBoolean(true);
        this.isRequests = new AtomicBoolean(true);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = plugin.getJedisPool().getResource()) {
                isReports.set(!jedis.exists(Actions.DisableHelpActions.REPORTS.name()) || Boolean.parseBoolean(jedis.get(Actions.DisableHelpActions.REPORTS.name())));
                isRequests.set(!jedis.exists(Actions.DisableHelpActions.REQUESTS.name()) || Boolean.parseBoolean(jedis.get(Actions.DisableHelpActions.REQUESTS.name())));
            }
        });
    }

    public void createAndSendReport(String sender, String message, String reported) {
        JsonObject object = new JsonObject();
        object.addProperty("action", Actions.REPORT.name());
        JsonObject payload = new JsonObject();
        payload.addProperty("sender", sender);
        payload.addProperty("message", message);
        payload.addProperty("reported", reported);
        payload.addProperty("server", plugin.getConfigurationHandler().getServerID());
        object.add("payload", payload);
        plugin.publishToRedisAsync(object.toString());
        plugin.getLogger().info("Sent " + Actions.REPORT.name() + " to Redis. Payload -> " + object.toString());
    }

    public void createAndSendRequest(String sender, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("action", Actions.REQUEST.name());
        JsonObject payload = new JsonObject();
        payload.addProperty("sender", sender);
        payload.addProperty("message", message);
        payload.addProperty("server", plugin.getConfigurationHandler().getServerID());
        object.add("payload", payload);
        plugin.publishToRedisAsync(object.toString());
        plugin.getLogger().info("Sent " + Actions.REQUEST.name() + " to Redis. Payload -> " + object.toString());
    }

    public void createAndSendStaffChat(String sender, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("action", Actions.STAFFCHAT.name());
        JsonObject payload = new JsonObject();
        payload.addProperty("message", message);
        payload.addProperty("sender", sender);
        payload.addProperty("server", plugin.getConfigurationHandler().getServerID());
        object.add("payload", payload);
        plugin.publishToRedisAsync(object.toString());
        plugin.getLogger().info("Sent " + Actions.STAFFCHAT.name() + " to Redis. Payload -> " + object.toString());
    }

    public void sendStaffStatusMessage(Player player, Actions action) {
        JsonObject object = new JsonObject();
        object.addProperty("action", action.name());

        JsonObject payload = new JsonObject();
        payload.addProperty("player", player.getName());
        payload.addProperty("server", plugin.getConfigurationHandler().getServerID());

        object.add("payload", payload);

        plugin.publishToRedisAsync(object.toString());
        plugin.getLogger().info("Sent " + action.name() + " to Redis. Payload -> " + object.toString());
    }

    public void sendReportRequestStatusMessage(Actions.DisableHelpActions disableHelpActions, boolean status) {
        JsonObject object = new JsonObject();
        object.addProperty("action", Actions.DISABLE_HELP.name());

        JsonObject payload = new JsonObject();
        payload.addProperty("type", disableHelpActions.name());
        payload.addProperty("status", status);

        object.add("payload", payload);

        plugin.publishToRedisAsync(object.toString());
        plugin.getLogger().info("Sent " + Actions.DISABLE_HELP.name() + " to Redis. Payload -> " + object.toString());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = plugin.getJedisPool().getResource()) {
                jedis.set(disableHelpActions.name(), Boolean.toString(status)); // Keep this in memory in case a server reboots.
            }
        });
    }
}
