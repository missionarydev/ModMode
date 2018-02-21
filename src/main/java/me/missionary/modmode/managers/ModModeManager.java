package me.missionary.modmode.managers;

import com.google.common.collect.Maps;
import me.missionary.modmode.ModMode;
import me.missionary.modmode.actions.Actions;
import me.missionary.modmode.commands.StaffChatCommand;
import me.missionary.modmode.utils.Constants;
import lombok.Getter;
import me.missionary.modmode.managers.items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class ModModeManager implements Listener {

    public static final Set<UUID> staffModeSet = new HashSet<>();

    private final ModMode plugin = ModMode.getPlugin();

    @Getter
    private final Set<UUID> onlineStaff;

    @Getter
    private final Map<UUID, Entity> despawnMap;

    private final Map<UUID, InventorySnapshot> inventorySnapshot = Maps.newHashMap();

    private final Map<Class<? extends StaffModeItem>, StaffModeItem> staffModeItems;

    public ModModeManager() {
        staffModeItems = new HashMap<>();
        despawnMap = new HashMap<>();
        onlineStaff = new HashSet<>();
        staffModeItems.put(HaltPlayerItem.class, new HaltPlayerItem());
        staffModeItems.put(InventorySeeItem.class, new InventorySeeItem());
        staffModeItems.put(RandomTeleportItem.class, new RandomTeleportItem());
        staffModeItems.put(PhaseTool.class, new PhaseTool());
        staffModeItems.put(VanishOnItem.class, new VanishOnItem());
        staffModeItems.put(VanishOffItem.class, new VanishOffItem());
        staffModeItems.put(StaffOnlineItem.class, new StaffOnlineItem());
        staffModeItems.put(WorldEditWandItem.class, new WorldEditWandItem());
        Bukkit.getPluginManager().registerEvents(this, ModMode.getPlugin());
    }

    /**
     * Gets an {@link StaffModeItem}.
     *
     * @param type the Staff mode item type.
     * @return the staff mode item.
     */
    public StaffModeItem getStaffModeItem(Class<? extends StaffModeItem> type) {
        return staffModeItems.get(type);
    }

    public boolean isInStaffMode(Player player) {
        return staffModeSet.contains(player.getUniqueId());
    }

    /**
     * Enables staff mode for an {@link Player}
     *
     * @param player The player to enable staff mode for.
     */
    public void enableStaffMode(Player player) {
        plugin.getLogger().info(player.getName() + " has entered Mod Mode at: " + Constants.DATE_FORMAT.format(System.currentTimeMillis()));
        if (!isInStaffMode(player)) {
            UUID uuid = player.getUniqueId();
            staffModeSet.add(uuid);

            // Log inventory
            PlayerInventory inventory = player.getInventory();
            inventorySnapshot.put(player.getUniqueId(), new InventorySnapshot(player.getInventory().getContents(), player.getInventory().getArmorContents()));
            inventory.clear();
            inventory.setArmorContents(new ItemStack[]{null, null, null, null});

            plugin.getVanishManager().vanish(player, true);
            // Apply items to player
            boolean isPlayerVanished = plugin.getVanishManager().isVanished(player.getUniqueId());
            staffModeItems.values().stream().filter(staffModeItem -> (staffModeItem.getClass() != VanishOnItem.class || isPlayerVanished) && (staffModeItem.getClass() != VanishOffItem.class || !isPlayerVanished)).forEach(staffModeItem -> staffModeItem.applyTo(player));


            // Update inventory
            player.updateInventory();

            // Give player creative
            player.setGameMode(GameMode.CREATIVE);

            // Give the player some metadata stating that they are in ModMode.
            player.setMetadata("ModMode", new FixedMetadataValue(plugin,true));
        }
    }

    /**
     * Disabled staff mode for an {@link Player}
     *
     * @param player The player to disable staff mode for.
     */
    public void disableStaffMode(Player player) {
        if (isInStaffMode(player)) {
            plugin.getLogger().info(player.getName() + " has left Mod Mode at: " + Constants.DATE_FORMAT.format(System.currentTimeMillis()));

            UUID uuid = player.getUniqueId();
            staffModeSet.remove(uuid);

            // Remove items from player
            PlayerInventory inventory = player.getInventory();
            staffModeItems.values().forEach(staffModeItem -> staffModeItem.removeFrom(player));

            // Restore inventory
            inventory.clear();
            InventorySnapshot i = inventorySnapshot.get(uuid);
            inventory.setContents(i.getInventoryContents());
            inventory.setArmorContents(i.getArmourContents());
            inventorySnapshot.remove(uuid);

            // Update inventory
            player.updateInventory();
            plugin.getVanishManager().unvanish(player, true);

            // Give player the appropriate gamemode based on their permissions.
            if (player.hasPermission("command.gamemode")) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }

            // Remove the modmode metadata
            player.removeMetadata("ModMode", plugin);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getPlayer().hasPermission("staff") && !onlineStaff.contains(event.getPlayer().getUniqueId())) {
            onlineStaff.add(event.getPlayer().getUniqueId());
            plugin.getActionManager().sendStaffStatusMessage(event.getPlayer(), Actions.STAFF_JOIN);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAsyncChatEvent(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if (StaffChatCommand.staffChatMap.contains(player.getUniqueId())){
            plugin.getActionManager().createAndSendStaffChat(player.getName(), event.getMessage());
            event.setCancelled(true);
            event.setMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isInStaffMode(event.getPlayer())) {
            disableStaffMode(event.getPlayer());
        }
        if (onlineStaff.contains(event.getPlayer().getUniqueId())) {
            onlineStaff.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isInStaffMode(player) && !player.hasPermission("command.mod.bypass")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks whilst in mod mode!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        if (isInStaffMode(player) && !player.hasPermission("command.mod.bypass")) {
            player.sendMessage(ChatColor.RED + "You cannot interact with entities whilst in mod mode!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if (isInStaffMode(player) && player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("command.mod.bypass")) {
            event.setCancelled(true);
        }
    }
}
