package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class VanishOnItem extends StaffModeItem {
    public VanishOnItem() {
        // Construct the vanish on item.
        super(8, "command.mod", new ItemBuilder(Material.INK_SACK).setName(ChatColor.GREEN + ChatColor.BOLD.toString() +  "Vanished").addUnsafeEnchantment(Enchantment.DURABILITY, 1).setDyeColor(DyeColor.LIME).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // Unvanish the player.
        plugin.getVanishManager().unvanish(event.getPlayer(), true);
        event.getPlayer().sendMessage(ChatColor.RED + "You are now visible.");
        // Give the actor the vanish off item.
        cycleItems(event.getPlayer());
        event.setCancelled(true);
    }

    private void cycleItems(Player player) {
        player.getInventory().setItem(getSlot(), plugin.getModModeManager().getStaffModeItem(VanishOffItem.class).getItem());
    }
}
