package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class HaltPlayerItem extends StaffModeItem {

    public HaltPlayerItem() {
        // Constructs the staff mode item.
        super(2, "command.mod", new ItemBuilder(Material.IRON_FENCE).setName(ChatColor.YELLOW + "Halt " + ChatColor.GRAY + "(Right-Click)").addUnsafeEnchantment(Enchantment.DURABILITY, 1).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // We will let the listener function below handle this items interaction.
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // If the actor is not a player, return.
        if(!(event.getRightClicked() instanceof Player)) {
            return;
        }
        // Get a reference to the player that was clicked.
        Player clickedPlayer = (Player) event.getRightClicked();

        // Get a reference to the event's actor.
        Player player = event.getPlayer();

        // If the player is not holding this item, return.
        if(!isHolding(player)) {
            return;
        }

        // If the player is not in staff mode, return.
        if(!plugin.getModModeManager().isInStaffMode(player)) {
            return;
        }

        // Dispatch the command to halt the clickedPlayer.
        Bukkit.dispatchCommand(player, "halt " + clickedPlayer.getName());
    }
}
