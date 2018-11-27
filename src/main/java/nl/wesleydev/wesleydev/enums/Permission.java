package nl.wesleydev.wesleydev.enums;

import nl.wesleydev.wesleydev.WesleyDevPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Permission {
    UNKNOWN(null),
    ALL("wesleydev.all"),
    ECONOMY("wesleydev.economy"),
    ECONOMY_BUY("wesleydev.economy.buy"),
    ECONOMY_PRICE("wesleydev.economy.price"),
    ECONOMY_MONSTERKILLREWARD("wesleydev.economy.monsterkillreward");

    private String text;

    Permission(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Permission fromString(String text) {
        for (Permission enumValue : Permission.values()) {
            if (enumValue.text != null && enumValue.text.equalsIgnoreCase(text)) {
                return enumValue;
            }
        }
        return Permission.UNKNOWN;
    }

    /**
     * Check if the command sender has the specific permission or all the permissions
     * @return true if the CommandSender has the specified Permission or Permission.ALL
     */
    public static boolean hasPermission(CommandSender sender, Permission permission) {
        return permission != Permission.UNKNOWN &&
                (sender.hasPermission(permission.getText()) || sender.hasPermission(Permission.ALL.getText()));
    }

    public static void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(String.format("[%s] %sYou do not have permission to do this.", WesleyDevPlugin.Name, ChatColor.RED));
    }
}
