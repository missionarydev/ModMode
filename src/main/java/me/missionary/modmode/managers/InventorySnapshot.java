package me.missionary.modmode.managers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
@Data
@AllArgsConstructor
public final class InventorySnapshot {

    private final ItemStack[] inventoryContents;
    private final ItemStack[] armourContents;

    /**
     * A 'snapshot' of an {@link PlayerInventory}.
     * @param playerInventory The inventory of a {@link org.bukkit.entity.Player}.
     */
    public InventorySnapshot(PlayerInventory playerInventory) {
        this(playerInventory.getContents(), playerInventory.getArmorContents());
    }
}
