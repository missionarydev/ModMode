package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import me.missionary.modmode.utils.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Missionary (missionarymc@gmail.com) on 10/11/2017.
 */
public class StaffOnlineItem extends StaffModeItem {

    public StaffOnlineItem() {
        super(6, "command.mod", new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatColor.YELLOW + "Online Staff " + ChatColor.GRAY + "(Right Click)").toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Set<Player> staff = Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("command.mod")).collect(Collectors.toSet());
        Menu menu = new Menu(ChatColor.YELLOW + "Online Staff", getSafestInventorySize(staff.size() / 9, 54));
        menu.runWhenEmpty(false);
        staff.forEach(player -> menu.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(player.getDisplayName()).toItemStack()));
        menu.showMenu(event.getPlayer());
    }

    public int getSafestInventorySize(final int size, int maxSize) {
        final int val;
        if (size <= 0) {
            val = 9;
        } else {
            val = size % 9 == 0 ? size : (size + 8) / 9 * 9;
        }
        return Math.min(val, maxSize);
    }
}
