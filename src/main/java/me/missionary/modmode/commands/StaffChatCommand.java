package me.missionary.modmode.commands;

import com.google.common.collect.Sets;
import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/17/2017.
 */
@RequiredArgsConstructor
public class StaffChatCommand implements Listener {

    public static final Set<UUID> staffChatMap = Sets.newSetFromMap(new ConcurrentHashMap<>());
    private final ModMode plugin;

    @Command(name = "staffchat", aliases = {"sc", "lsc", "gsc", "ac"}, permission = "command.staffchat", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player sender = args.getPlayer();
        if (args.length() == 0) {
            if (staffChatMap.contains(sender.getUniqueId())) {
                staffChatMap.remove(sender.getUniqueId());
                sender.sendMessage(ChatColor.RED + "You have toggled out of staff chat mode.");
            } else {
                staffChatMap.add(sender.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "You are now in staff chat mode.");
            }
        } else {
            final String message = String.join(" ", args.getArgs());
            plugin.getActionManager().createAndSendStaffChat(sender.getName(), message);
        }
    }
}
