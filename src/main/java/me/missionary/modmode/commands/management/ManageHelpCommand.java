package me.missionary.modmode.commands.management;

import me.missionary.modmode.ModMode;
import me.missionary.modmode.actions.Actions;
import me.missionary.modmode.utils.AbstractMenu;
import me.missionary.modmode.utils.ItemBuilder;
import me.missionary.modmode.utils.commands.Command;
import me.missionary.modmode.utils.commands.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;

/**
 * Created by Missionary (missionarymc@gmail.com) on 10/10/2017.
 */
@RequiredArgsConstructor
public class ManageHelpCommand { // TODO: 10/10/2017 find better command name

    private final ModMode plugin;

    @Command(name = "managehelp", permission = "command.managehelp", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        new ChannelManagementMenu(plugin, player).open(player);
    }


    public class ChannelManagementMenu extends AbstractMenu {

        private final Player player;

        public ChannelManagementMenu(ModMode plugin, Player player) {
            super(plugin, 9, ChatColor.YELLOW + "Manage Reports & Requests");
            this.player = player;
            init();
        }

        private void init() {
            inventory.setItem(3, new ItemBuilder(Material.WOOL)
                    .setName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Toggle Requests")
                    .setWoolColor(plugin.getActionManager().getIsRequests().get() ? DyeColor.LIME : DyeColor.RED)
                    .setLore(Arrays.asList(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------",
                            ChatColor.YELLOW + "Click to toggle allowance of requests",
                            ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------",
                            plugin.getActionManager().getIsRequests().get() ? ChatColor.GREEN + "Requests are currently enabled." : ChatColor.RED + "Requests are currently disabled",
                            ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------")).toItemStack());
            inventory.setItem(5, new ItemBuilder(Material.WOOL)
                    .setName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Toggle Reports")
                    .setWoolColor(plugin.getActionManager().getIsReports().get() ? DyeColor.LIME : DyeColor.RED)
                    .setLore(Arrays.asList(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------",
                            ChatColor.YELLOW + "Click to toggle allowance of reports",
                            ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------",
                            plugin.getActionManager().getIsReports().get() ? ChatColor.GREEN + "Reports are currently enabled." : ChatColor.RED + "Reports are currently disabled",
                            ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------")).toItemStack());
            player.updateInventory();
        }

        @Override
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
                switch (event.getSlot()) {
                    case 3: {
                        plugin.getActionManager().getIsRequests().set(!plugin.getActionManager().getIsRequests().get());
                        init();
                        plugin.getActionManager().sendReportRequestStatusMessage(Actions.DisableHelpActions.REQUESTS, plugin.getActionManager().getIsRequests().get());
                    }
                    case 5: {
                        plugin.getActionManager().getIsRequests().set(!plugin.getActionManager().getIsReports().get());
                        init();
                        plugin.getActionManager().sendReportRequestStatusMessage(Actions.DisableHelpActions.REPORTS, plugin.getActionManager().getIsReports().get());
                    }
                }
            }
        }


        @Override
        public void onInventoryClose(InventoryCloseEvent event) {
            // idk
        }
    }
}
