package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.managers.events.PlayerFreezeEvent;
import me.missionary.modmode.utils.Utils;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
@RequiredArgsConstructor
public class HaltCommand {

    private final ModMode plugin;

    @me.missionary.modmode.utils.commands.Command(name = "halt", aliases = {"ss", "freeze"}, permission = "command.halt", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player sender = args.getPlayer();
        if (args.length() < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /halt <playerName>");
            return;
        }

        if (args.getArgs(0).equalsIgnoreCase("list")) {
            Set<String> frozenMessage = new HashSet<>();
            plugin.getScreenshareManager().getAllHaltedPlayers().stream().map(Bukkit::getOfflinePlayer).forEach(frozenPlayer -> {
                ChatColor color = frozenPlayer.isOnline() ? ChatColor.GREEN : ChatColor.RED;
                frozenMessage.add(color + frozenPlayer.getName());
            });
            String players = StringUtils.join(frozenMessage, ChatColor.WHITE + ", ");
            sender.sendMessage(ChatColor.DARK_RED + ChatColor.STRIKETHROUGH.toString() + Strings.repeat('-', 55));
            sender.sendMessage(ChatColor.RED + "All Frozen Players:");
            sender.sendMessage(players);
            sender.sendMessage(ChatColor.DARK_RED + ChatColor.STRIKETHROUGH.toString() + Strings.repeat('-', 55));
            return;
        }

        Player target = Bukkit.getServer().getPlayer(args.getArgs(0));
        if (target == null) {
            sender.sendMessage(ChatColor.RED.toString() + args.getArgs(0) + " doesnt exist.");
            return;
        }
        if (!Utils.canSee(sender, target)) {
            if (plugin.getScreenshareManager().isHalted(target)) {
                plugin.getScreenshareManager().unhaltPlayer(target);
                Command.broadcastCommandMessage(sender, ChatColor.GREEN + target.getName() + " is no longer frozen");
                return;
            } else {
                sender.sendMessage(String.format(ChatColor.RED + "%s was not found.", args.getArgs(0)));
                return;
            }
        }

        if (target.equals(sender) && target.hasPermission("command.halt.bypass")) {
            sender.sendMessage(ChatColor.RED + "You cannot freeze yourself.");
            return;
        }

        boolean shouldFreeze = plugin.getScreenshareManager().isHalted(target);
        PlayerFreezeEvent playerFreezeEvent = new PlayerFreezeEvent(target, shouldFreeze);
        Bukkit.getServer().getPluginManager().callEvent(playerFreezeEvent);
        if (playerFreezeEvent.isCancelled()) {
            sender.sendMessage(ChatColor.RED + "Unable to freeze " + target.getName() + '.');
            return;
        }
        if (shouldFreeze) {
            plugin.getScreenshareManager().unhaltPlayer(target);
            Command.broadcastCommandMessage(sender, ChatColor.GREEN + target.getName() + " is no longer halted.");
        } else {
            plugin.getScreenshareManager().haltPlayer(target);
            Command.broadcastCommandMessage(sender, ChatColor.RED + target.getName() + " is now halted.");
            target.playSound(target.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
        }
    }
}
