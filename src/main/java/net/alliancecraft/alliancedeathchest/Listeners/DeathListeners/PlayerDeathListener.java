package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Utils.UnicodeFontReplace;
import net.alliancecraft.allianceutils.Utils.GradientMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

import static net.alliancecraft.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;
import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class PlayerDeathListener implements Listener {
    private JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public static Map<UUID, BukkitTask> playerHologram = new HashMap<>();

    public static Map<UUID, BukkitTask> chestTaskLaterRemove = new HashMap<>();

    public static Map<UUID, BukkitTask> actionbarTasks = new HashMap<>();

    public static Map<Player, Player> deathPlayerCount = new HashMap<>();

    public DeathConfigs deathConfigs = new DeathConfigs();

    public List<String> allowedWorlds(JavaPlugin plugin){
        return deathConfigs.allowedWorlds(plugin);
    }

    public void createDeathChestFile(PlayerInventory playerInventory, String fileName, String cause){
        deathConfigs.createDeathChestFile(playerInventory, fileName, cause);
    }

    public Location createDeathChest(Location location, Player player, String fileDir, JavaPlugin plugin){
        return deathConfigs.createDeathChest(location, player, fileDir, plugin);
    }

    public ArmorStand createHologram(Location location, List<String> lines, String fileDir, JavaPlugin plugin){
        return deathConfigs.createHologram(location, lines, fileDir, plugin);
    }

    public void startDeathTimer(String fileDir, ArmorStand armorStand, JavaPlugin plugin, Player player){
        deathConfigs.startDeathTimer(fileDir, armorStand, plugin, player);
    }

    public void removeDeathChest(String fileDir, UUID playerUUID){
        deathConfigs.removeDeathChest(fileDir, playerUUID);
    }

    public void startCountAction(UUID playerUUID, String timeString){
//        if (timeString.contains("-")){
//            stopCountAction(playerUUID);
//            return;
//        }

        deathConfigs.startCountAction(playerUUID, timeString);
    }

    public void stopCountAction(UUID player) {
        if (actionbarTasks.get(player) != null) {
            actionbarTasks.get(player).cancel();
            actionbarTasks.remove(player, actionbarTasks.get(player));
        }
    }

    public void dropItens(Location location, String fileName){
        deathConfigs.dropItens(location, fileName);
    }

    public String getDeathCauseMessage(PlayerDeathEvent event){
       return deathConfigs.getDeathCauseMessage(event);
    }

    public static void removeDeathCount(Player player){
        deathPlayerCount.remove(player);
    }

    public List<String> getDeathMessage(JavaPlugin plugin){
        return deathConfigs.getDeathMessage(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPLayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (deathPlayerCount.containsKey(player)) {
            player.sendMessage(UnicodeFontReplace.allianceFontReplace("§cVocê já tem um baú da morte."));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(allianceFontReplace("§cVocê já tem um baú da morte.")));
        } else {
            if (allowedWorlds(plugin).contains(event.getEntity().getWorld().getName())) {
                List<String> messages = plugin.getConfig().getStringList("death-chest.notAllowedItems");
                for (String message : messages) {
                    event.getEntity().sendMessage(GradientMessage.createGradientMessageAsString(message));
                }
                return;
            }

            if (event.getDrops().isEmpty()) return;

            deathPlayerCount.put(player, player);
            onPlayerDeathCreateChest(event);
        }
    }


    public void onPlayerDeathCreateChest(PlayerDeathEvent event){
        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty()) return;

        int deathChestDuration = plugin.getConfig().getInt("death-chest.disappear", 30);
        final int[] ticks = {deathChestDuration * 20};

        Player player = event.getEntity();
        PlayerInventory playerInventory = player.getInventory();
        Location location = player.getLocation().getBlock().getLocation();

        String fileDir = plugin.getDataFolder() + "/data/" + player.getName() + "/" + player.getName() + "-" + new Random().nextInt(10000) + "-" + new Random().nextInt(10000) + ".yml";
        List<String> lines = new ArrayList<>();
        lines.add(allianceFontReplace("§cBaú da morte"));
        lines.add(allianceFontReplace("§b" + player.getName()));

        location.getChunk().load();
        location.getChunk().setForceLoaded(true);

        createDeathChestFile(playerInventory, fileDir, getDeathCauseMessage(event));

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(fileDir));

        Location deathChestLocation = createDeathChest(location.clone().add(0.5, 0, 0.5), player, fileDir, plugin);

        startDeathTimer(fileDir, createHologram(deathChestLocation, lines, fileDir, plugin), plugin, player);

        BukkitTask taskLaterChest = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dropItens(location, fileDir);
            player.sendMessage(allianceFontReplace("§cSeu baú da morte foi excluido, e os itens foram dropados."));
            stopCountAction(player.getUniqueId());
            removeDeathChest(fileDir, player.getUniqueId());
            if (location.getChunk().isLoaded()){
                location.getChunk().setForceLoaded(false);
            }
        },ticks[0]);

        chestTaskLaterRemove.put(player.getUniqueId(), taskLaterChest);
        drops.clear();
    }
}
