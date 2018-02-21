package me.missionary.modmode.managers.items;

import me.missionary.modmode.managers.StaffModeItem;
import me.missionary.modmode.utils.ItemBuilder;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/22/2017.
 */
public class RandomTeleportItem extends StaffModeItem {

    public RandomTeleportItem() {
        super(7, "command.mod", new ItemBuilder(Material.BLAZE_ROD).setName(ChatColor.YELLOW + "Random Teleport " + ChatColor.GRAY + "(Right-Click, Shift for " + ChatColor.AQUA + "Miners" + ChatColor.GRAY + ")").addUnsafeEnchantment(Enchantment.DURABILITY, 1).toItemStack());
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        // Get a reference to the actor.
        Player player = event.getPlayer();
        // Create a new ArrayList that takes a Player object.
        final List<Player> players;

        // If the player is sneaking, iterate and see if the players Y is equal to or less than 35, if so add.
        if (player.isSneaking()) {
            players = Lists.newArrayList();
            Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> !onlinePlayer.equals(player) && onlinePlayer.getLocation().getBlockY() <= 35).forEach(players::add);
        } else {
            // Else iterate and remove the actor from the list, then collect to a list.
            players = Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> !onlinePlayer.equals(player)).collect(Collectors.toList());
        }

        // If the list is empty, return.
        if (players.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There is no one to teleport to!");
            event.setCancelled(true);
            return;
        }

        // Get a target player from the list, randomized. Micro-optimization by using a ThreadLocalRandom.
        Player target = players.get(ThreadLocalRandom.current().nextInt(players.size()));

        // If the target is vanished, do not teleport.
        if (plugin.getVanishManager().isVanished(target.getUniqueId())) {
            player.sendMessage(target.getDisplayName() + ChatColor.GOLD + " is vanished therefore you may not random tp.");
            event.setCancelled(true);
            return;
        }

        // Handle teleporting the player to the target.
        player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.sendMessage(ChatColor.GOLD + "Teleported to " + target.getDisplayName() + (target.getLocation().getY() <= 35 ? ChatColor.AQUA + " (Miner)" : "") + ChatColor.GOLD + '.');
        event.setCancelled(true);
    }
}
