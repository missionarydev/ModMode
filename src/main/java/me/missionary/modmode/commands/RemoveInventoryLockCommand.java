package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.managers.exceptions.InventoryLockException;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/6/2017.
 */
@RequiredArgsConstructor
public class RemoveInventoryLockCommand {

    private final ModMode plugin;

    @Command(name = "removeinvlock", aliases = {"removeinventorylock"}, inGameOnly = true, permission = "command.removeinvlock")
    public void onCommand(CommandArgs args) {
        Player sender = args.getPlayer();
        if (args.length() < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /removeinvlock <playerName>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
        if (target == null) {
            sender.sendMessage(String.format("%s was not found", args.getArgs(0)));
        }

        if (plugin.getScreenshareManager().isHalted(target) && plugin.getScreenshareManager().isInventoryLocked(target) && target != null) {
            try {
                plugin.getScreenshareManager().removeInventoryLock(target);
                sender.sendMessage(ChatColor.GREEN + "Successfully removed the inventory lock of " + target.getDisplayName() + '.');
            } catch (InventoryLockException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Handled exception caught whilst trying to remove the inventory lock of " + target.getName() + ". Exception cause: " + e.getCause() + '.');
            }
        }
    }
}
