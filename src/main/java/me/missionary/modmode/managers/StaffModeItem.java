package me.missionary.modmode.managers;

import me.missionary.modmode.ModMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */

public abstract class StaffModeItem implements Listener {

    protected static final ModMode plugin = ModMode.getPlugin();

    private final ItemStack itemStack;
    private final int slot;
    private final String permission;

    public StaffModeItem(int slot, String permission, ItemStack itemStack) {
        this.slot = slot;
        this.permission = permission;
        this.itemStack = itemStack.clone();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Gives an {@link Player} the staff mode item.
     * @param player Inputted player
     */
    public void applyTo(Player player) {
        if (player.hasPermission(permission)) player.getInventory().setItem(slot, itemStack);
    }

    /**
     * Removes the {@link StaffModeItem} from a {@link Player}
     *
     * @param player Inputted player
     */
    public void removeFrom(Player player) {
        player.getInventory().setItem(slot, null);
    }

    /**
     * Get's the {@link ItemStack} of the {@link StaffModeItem}
     *
     * @return itemStack
     */
    public final ItemStack getItem() {
        return itemStack;
    }

    /**
     * Checks to see if a {@link Player} is holding the item.
     *
     * @param player Inputted player
     * @return true if the player is holding the item, false if the player is not.
     */
    public final boolean isHolding(Player player) {
        return player != null && player.getItemInHand() != null && player.getItemInHand().equals(itemStack);
    }

    /**
     * Checks to see if an {@link ItemStack} is equal to the {@link StaffModeItem} itemstack.
     *
     * @param itemStack Inputted ItemStack
     * @return true if it is equal, false if it is not equal.
     */
    public final boolean isEqual(ItemStack itemStack) {
        return itemStack != null && this.itemStack.equals(itemStack);
    }

    /**
     * Get the slot of a item.
     *
     * @return slot
     */
    public int getSlot() {
        return slot;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR) && event.getItem().equals(itemStack)) {
            Player player = event.getPlayer();
            if (plugin.getModModeManager().isInStaffMode(event.getPlayer())) {
                onInteract(event);
            } else {
                player.sendMessage(ChatColor.RED + "You must be in staff mode to use that.");
                player.getInventory().remove(getItem());
                event.setCancelled(true);
            }
        }
    }

    // Handle the interact event in the subclasses.
    public abstract void onInteract(PlayerInteractEvent event);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (itemStack.equals(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (itemStack.equals(event.getEntity().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(this.itemStack::equals);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            ItemStack current = event.getCurrentItem();
            final boolean isInStaffMode = plugin.getModModeManager().isInStaffMode(player);

            if (current != null && itemStack.equals(current)) {
                if (isInStaffMode) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                } else {
                    event.setResult(Event.Result.DENY);
                    event.setCurrentItem(null);
                }
            }

            ItemStack cursor = event.getCursor();

            if (cursor != null && itemStack.equals(cursor)) {
                if (isInStaffMode) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                } else {
                    event.setResult(Event.Result.DENY);
                    event.getView().setCursor(null);
                }
            }
        }
    }
}
