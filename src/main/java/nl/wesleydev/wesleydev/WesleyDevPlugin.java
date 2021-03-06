package nl.wesleydev.wesleydev;

import net.milkbowl.vault.economy.Economy;
import nl.wesleydev.wesleydev.commands.CommandWesleyDev;
import nl.wesleydev.wesleydev.listeners.MonsterKillRewardListener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class WesleyDevPlugin extends JavaPlugin {

    public static final String Name = "WesleyDev";

    private static Economy economy = null;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        InitializeCommands();
        InitializeListeners();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void InitializeCommands() {
        String wesleyDevCmd = "wesleydev";

        CommandWesleyDev commandWesleyDev = new CommandWesleyDev(economy);
        getCommand(wesleyDevCmd).setTabCompleter(commandWesleyDev);
        getCommand(wesleyDevCmd).setExecutor(commandWesleyDev);
    }

    private void InitializeListeners() {
        if (economy != null) {
            getServer().getPluginManager().registerEvents(new MonsterKillRewardListener(this, economy), this);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider  = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider  == null) {
            return false;
        }
        economy = economyProvider.getProvider();
        return economy != null;
    }
}
