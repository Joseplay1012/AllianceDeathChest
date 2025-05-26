package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Utils.InventoryMetadataManager;
import net.alliancecraft.allianceutils.Utils.GradientMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.alliancecraft.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;
import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class PlayerInteractiListener extends DeathConfigs implements Listener {
    private JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;

        ArmorStand chest = (ArmorStand) event.getRightClicked();

        Player player = event.getPlayer();

        if (chest.hasMetadata(graveMetaData)) {

            event.setCancelled(true);

            String fileDir = chest.getMetadata(graveMetaData).get(0).asString();

            File chestFile = new File(fileDir);

            if (!chestFile.exists()) {
                openNotFoundFile(player);
                return;
            }

            YamlConfiguration chestConfig = YamlConfiguration.loadConfiguration(chestFile);
            String chestOwner = chestConfig.getString("info.owner.name");

            openDeathChest(player, chestOwner, fileDir, "MetaData");
        } else if (chest.getPersistentDataContainer().has(graveNameSpaceKey, PersistentDataType.STRING)){
            event.setCancelled(true);

            String fileDir = chest.getPersistentDataContainer().get(graveNameSpaceKey, PersistentDataType.STRING);

            File chestFile = new File(fileDir);

            if (!chestFile.exists()) {
                openNotFoundFile(player);
                return;
            }

            YamlConfiguration chestConfig = YamlConfiguration.loadConfiguration(chestFile);
            String chestOwner = chestConfig.getString("info.owner.name");

            openDeathChest(player, chestOwner, fileDir, "PersistentData");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestInventoryClick(InventoryClickEvent event) {
        InventoryMetadataManager inventoryMetadataManager = new InventoryMetadataManager(plugin);

        String inventoryName = event.getView().getTitle();

        ItemStack clickItem = event.getCurrentItem();

        int clickSlot = event.getSlot();

        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getClickedInventory();

        if (inventoryName.equalsIgnoreCase("AllianceDeathChest") || inventoryName.contains(getDeathChestTitle())) {

            event.setCancelled(true);
            if (clickItem != null && clickItem.hasItemMeta()) {
                ItemStack itemInfo = inventory.getItem(53);
                ItemStack itemInfo2 = inventory.getItem(52);
                ItemStack itemInfo3 = inventory.getItem(51);
                String fileDir = "";
                String playerUUID = "";

                YamlConfiguration chestConfig = null;

                if (itemInfo != null && itemInfo.hasItemMeta() && itemInfo.getItemMeta().hasDisplayName()){
                    fileDir = plugin.getDataFolder() + "/data/" + itemInfo.getItemMeta().getDisplayName() + "/" + itemInfo3.getItemMeta().getDisplayName();
                }

                if (itemInfo2 != null && itemInfo2.hasItemMeta() && itemInfo2.getItemMeta().hasDisplayName()){
                    playerUUID = itemInfo2.getItemMeta().getDisplayName();
                }

                if (clickItem.getItemMeta().hasDisplayName()) {
                    String buttonCollectName = GradientMessage.createGradientMessageAsString(plugin.getConfig().getString("buttons.collect.name"));
                    String buttonDeleteName = GradientMessage.createGradientMessageAsString(plugin.getConfig().getString("buttons.delete.name"));
                    if (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(buttonCollectName)) {
                        player.closeInventory();
                        restorePlayerDeathChest(player, fileDir);
                        removeDeathChest(fileDir, UUID.fromString(playerUUID)); //remover
                    } else if (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(buttonDeleteName)) {
                        player.closeInventory();
                        removeDeathChest(fileDir, UUID.fromString(playerUUID));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdminChestInventoryClick(InventoryClickEvent event) {
        InventoryMetadataManager inventoryMetadataManager = new InventoryMetadataManager(plugin);

        String inventoryName = event.getView().getTitle();

        ItemStack clickItem = event.getCurrentItem();

        int clickSlot = event.getSlot();

        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getClickedInventory();

        if (inventoryName.equalsIgnoreCase("AllianceDeathChest") || inventoryName.contains(allianceFontReplace("§e Admin - bau -"))) {

            event.setCancelled(true);
            if (clickItem != null && clickItem.hasItemMeta()) {
                ItemStack itemInfo = inventory.getItem(53);
                ItemStack itemInfo2 = inventory.getItem(52);
                ItemStack itemInfo3 = inventory.getItem(51);
                String fileDir = "";
                String playerUUID = "";

                YamlConfiguration chestConfig = null;

                if (itemInfo != null && itemInfo.hasItemMeta() && itemInfo.getItemMeta().hasDisplayName()){
                    fileDir = plugin.getDataFolder() + "/data/" + itemInfo.getItemMeta().getDisplayName() + "/" + itemInfo3.getItemMeta().getDisplayName();
                }

                if (itemInfo2 != null && itemInfo2.hasItemMeta() && itemInfo2.getItemMeta().hasDisplayName()){
                    playerUUID = itemInfo2.getItemMeta().getDisplayName();
                }

                if (clickItem.getItemMeta().hasDisplayName()) {
                    if (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(allianceFontReplace("§aDevolver items"))) {
                        player.closeInventory();

                        Player chestOwnerPlayer = Bukkit.getPlayer(itemInfo.getItemMeta().getDisplayName());

                        if (chestOwnerPlayer != null && chestOwnerPlayer.isOnline()) {
                            restorePlayerDeathChest(chestOwnerPlayer, fileDir);
                            removeDeathChest(fileDir, UUID.fromString(playerUUID)); //remover
                        } else {
                            player.sendMessage(allianceFontReplace("§cjogador não esta online."));
                        }
                    }
                }
            }
        }
    }
}
