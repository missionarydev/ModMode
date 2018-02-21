package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import me.missionary.modmode.managers.ModModeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Missionary (missionarymc@gmail.com) on 3/27/2017.
 */
@RequiredArgsConstructor
public class ModModeCommand {

    private final ModMode plugin;

    @Command(name = "h", aliases = {"mod", "mm", "sm", "moderatormode", "hackermode"}, inGameOnly = true, permission = "command.mod", description = "Command used to Moderation Mode")
    public void onCommand(CommandArgs args){
        Player sender = args.getPlayer();
        ModModeManager staffModeManager = plugin.getModModeManager();
        if (args.length() == 0) {
            if (staffModeManager.isInStaffMode(sender)) {
                staffModeManager.disableStaffMode(sender);
                sender.sendMessage(ChatColor.GOLD + "Hacker Mode: " + ChatColor.RED + "Disabled");
            } else {
                staffModeManager.enableStaffMode(sender);
                sender.sendMessage(ChatColor.GOLD + "Hacker Mode: " + ChatColor.GREEN + "Enabled");
            }
        } else if (args.length() == 1){
            Player target = Bukkit.getPlayer(args.getArgs(0));
            if (target == null){
                sender.sendMessage(String.format("%s was not found.", args.getArgs(0)));
                return;
            }
            if (staffModeManager.isInStaffMode(target)){
                staffModeManager.disableStaffMode(target);
                target.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
                sender.sendMessage(ChatColor.GREEN + "Disabled mod mode for " + target.getName());
            } else {
                staffModeManager.enableStaffMode(target);
                target.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.GREEN + "Enabled");
                sender.sendMessage(ChatColor.GREEN + "Enabled mod mode for " + target.getName());
            }
        }
    }
}
