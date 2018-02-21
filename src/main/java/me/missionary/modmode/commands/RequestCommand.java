package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.Cooldowns;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/17/2017.
 */
@RequiredArgsConstructor
public class RequestCommand {

    private final ModMode plugin;

    @Command(name = "request", aliases = {"helpop", "reqstaff"}, inGameOnly = true)
    public void onCommand(CommandArgs args){
        Player sender = args.getPlayer();

        if (!plugin.getActionManager().getIsRequests().get()){
            sender.sendMessage(ChatColor.RED + "Requests are currently disabled.");
            return;
        }

        if (args.length() == 0){
            sender.sendMessage(ChatColor.RED + args.getCommand().getUsage());
            sender.sendMessage(ChatColor.RED + "For parameter 'reason': No reason was found.");
            return;
        }

        if (Cooldowns.isOnCooldown("request", sender)){
            sender.sendMessage(ChatColor.RED + "You are on cooldown for " + Cooldowns.getCooldownForPlayerInt("request", sender) + " seconds.");
            return;
        }

        final String reason = String.join(" ", args.getArgs());
        plugin.getActionManager().createAndSendRequest(sender.getName(), reason);
        sender.sendMessage(ChatColor.GREEN + "You have sent your request for " + reason + '.');
        Cooldowns.addCooldown("request", sender, 60);
    }
}