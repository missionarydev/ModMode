package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.managers.events.PlayerVanishEvent;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
@RequiredArgsConstructor
public class VanishCommand {

    private static final String PERMISSION_VANISH_OTHERS = "command.vanish.others";

    private final ModMode plugin;

    @Command(name = "vanish", aliases = {"v", "vis", "ghostmode", "invis"}, permission = "command.vanish", inGameOnly = true, description = "Command that allows players to toggle into a vanished state.")
    public void onCommand(CommandArgs args) {
        Player sender = args.getPlayer();
        Player target;

        if (args.length() > 0 && sender.hasPermission(PERMISSION_VANISH_OTHERS)) {
            target = Bukkit.getPlayer(args.getArgs(0));
        } else {
            target = sender;
        }

        if (target == null || !sender.canSee(target)) {
            sender.sendMessage(String.format("%s was not found", args.getArgs(0)));
            return;
        }
        boolean currentlyVanished = plugin.getVanishManager().isVanished(target.getUniqueId());

        PlayerVanishEvent playerVanishEvent = new PlayerVanishEvent(target, currentlyVanished, !currentlyVanished);
        Bukkit.getPluginManager().callEvent(playerVanishEvent);

        if (playerVanishEvent.isCancelled()) {
            if (playerVanishEvent.getNewValue()) {
                sender.sendMessage(ChatColor.RED + "Unable to vanish " + target.getName() + '.');
            } else {
                sender.sendMessage(ChatColor.RED + "Unable to un-vanish " + target.getName() + '.');
            }
            return;
        }

        // Actually (un)vanish the player
        if (playerVanishEvent.getNewValue()) {
            plugin.getVanishManager().vanish(target, true);
        } else {
            plugin.getVanishManager().unvanish(target, true);
        }

        // Send messages
        if (sender == target) {
            sender.sendMessage(ChatColor.YELLOW + "You are now " + (playerVanishEvent.getNewValue() ? ChatColor.GREEN + "vanished" : ChatColor.RED + "visible") + ChatColor.YELLOW + '.');
        } else {
            target.sendMessage(sender.getName() + ChatColor.YELLOW + " has set your vanish state to " + playerVanishEvent.getNewValue() + '.');
            sender.sendMessage(ChatColor.YELLOW + "Vanish mode of " + ChatColor.RED + target.getName() + ChatColor.YELLOW + " set to " + (playerVanishEvent.getNewValue() ? ChatColor.GREEN + "true" : ChatColor.RED + "false") + ChatColor.YELLOW + '.');
        }
    }
}