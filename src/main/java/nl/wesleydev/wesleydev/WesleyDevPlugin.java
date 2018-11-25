package nl.wesleydev.wesleydev;

import nl.wesleydev.wesleydev.commands.CommandWesleyDev;
import org.bukkit.plugin.java.JavaPlugin;

public class WesleyDevPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("wesleydev").setExecutor(new CommandWesleyDev());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
