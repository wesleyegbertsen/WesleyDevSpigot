package nl.wesleydev.wesleydev;

import org.bukkit.plugin.java.JavaPlugin;

public class WesleyDevPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        getLogger().info("onEnable is called!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("onDisable is called!");
    }
}
