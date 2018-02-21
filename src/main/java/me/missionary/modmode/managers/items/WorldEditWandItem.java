package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Missionary on 7/9/2017.
 */
public class WorldEditWandItem extends StaffModeItem {

    public WorldEditWandItem(){
        // Construct the worldedit wand item.
        super(3, "worldedit.wand", new ItemBuilder(Material.WOOD_AXE).setName(ChatColor.YELLOW + "WorldEdit Wand").addUnsafeEnchantment(Enchantment.DURABILITY, 1).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true); // Let worldedit handle the event.
    }
}

