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
public class VanishOffItem extends StaffModeItem {

    public VanishOffItem() {
        // Construct the vanish off item.
        super(8, "command.mod", new ItemBuilder(Material.INK_SACK).setName(ChatColor.RED + ChatColor.BOLD.toString() + "Not Vanished").addUnsafeEnchantment(Enchantment.DURABILITY, 1).setDyeColor(DyeColor.RED).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // Set the player vanished.
        plugin.getVanishManager().vanish(event.getPlayer(), true);
        event.getPlayer().sendMessage(ChatColor.GREEN + "You are now vanished.");
        // Give the actor the vanish on item.
        cycleItems(event.getPlayer());
        event.setCancelled(true);
    }

    private void cycleItems(Player player) {
        player.getInventory().setItem(getSlot(), plugin.getModModeManager().getStaffModeItem(VanishOnItem.class).getItem());
    }
}
