package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class PhaseTool extends StaffModeItem {
    public PhaseTool() {
        // Constructs the phase tool staff mode item.
        super(0, "command.mod", new ItemBuilder(Material.COMPASS).setName(ChatColor.YELLOW + "Compass " + ChatColor.GRAY + " (Right-Click)").addUnsafeEnchantment(Enchantment.DURABILITY, 1).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // If the event is not cancelled.
        if(!event.isCancelled()) {
            // Run /thru.
            Bukkit.dispatchCommand(event.getPlayer(), "thru");
            // Set the event cancelled.
            event.setCancelled(true);
        }
    }
}
