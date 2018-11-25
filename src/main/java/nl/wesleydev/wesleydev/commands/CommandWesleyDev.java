package nl.wesleydev.wesleydev.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWesleyDev implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("WesleyDev: Thank you for using WesleyDev!");
        return true;
    }

}
