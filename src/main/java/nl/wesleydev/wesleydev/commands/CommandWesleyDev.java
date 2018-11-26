package nl.wesleydev.wesleydev.commands;

import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import nl.wesleydev.wesleydev.commands.enums.WesleyDevBuyItem;
import nl.wesleydev.wesleydev.commands.enums.WesleyDevCommandType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class CommandWesleyDev implements TabExecutor {

    //TODO: When returning false log back to the command sender why

    private Plugin plugin;
    private Economy economy;

    public CommandWesleyDev(Plugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (WesleyDevCommandType.fromString(args[0])) {
                case BUY:
                    return HandleBuyCommand(sender, command, label, args);
            }
        } else {
            sender.sendMessage("WesleyDev: Thank you for using WesleyDev!");
            return true;
        }

        return false;
    }

    private boolean HandleBuyCommand(CommandSender sender, Command command, String label, String[] args) {
        int itemCost = 1000;

        if(args.length >= 2 && sender instanceof Player) {
            Player player = (Player) sender;

            switch (WesleyDevBuyItem.fromString(args[1])) {
                case DIAMOND:
                    //Buy diamond with the given amount of diamonds in args[2]
                    try {
                        int amountToBuy = args.length == 3 ? Integer.parseInt(args[2]) : 1;
                        int totalCost = itemCost * amountToBuy;
                        //Making sure there is no integer overflow, and the player is not buying 0 diamonds
                        if (totalCost < 1) return false;
                        double playerBalance = economy.getBalance(player);
                        String diamondMessage = amountToBuy == 1 ? "diamond" : "diamonds";

                        if (playerBalance >= totalCost && economy.withdrawPlayer(player, totalCost).transactionSuccess()) {
                            ItemStack diamonds = new ItemStack(Material.DIAMOND, amountToBuy);
                            player.getInventory().addItem(diamonds);

                            player.sendMessage(String.format("You bought %s %s, costing you %s. You now have %s",
                                    amountToBuy,
                                    diamondMessage,
                                    economy.format(totalCost),
                                    economy.format(playerBalance - totalCost)));
                        } else {
                            player.sendMessage(String.format("You tried to buy %s %s, costing you %s, " +
                                            "but you only have %s",
                                    amountToBuy,
                                    diamondMessage,
                                    economy.format(totalCost),
                                    economy.format(playerBalance)));
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }


    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("buy");
        } else if (args.length == 2) {
            switch (WesleyDevCommandType.fromString(args[0])) {
                case BUY:
                    return Lists.newArrayList("diamond");
                default:
                    return Collections.emptyList();
            }
        } else if (args.length == 3) {
            return Lists.newArrayList("1", "10", "100", "1000");
        } else {
            return Collections.emptyList();
        }
    }
}
