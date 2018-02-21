package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import me.missionary.modmode.utils.Menu;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.md_5.bungee.api.ChatColor.*;

import java.util.Collections;


/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class InventorySeeItem extends StaffModeItem {

    public InventorySeeItem() {
        // Constructs the staff mode item.
        super(1, "command.mod", new ItemBuilder(Material.ENCHANTED_BOOK).setName(ChatColor.YELLOW + "Inspect Inventory" + ChatColor.GRAY + " (Right-Click)").toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // We will let the listener function below handle this items interactions.
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        // Get a reference to the actor.
        Player player = event.getPlayer();

        // If the actor is not in staff mode, return.
        if (!plugin.getModModeManager().isInStaffMode(player)) {
            return;
        }

        // Check to see if the right clicked entity is not instanceof a Player. If not request despawn or return.
        if (!(event.getRightClicked() instanceof Player)){
            Entity entity = event.getRightClicked();
            event.setCancelled(true);
            plugin.getModModeManager().getDespawnMap().put(event.getPlayer().getUniqueId(), entity);
            event.getPlayer().spigot().sendMessage(new ComponentBuilder("Click to despawn this ").color(BLUE).reset()
                    .append(entity.getType().name()).color(YELLOW).reset()
                    .append(".").color(BLUE)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/despawn"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click to despawn.").color(YELLOW).create())).create());
            return;
        }

        // Get a reference to the player that was right clicked.
        Player clickedPlayer = (Player) event.getRightClicked();

        // If the player is not holding this item, return.
        if (!isHolding(player)) {
            return;
        }

        // If the actor is not sneaking open the clickedPlayer's inventory.
        if (!player.isSneaking()) {
            player.openInventory(clickedPlayer.getInventory());
            event.setCancelled(true);
        } else {
            // Else open a new Menu containing the clickedPlayer's armor contents, active potion effects, food level, and health level. Also allows for you to clear the targets inventory.
            Menu inventory = new Menu(ChatColor.GOLD.toString() + "Inspecting: " + clickedPlayer.getName(), 2);
            ItemStack stack = new ItemBuilder(Material.POTION).setName(ChatColor.YELLOW + "Active Potion Effects").toItemStack();
            clickedPlayer.getActivePotionEffects().forEach(potionEffect -> addLore(stack, ChatColor.YELLOW + potionEffect.getType().getName() + " " +ChatColor.GREEN + " Amplifier: " +  ChatColor.WHITE +  (potionEffect.getAmplifier() + 1) + ChatColor.GREEN +  " Duration: " + ChatColor.WHITE +  potionEffect.getDuration() / 20));
            inventory.setItem(0, clickedPlayer.getInventory().getHelmet());
            inventory.setItem(1, clickedPlayer.getInventory().getChestplate());
            inventory.setItem(2, clickedPlayer.getInventory().getLeggings());
            inventory.setItem(3, clickedPlayer.getInventory().getBoots());
            inventory.setItem(13, stack);
            inventory.setItem(14, new ItemBuilder(Material.POTION).setName(ChatColor.GREEN.toString() + clickedPlayer.getHealth() + '/' + clickedPlayer.getMaxHealth() + ChatColor.YELLOW + " Health Status").setDurability((short) 16421).toItemStack());
            inventory.setItem(15, new ItemBuilder(Material.COOKED_BEEF).setName(ChatColor.GREEN.toString() + clickedPlayer.getFoodLevel() + ChatColor.YELLOW + " Food Level").toItemStack());
            inventory.setItem(16, new ItemBuilder(Material.PACKED_ICE).setName(ChatColor.AQUA + "Click to halt this player.").toItemStack());
            inventory.setItem(17, new ItemBuilder(Material.WOOL).setDyeColor(DyeColor.RED).setName(ChatColor.RED.toString() + "Clear Inventory").toItemStack());
            inventory.runWhenEmpty(false);
            inventory.setGlobalAction((player1, inv, item, slot, action) -> {
                switch (slot) {
                    // If the slot is the 16th slot, do some logic then halt the player.
                    case 16: {
                        if (plugin.getModModeManager().isInStaffMode(clickedPlayer)) {
                            player1.sendMessage(ChatColor.RED + clickedPlayer.getName() + " is a staff member and in Mod Mode, therefore you cannot halt them.");
                            return;
                        }
                        player1.performCommand("halt " + clickedPlayer.getName());
                        break;
                    }
                    // If the slot is in the 17th slot, do some logic then clear the targets inventory.
                    case 17: {
                        if (plugin.getModModeManager().isInStaffMode(clickedPlayer)) {
                            return;
                        }
                        clickedPlayer.getInventory().clear();
                        clickedPlayer.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                        player1.sendMessage(ChatColor.RED + "Cleared the inventory of " + clickedPlayer.getName() + '.');
                        clickedPlayer.sendMessage(ChatColor.GOLD + "Your inventory has been cleared by " + player1.getName() + '.');
                        player1.closeInventory();
                        break;
                    }
                }
            });
            event.setCancelled(true);
            inventory.showMenu(player);
        }
    }

    // Helper method.
    private ItemStack addLore(ItemStack itemStack, String lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getLore() != null) {
            meta.getLore().add(lore);
        } else {
            meta.setLore(Collections.singletonList(lore));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}

