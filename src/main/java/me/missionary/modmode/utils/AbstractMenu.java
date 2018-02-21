package me.missionary.modmode.utils;

import me.missionary.modmode.ModMode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public abstract class AbstractMenu implements InventoryHolder {

    protected final ModMode plugin;

    protected final Inventory inventory;

    public AbstractMenu(ModMode plugin, int size, String title) {
        this.plugin = plugin;
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        this.inventory = plugin.getServer().createInventory(this, size, title);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public abstract void onInventoryClick(InventoryClickEvent event);

    public abstract void onInventoryClose(InventoryCloseEvent event);
}
