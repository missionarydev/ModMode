package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.Cooldowns;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/17/2017.
 */
@RequiredArgsConstructor
public class ReportCommand {

    private final ModMode plugin;

    @Command(name = "report", aliases = {"reportplayer"}, inGameOnly = true)
    public void onCommand(CommandArgs args){
        Player sender = args.getPlayer();

        if (!plugin.getActionManager().getIsReports().get()){
            sender.sendMessage(ChatColor.RED + "Reports are currently disabled.");
            return;
        }

        if (args.length() == 0){
            sender.sendMessage(ChatColor.RED + args.getCommand().getUsage());
            sender.sendMessage(ChatColor.RED + "For parameter 'playerName': No player was found.");
            return;
        } else if (args.length() == 1){
            sender.sendMessage(ChatColor.RED + args.getCommand().getUsage());
            sender.sendMessage(ChatColor.RED + "For parameter 'reason': No reason was found.");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArgs(0));
        if (target == null){
            sender.sendMessage(ChatColor.RED + args.getCommand().getUsage());
            sender.sendMessage(ChatColor.RED + "For parameter 'playerName': The player was invalid.");
            return;
        }

        if (target == sender){
            sender.sendMessage(ChatColor.RED + "You may not report yourself.");
            return;
        }

        if (Cooldowns.isOnCooldown("report", sender)){
            sender.sendMessage(ChatColor.RED + "You are on cooldown for " + Cooldowns.getCooldownForPlayerInt("report", sender) + " seconds.");
            return;
        }

        final String reason = String.join(" ", Arrays.copyOfRange(args.getArgs(), 1, args.length()));
        plugin.getActionManager().createAndSendReport(sender.getName(), reason, target.getName());
        sender.sendMessage(ChatColor.GREEN + "You have sent your report on " + target.getName() + " for " + reason + '.');
        Cooldowns.addCooldown("report", sender, 60);
    }
}
