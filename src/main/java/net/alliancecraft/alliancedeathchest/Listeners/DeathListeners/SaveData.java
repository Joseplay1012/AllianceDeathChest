package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.PlayerDeathListener.*;

public class SaveData extends DeathConfigs implements Listener {
    public Map<UUID, BukkitTask> restorePlayerHologram = new HashMap<>();
    public Map<UUID, BukkitTask> restoreChestLater = new HashMap<>();
    public Map<UUID, BukkitTask> restoreActionBar = new HashMap<>();
    private Set<UUID> restorePlayerDeathChestCount = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (playerHologram.containsKey(playerUUID)) {
            restorePlayerHologram.put(playerUUID, playerHologram.get(playerUUID));
        }

        if (chestTaskLaterRemove.get(playerUUID) != null){
            restorePlayerHologram.put(playerUUID, chestTaskLaterRemove.get(playerUUID));
        }

        if (actionbarTasks.get(playerUUID) != null){
            restoreActionBar.put(playerUUID, actionbarTasks.get(playerUUID));
        }

        if (deathPlayerCount.containsKey(player)) {
            restorePlayerDeathChestCount.add(playerUUID);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (restorePlayerHologram.containsKey(playerUUID)) {
            playerHologram.put(playerUUID, restorePlayerHologram.get(playerUUID));
            restorePlayerHologram.remove(playerUUID);
        }

        if (restoreChestLater.get(playerUUID) != null){
            chestTaskLaterRemove.put(playerUUID, restoreChestLater.get(playerUUID));
            restoreChestLater.remove(playerUUID);
        }

        if (restoreActionBar.get(playerUUID) != null){
            actionbarTasks.put(playerUUID, restoreActionBar.get(playerUUID));
            restoreActionBar.remove(playerUUID);
        }

        if (restorePlayerDeathChestCount.contains(playerUUID)) {
            deathPlayerCount.put(player, player);
            restorePlayerDeathChestCount.remove(playerUUID);
        }
    }

}
