package me.missionary.modmode.commands;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * Created by Missionary on 7/13/2017.
 */
@RequiredArgsConstructor
public class DespawnCommand {

    private final ModMode plugin;

    @Command(name = "despawn", permission = "command.despawn", description = "Command used to despawn an entity", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player sender = args.getPlayer();
        if (!plugin.getModModeManager().getDespawnMap().containsKey(sender.getUniqueId())) {
            final String text = "You have not selected an entity to despawn! "
                    + "In Mod Mode right click the entity with the book to select it.";
            sender.spigot().sendMessage(new ComponentBuilder(text).color(RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click here to enter mod mode!").color(YELLOW).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getModModeManager().isInStaffMode(args.getPlayer()) ? "" : "/h"))
                    .create());
        } else {
            Entity entity = plugin.getModModeManager().getDespawnMap().get(sender.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Successfully despawned the " + entity.getType().name() + '.');
            entity.remove();
            plugin.getModModeManager().getDespawnMap().remove(sender.getUniqueId());
        }
    }
}
