package me.missionary.modmode.managers;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.managers.exceptions.InventoryLockException;
import me.missionary.modmode.utils.ItemBuilder;
import me.missionary.modmode.utils.Menu;
import me.missionary.modmode.utils.Utils;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
public class ScreenshareManager implements Listener {

    private final ModMode plugin = ModMode.getPlugin();
    private final Set<UUID> currentlyFrozen;
    private final Set<UUID> inventoryLocked;
    private final Menu menu;

    public ScreenshareManager() {
        Bukkit.getPluginManager().registerEvents(this, ModMode.getPlugin());
        currentlyFrozen = new HashSet<>();
        inventoryLocked = new HashSet<>();
        menu = new Menu(ChatColor.RED + "Halted!", 1);
        menu.setItem(4, new ItemBuilder(Material.ANVIL).setName(ChatColor.RED.toString() + ChatColor.BOLD + "You have been halted!").addLoreLine(ChatColor.YELLOW + "Please download Teamspeak 3 ").addLoreLine(ChatColor.YELLOW + "and join " + ChatColor.AQUA + plugin.getConfig().getString("teamspeakServerIP")).addLoreLine(ChatColor.YELLOW + "You have " + ChatColor.RED + 5 + " minutes " + ChatColor.YELLOW + "to join.").toItemStack());
        menu.fillRange(0, 3, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(DyeColor.RED.getData()).setName("").toItemStack());
        menu.fillRange(5, 8, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(DyeColor.RED.getData()).setName("").toItemStack());
        menu.runWhenEmpty(false);
    }

    public Set<UUID> getAllHaltedPlayers() {
        return Collections.unmodifiableSet(currentlyFrozen);
    }

    public boolean isHalted(Player player) {
        return currentlyFrozen.contains(player.getUniqueId());
    }

    public boolean isInventoryLocked(Player player) {
        return inventoryLocked.contains(player.getUniqueId());
    }

    /**
     * Halts an {@link Player}, freezing them.
     *
     * @param player The player to halt.
     */
    public void haltPlayer(Player player) {
        currentlyFrozen.add(player.getUniqueId());
        sendHaltedMessage(player);
        sendFrozenGUI(player);
        inventoryLocked.add(player.getUniqueId());
    }

    /**
     * Un-halts an {@link Player}, un-freezing them.
     *
     * @param player The player to un-halt.
     */
    public void unhaltPlayer(Player player) {
        currentlyFrozen.remove(player.getUniqueId());
        inventoryLocked.remove(player.getUniqueId());
        player.closeInventory();
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "You have been un-halted, thank you for your cooperation.");
    }

    /**
     * Removes the inventory lock from a {@link Player}.
     *
     * @param player The player to remove the lock from.
     * @throws InventoryLockException exception thrown if inventory is not locked
     */
    public void removeInventoryLock(Player player) throws InventoryLockException {
        if (isInventoryLocked(player)) {
            inventoryLocked.remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Your inventory lock has been removed however you are still required to join TS.");
        } else {
            throw new InventoryLockException(player.getUniqueId() + " was not inventory locked and something is very broken");
        }
    }

    private void sendFrozenGUI(Player player) {
        menu.showMenu(player);
    }


    private void sendHaltedMessage(Player player) {
        new BukkitRunnable() {
            public void run() {
                if (player.isOnline() && currentlyFrozen.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + Strings.repeat('-', 55));
                    player.sendMessage(ChatColor.RED + "You have been " + ChatColor.DARK_RED + "HALTED" + ChatColor.RED + " by a staff member.");
                    player.sendMessage(ChatColor.RED + "If you log out you will be " + ChatColor.DARK_RED + "BANNED" + ChatColor.RED + '.');
                    player.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + "5 minutes" + ChatColor.RED + " to join the TeamSpeak 3 Server.");
                    player.sendMessage(ChatColor.RED + "TeamSpeak 3 IP: " + ChatColor.GRAY + plugin.getConfig().getString("teamspeakServerIP") + ChatColor.RED + '.');
                    player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + Strings.repeat('-', 55));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0, 10 * 20);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player attacker = Utils.getFinalAttacker(event, false);
            if (attacker == null) {
                return;
            }
            final Player player = (Player) entity;
            if (currentlyFrozen.contains(player.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + player.getName() + " has been halted by a staff member and is being checked for cheats.");
                event.setCancelled(true);
                return;
            }
            if (currentlyFrozen.contains(attacker.getUniqueId())) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You may not attack players whilst halted.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if (e instanceof Player) {
            Player player = (Player) e.getEntity();
            if (currentlyFrozen.contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        final Player player = event.getPlayer();
        if (currentlyFrozen.contains(player.getUniqueId())) {
            event.setTo(event.getFrom());
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (currentlyFrozen.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks whilst halted.");
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (currentlyFrozen.contains(player.getUniqueId()) && inventoryLocked.contains(player.getUniqueId())) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendFrozenGUI(player), 10L);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (currentlyFrozen.contains(e.getPlayer().getUniqueId())) {
            Bukkit.getOnlinePlayers().stream().filter(online -> online.hasPermission("command.freeze") || online.isOp()).forEach(online -> online.sendMessage(ChatColor.RED + "The player " + e.getPlayer().getName() + " has logged off whilst halted."));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("*") || e.getPlayer().isOp()) {
           plugin.getServer().getLogger().info(e.getPlayer().getName() + " has joined with * and/or is opped.");
        }
        if (currentlyFrozen.contains(e.getPlayer().getUniqueId())) {
            sendHaltedMessage(e.getPlayer());
            if (inventoryLocked.contains(e.getPlayer().getUniqueId())) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> sendFrozenGUI(e.getPlayer()), 5);
            }
            Bukkit.getOnlinePlayers().stream().filter(online -> online.hasPermission("command.freeze")).forEach(online -> online.sendMessage(ChatColor.DARK_AQUA + e.getPlayer().getName() + ChatColor.AQUA + " has logged on whilst still halted. Perhaps the user was previously banned? You may unfreeze the user by using /halt <playerName>."));
        }
    }
}
