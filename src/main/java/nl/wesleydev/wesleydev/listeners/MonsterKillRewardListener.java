package nl.wesleydev.wesleydev.listeners;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import nl.wesleydev.wesleydev.enums.Permission;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MonsterKillRewardListener implements Listener {
    private Plugin plugin;
    private Economy economy;

    /**
     * Monsters that are damaged are stored in this HashMap as UUID with the players UUIDs that damaged the monster
     */
    private static HashMap<UUID, Collection<UUID>> monsterDamagedByPlayersTable = new HashMap<UUID, Collection<UUID>>();

    public MonsterKillRewardListener(Plugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (shouldHandleEntityDeathEvent(event)) {
            UUID monsterUUID = event.getEntity().getUniqueId();
            Collection<UUID> players = monsterDamagedByPlayersTable.get(monsterUUID);

            if (players.size() == 1 ) {
                handleEconomyDeposit(event);
            } else {
                handleEconomyDeposit(players, event);
            }
            removeMonsterFromDamagedTable(monsterUUID);
        }
    }

    /**
     * @return Returns true if player is not null, livingEntity is a monster
     * and monsterDamagedByPlayersTable contains the killed monster.
     */
    private boolean shouldHandleEntityDeathEvent(EntityDeathEvent event) {
        return event.getEntity().getKiller() != null &&
                event.getEntity() instanceof Monster &&
                monsterDamagedByPlayersTable.containsKey(event.getEntity().getUniqueId());
    }

    private void handleEconomyDeposit(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        if (player != null) {
            EconomyResponse economyResponse = economy.depositPlayer(player, getRandomRewardAmount(event));
            if (economyResponse.transactionSuccess()) {
                player.sendMessage(String.format("You killed a %s, giving you %s. You now have %s",
                        event.getEntity().getName(),
                        economy.format(economyResponse.amount),
                        economy.format(economyResponse.balance)));
            } else {
                plugin.getLogger().severe(String.format("An error occurred: %s", economyResponse.errorMessage));
            }
        }
    }

    private void handleEconomyDeposit(Collection<UUID> playerUUIDs, EntityDeathEvent event) {
        Collection<Player> players = getPlayers(playerUUIDs);

        int reward = getRandomRewardAmount(event);
        //If the reward is smaller than the amount of players, reward is set to the amount of players
        if (reward < players.size()) reward = players.size();
        //reward is shared across the players
        reward = reward / players.size();

        for (Player player : players) {
            EconomyResponse economyResponse = economy.depositPlayer(player, reward);
            if (economyResponse.transactionSuccess()) {
                player.sendMessage(String.format("You helped kill a %s, giving you a share of %s. You now have %s",
                        event.getEntity().getName(),
                        economy.format(economyResponse.amount),
                        economy.format(economyResponse.balance)));
            } else {
                plugin.getLogger().severe(String.format("An error occurred: %s", economyResponse.errorMessage));
            }
        }
    }

    private Collection<Player> getPlayers(Collection<UUID> playerUUIDs) {
        Collection<Player> players = new ArrayList<Player>();

        for (UUID playerUUID : playerUUIDs) {
            Player player = plugin.getServer().getPlayer(playerUUID);

            if (player != null) players.add(player);
        }

        return players;
    }

    private int getRandomRewardAmount(EntityDeathEvent event) {
        int maxHealthKilledEntity = (int) event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        //maxHealthKilledEntity is divided by 2, because max health for vanilla monster is 20
        //This in turn will set the minimum of rewardHealthModifier to 1 when it's a vanilla monster
        //Allowing mobs from plugins with more health to give better rewards, thus making it fair.
        int minRewardHealthModifier = getPercentage(maxHealthKilledEntity / 2, 10);
        int rewardHealthModifier = getRandomInteger(minRewardHealthModifier, maxHealthKilledEntity / 2);

        int droppedExp = event.getDroppedExp();
        int minRewardAmount = getPercentage(droppedExp, 20);
        int maxRewardAmount = getPercentage(droppedExp, 80) * rewardHealthModifier;
        return getRandomInteger(minRewardAmount, maxRewardAmount);
    }

    private int getPercentage(int value, int percentage) {
        return (int)(value*(percentage/100.0f));
    }

    private int getRandomInteger(int min, int max) {
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }

    /**
     * When a player damages a monster it's registered in monsterDamagedByPlayersTable.
     * This will allow the plugin to give everyone money when a mob is killed by multiple players
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (shouldHandleEntityDamageByEntityEvent(event)) {
            UUID monsterUUID = event.getEntity().getUniqueId();
            UUID playerUUID = event.getDamager().getUniqueId();
            Collection<UUID> players;

            if (monsterDamagedByPlayersTable.containsKey(monsterUUID)) {
                players = monsterDamagedByPlayersTable.get(monsterUUID);
                if (!players.contains(playerUUID)) players.add(playerUUID);
            } else {
                players = new ArrayList<UUID>(Arrays.asList(playerUUID));
            }

            monsterDamagedByPlayersTable.put(monsterUUID, players);
        }
    }

    /**
     * @return Returns true if entity is a monster and damaged by a player.
     */
    private boolean shouldHandleEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        return event.getDamager() instanceof Player && event.getEntity() instanceof Monster
                && hasMonsterKillRewardPermission(event.getDamager());
    }

    private boolean hasMonsterKillRewardPermission(CommandSender sender) {
        return Permission.hasPermission(sender, Permission.ECONOMY)
                || Permission.hasPermission(sender, Permission.ECONOMY_MONSTERKILLREWARD);
    }

    /**
     * Making sure monsterDamagedByPlayersTable is clearing out monsters that are still alive in an unloaded chunk
     */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Monster) {
                removeMonsterFromDamagedTable(entity.getUniqueId());
            }
        }
    }

    private void removeMonsterFromDamagedTable(UUID monsterUUID) {
        if (monsterDamagedByPlayersTable.containsKey(monsterUUID)) {
            monsterDamagedByPlayersTable.remove(monsterUUID);
        }
    }
}
