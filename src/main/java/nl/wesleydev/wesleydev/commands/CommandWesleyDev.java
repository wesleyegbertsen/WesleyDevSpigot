package nl.wesleydev.wesleydev.commands;

import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import nl.wesleydev.wesleydev.WesleyDevPlugin;
import nl.wesleydev.wesleydev.commands.enums.WesleyDevCommandType;
import nl.wesleydev.wesleydev.commands.immutables.BuyableMaterial;
import nl.wesleydev.wesleydev.enums.Permission;
import nl.wesleydev.wesleydev.helpers.ListHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandWesleyDev implements TabExecutor {

    //TODO: When returning false log back to the command sender why
    private Economy economy;

    /**
     * Materials that players are allowed to buy
     */
    private static final BuyableMaterial[] BUYABLE_MATERIALS = new BuyableMaterial[] {
        new BuyableMaterial(Material.DIAMOND, 1000),
        new BuyableMaterial(Material.GOLD_INGOT, 750),
        new BuyableMaterial(Material.IRON_INGOT, 500),
        new BuyableMaterial(Material.COAL, 100),
        new BuyableMaterial(Material.REDSTONE, 250),
        new BuyableMaterial(Material.LAPIS_LAZULI, 250),
        new BuyableMaterial(Material.EMERALD, 750)
    };

    public CommandWesleyDev(Economy economy) {
        this.economy = economy;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (WesleyDevCommandType.fromString(args[0])) {
                case BUY:
                    if (!hasBuyPermission(sender)) return true;
                    return handleBuyCommand(sender, args);
                case PRICE:
                    if (!hasPricePermission(sender)) return true;
                    return handlePriceCommand(sender, args);
            }
        } else {
            sender.sendMessage(String.format("[%s] Thank you for using this plugin!", WesleyDevPlugin.Name));
            return true;
        }

        return false;
    }

    private boolean hasBuyPermission(CommandSender sender) {
        if (Permission.hasPermission(sender, Permission.ECONOMY)
                || Permission.hasPermission(sender, Permission.ECONOMY_BUY)) {
            return true;
        } else {
            Permission.sendNoPermissionMessage(sender);
            return false;
        }
    }

    private boolean handleBuyCommand(CommandSender sender, String[] args) {
        if (args.length > 3) return false;
        if (args.length >= 2 && sender instanceof Player) {
            Player player = (Player) sender;
            String material = args[1];
            BuyableMaterial buyableMaterial = BuyableMaterial.getFromArray(BUYABLE_MATERIALS, material);

            if (buyableMaterial != null) {
                //Buy material with the given amount of the material in args[2]
                try {
                    int amountToBuy = args.length == 3 ? Integer.parseInt(args[2]) : 1;
                    double totalCost = buyableMaterial.getPrice() * amountToBuy;
                    //Making sure there is no integer overflow, and the player is not buying 0 materials
                    if (totalCost < 1) return false;
                    double playerBalance = economy.getBalance(player);
                    String materialDescription = amountToBuy == 1 ? material : String.format("%ss", material);

                    if (playerBalance >= totalCost && economy.withdrawPlayer(player, totalCost).transactionSuccess()) {
                        ItemStack materials = new ItemStack(buyableMaterial.getMaterial(), amountToBuy);
                        player.getInventory().addItem(materials);

                        player.sendMessage(String.format("You bought %s %s, costing you %s. You now have %s",
                                amountToBuy,
                                materialDescription,
                                economy.format(totalCost),
                                economy.format(playerBalance - totalCost)));
                    } else {
                        player.sendMessage(String.format("You tried to buy %s %s, costing you %s, " +
                                        "but you only have %s",
                                amountToBuy,
                                materialDescription,
                                economy.format(totalCost),
                                economy.format(playerBalance)));
                    }
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean hasPricePermission(CommandSender sender) {
        if (Permission.hasPermission(sender, Permission.ECONOMY)
                || Permission.hasPermission(sender, Permission.ECONOMY_PRICE)) {
            return true;
        } else {
            Permission.sendNoPermissionMessage(sender);
            return false;
        }
    }

    private boolean handlePriceCommand(CommandSender sender, String[] args) {
        if (args.length > 3) return false;
        if (args.length >= 2) {
            String material = args[1];
            BuyableMaterial buyableMaterial = BuyableMaterial.getFromArray(BUYABLE_MATERIALS, material);

            if (buyableMaterial != null) {
                try {
                    int amountToCalculatePrice = args.length == 3 ? Integer.parseInt(args[2]) : 1;
                    double totalCost = buyableMaterial.getPrice() * amountToCalculatePrice;
                    //Making sure there is no integer overflow, and the sender is not checking price of 0 materials
                    if (totalCost < 1) return false;
                    String materialDescription = amountToCalculatePrice == 1 ? material : String.format("%ss", material);

                    sender.sendMessage(String.format("The price of %s %s is %s",
                            amountToCalculatePrice,
                            materialDescription,
                            economy.format(totalCost)));
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private static final String[] WESLEYDEV_COMMANDS = new String[] {
            WesleyDevCommandType.BUY.getText(),
            WesleyDevCommandType.PRICE.getText()
    };

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return ListHelper.getItemsStartWithValue(Arrays.asList(WESLEYDEV_COMMANDS), args[0]);
        } else if (args.length == 2) {
            switch (WesleyDevCommandType.fromString(args[0])) {
                case BUY:
                case PRICE:
                    return ListHelper.getItemsContainValue(BuyableMaterial.getAsEnumStringList(BUYABLE_MATERIALS), args[1]);
                default:
                    return Collections.emptyList();
            }
        } else if (args.length == 3) {
            return ListHelper.getItemsStartWithValue(Lists.newArrayList("1", "10", "100", "1000"), args[2]);
        } else {
            return Collections.emptyList();
        }
    }
}
