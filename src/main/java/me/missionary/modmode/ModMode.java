package me.missionary.modmode;

import me.missionary.modmode.actions.ActionManager;
import me.missionary.modmode.actions.JedisSubscriberImpl;
import me.missionary.modmode.commands.*;
import me.missionary.modmode.commands.management.ManageHelpCommand;
import me.missionary.modmode.managers.ModModeManager;
import me.missionary.modmode.managers.ScreenshareManager;
import me.missionary.modmode.managers.VanishManager;
import me.missionary.modmode.utils.AbstactMenuListener;
import me.missionary.modmode.utils.commands.CommandFramework;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;

/**
 * Created by Missionary (missionarymc@gmail.com) on 3/26/2017.
 */
public class ModMode extends JavaPlugin {

    public static final String MODMODE_CHANNEL = "ModMode";

    @Getter
    private static ModMode plugin;

    @Getter
    private ConfigurationHandler configurationHandler;

    @Getter
    private ModModeManager modModeManager;

    @Getter
    private VanishManager vanishManager;

    @Getter
    private ScreenshareManager screenshareManager;

    @Getter
    private CommandFramework framework;

    @Getter
    private ActionManager actionManager;

    @Getter
    private JedisPool jedisPool;

    @Getter
    private Thread subscriberThread;

    @Override
    public void onEnable() {
        plugin = this;
        // Save the plugins default configuration.
        saveDefaultConfig();
        configurationHandler = new ConfigurationHandler(this);
        initializeRedis();
        initializeManagers();
        registerCommands();
    }


    private void initializeRedis() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, configurationHandler.getJedisAddress(), configurationHandler.getJedisPort(), 2000, configurationHandler.isJedisAuth() ? configurationHandler.getJedisPassword() : null);
        Jedis redis = new Jedis(configurationHandler.getJedisAddress(), configurationHandler.getJedisPort());
        if (configurationHandler.isJedisAuth()) {
            redis.auth(configurationHandler.getJedisPassword());
        }
        subscriberThread = new Thread(() -> redis.subscribe(new JedisSubscriberImpl(this), MODMODE_CHANNEL));
        subscriberThread.start();
    }


    private void registerCommands() {
        // Create a new CommandFramework instance and register the commands listed below.
        framework = new CommandFramework(this);
        framework.registerCommands(new ModModeCommand(this));
        framework.registerCommands(new HaltCommand(this));
        framework.registerCommands(new RemoveInventoryLockCommand(this));
        framework.registerCommands(new VanishCommand(this));
        framework.registerCommands(new DespawnCommand(this));
        framework.registerCommands(new ReportCommand(this));
        framework.registerCommands(new RequestCommand(this));
        framework.registerCommands(new StaffChatCommand(this));
        framework.registerCommands(new ManageHelpCommand(this));
    }


    private void initializeManagers() {
        // Initializes the managers.
        vanishManager = new VanishManager();
        modModeManager = new ModModeManager();
        screenshareManager = new ScreenshareManager();
        actionManager = new ActionManager(this);
        getServer().getPluginManager().registerEvents(new AbstactMenuListener(), this);
    }

    @Override
    public void onDisable() {
        // Disable staff mode for all of the players in the staffModeSet.
        new HashSet<>(ModModeManager.staffModeSet).forEach(uuid -> getModModeManager().disableStaffMode(Bukkit.getPlayer(uuid)));
        if (subscriberThread.isAlive()) {
            subscriberThread.interrupt();
            getLogger().info("Killed the Jedis Subscriber thread.");
        }
        jedisPool.close();
        plugin = null;
    }

    public void publishToRedisAsync(String message) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(MODMODE_CHANNEL, message);
            }
        });
    }
}
