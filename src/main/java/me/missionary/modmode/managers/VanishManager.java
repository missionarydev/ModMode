package me.missionary.modmode.managers;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.Utils;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
public class VanishManager implements Listener {
    private static final String INVENTORY_INTERACT_PERMISSION = "vanish.inventorysee";
    private static final String BLOCK_INTERACT_PERMISSION = "vanish.build";
    private static final String CHEST_INTERACT_PERMISSION = "vanish.chestinteract";
    private Set<UUID> vanishedSet;
    private Map<UUID, Location> chestViewMap;

    public VanishManager() {
        vanishedSet = new HashSet<>();
        chestViewMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, ModMode.getPlugin());
    }

    public Set<UUID> getVanishedPlayers() {
        return Collections.unmodifiableSet(vanishedSet);
    }

    public boolean isVanished(UUID uuid) {
        return vanishedSet.contains(uuid);
    }

    /**
     * Vanish's a player hiding them from all other non staff members.
     * @param player Player to be hidden.
     * @param shouldUpdateState Update the players state for Players and collision with entities.
     */
    public void vanish(Player player, boolean shouldUpdateState) {
        vanishedSet.add(player.getUniqueId());
        player.setMetadata("Vanish", new FixedMetadataValue(ModMode.getPlugin(), true));
        if (shouldUpdateState) {
            updateState(player, true);
        }
    }

    /**
     * Unvanish's a player showing them to all players.
     * @param player Player to be shown.
     * @param shouldUpdateState Update the players state for Players and collision with entities.
     */
    public void unvanish(Player player, boolean shouldUpdateState) {
        vanishedSet.remove(player.getUniqueId());
        player.removeMetadata("Vanish", ModMode.getPlugin());
        if (shouldUpdateState) {
            updateState(player, false);
        }
    }

    /**
     * Handles chest actions for vanished staff members.
     * @param player The player to handle the chest for
     * @param chest The {@link Chest} to handle.
     * @param open Are we opening or closing the chest (for sounds).
     */
    private void handleChestViewActions(Player player, Chest chest, boolean open) {
        Inventory chestInventory = chest.getInventory();
        if (chestInventory instanceof DoubleChestInventory) {
            chest = (Chest) ((DoubleChestInventory) chestInventory).getHolder().getLeftSide();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(chest.getX(), chest.getY(), chest.getZ(), Blocks.CHEST, 1, open ? 1 : 0));
        player.playSound(chest.getLocation(), open ? Sound.CHEST_OPEN : Sound.CHEST_CLOSE, 1.0f, 1.0f);
        player.sendMessage(ChatColor.RED + "Opening chest silently...");
    }

    /**
     * Updates the state of the player (Entity collision and visibility)
     * @param player The {@link Player}
     * @param vanished What vanish state will the player be.
     */
    private void updateState(Player player, boolean vanished) {
        // First, ensure player does not collide with entities if they are vanished
        player.spigot().setCollidesWithEntities(!vanished);

        // Finally, loop through all players, hiding player from other players if vanished
        Bukkit.getOnlinePlayers().stream().filter(target -> !player.equals(target)).forEach(target -> {
            if (vanished) {
                if (target.hasPermission("command.vanish")) {
                    target.showPlayer(player);
                } else {
                    target.hidePlayer(player);
                }
            } else {
                target.showPlayer(player);
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isVanished(player.getUniqueId())) {
            player.sendMessage(ChatColor.AQUA + "You have joined vanished.");
            updateState(player, true);
        } else {
            Bukkit.getOnlinePlayers().stream().filter(target -> isVanished(target.getUniqueId())).forEach(player::hidePlayer);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity target = event.getRightClicked();
        if (target instanceof Player) {
            Player player = event.getPlayer();
            if (!player.isSneaking() && player.hasPermission(INVENTORY_INTERACT_PERMISSION) && isVanished(player.getUniqueId()) && player.getItemInHand().getType().equals(Material.AIR)) {
                player.openInventory(((Player) target).getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getReason() != EntityTargetEvent.TargetReason.CUSTOM) {
            Entity entity = event.getEntity();
            Entity target = event.getTarget();
            if ((entity instanceof ExperienceOrb || entity instanceof LivingEntity) && target instanceof Player && isVanished(target.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (isVanished(event.getEntity().getUniqueId())) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.SUICIDE) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Player attacker = Utils.getFinalAttacker(event, true);
            if (isVanished(player.getUniqueId())) {
                if (attacker != null) {
                    attacker.sendMessage(ChatColor.RED + "That player is vanished.");
                }
                event.setCancelled(true);
                return;
            }

            if (attacker != null && isVanished(attacker.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst vanished!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isVanished(player.getUniqueId()) && !player.hasPermission(BLOCK_INTERACT_PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isVanished(player.getUniqueId()) && !player.hasPermission(BLOCK_INTERACT_PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (isVanished(player.getUniqueId()) && !player.hasPermission(BLOCK_INTERACT_PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isVanished(player.getUniqueId())) {
            if (event.getAction().equals(Action.PHYSICAL)) {
                event.setCancelled(true);
            } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                BlockState state = event.getClickedBlock().getState();
                if (state instanceof Chest && !player.hasPermission("vanish.bypasschest")) {
                    Chest chest = (Chest) state;
                    if (chest.getInventory().getType() == InventoryType.CHEST && chestViewMap.putIfAbsent(player.getUniqueId(), chest.getLocation()) == null) {
                        ItemStack[] contents = chest.getInventory().getContents();
                        Inventory chestViewInventory = Bukkit.createInventory(null, contents.length, ChatColor.RED + "[Silent] " + ChatColor.RESET + chest.getInventory().getType().getDefaultTitle());
                        chestViewInventory.setContents(contents);
                        player.openInventory(chestViewInventory);
                        handleChestViewActions(player, chest, true);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (chestViewMap.containsKey(player.getUniqueId())) {
            Location chestLocation = chestViewMap.remove(player.getUniqueId());
            if (chestLocation != null) {
                BlockState blockState = chestLocation.getBlock().getState();
                if (blockState instanceof Chest) {
                    handleChestViewActions(player, (Chest) blockState, false);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player) humanEntity;
            if (chestViewMap.containsKey(player.getUniqueId())) {
                ItemStack stack = event.getCurrentItem();
                if (stack != null && stack.getType() != Material.AIR && !player.hasPermission(CHEST_INTERACT_PERMISSION)) {
                    player.sendMessage(ChatColor.RED + "You cannot interact with chests while vanished.");
                    event.setCancelled(true);
                }
            }
        }
    }
}

