package nl.wesleydev.wesleydev.listeners;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class EntityDeathListener implements Listener {
    private Plugin plugin;
    private Economy economy;

    public EntityDeathListener(Plugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        LivingEntity livingEntity = event.getEntity();

        if (ShouldHandleEvent(player, livingEntity)) {
            EconomyResponse economyResponse = economy.depositPlayer(player, GetRandomRewardAmount(event));
            if(economyResponse.transactionSuccess()) {
                player.sendMessage(String.format("You killed a %s, giving you %s. You now have %s",
                        livingEntity.getName(),
                        economy.format(economyResponse.amount),
                        economy.format(economyResponse.balance)));
            } else {
                plugin.getLogger().severe(String.format("An error occurred: %s", economyResponse.errorMessage));
            }
        }
    }

    /**
     * @param player The player that killed the livingEntity
     * @param livingEntity The killed livingEntity by player.
     * @return Returns true if player is not null and livingEntity is a monster.
     */
    private boolean ShouldHandleEvent(Player player, LivingEntity livingEntity) {
        return player != null && livingEntity instanceof Monster;
    }

    private int GetRandomRewardAmount(EntityDeathEvent event) {
        int maxHealthKilledEntity = (int) event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        //maxHealthKilledEntity is divided by 2, because max health for vanilla monster is 20
        //This in turn will set the minimum of rewardHealthModifier to 1 when it's a vanilla monster
        //Allowing mobs from plugins with more health to give better rewards, thus making it fair.
        int minRewardHealthModifier = GetPercentage(maxHealthKilledEntity / 2, 10);
        int rewardHealthModifier = GetRandomInteger(minRewardHealthModifier, maxHealthKilledEntity / 2);

        int droppedExp = event.getDroppedExp();
        int minRewardAmount = GetPercentage(droppedExp, 20);
        int maxRewardAmount = GetPercentage(droppedExp, 80) * rewardHealthModifier;
        return GetRandomInteger(minRewardAmount, maxRewardAmount);
    }

    private int GetPercentage(int value, int percentage) {
        return (int)(value*(percentage/100.0f));
    }

    private int GetRandomInteger(int min, int max) {
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }
}
