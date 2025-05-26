package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Commads.deathchest.CommandUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandListener extends CommandUtils implements Listener {
    public CommandUtils commandUtils = new CommandUtils();
    private JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);


    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("DeathChest - Página")) {
            event.setCancelled(true); // Previne a retirada de itens

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                // Clique em uma cabeça de jogador
                String fileName = clickedItem.getItemMeta().getDisplayName();

                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();

                String fileDir = plugin.getDataFolder() + "/data/" + fileName + "/" + clickedItem.getItemMeta().getLore().get(0);

                if (event.getClick().isLeftClick()){
                    player.openInventory(createAdminDeathChestInventory(fileDir, plugin));
                } else if (event.getClick().isRightClick()){
                    player.openInventory(commandUtils.openSettingsGUI(player, fileDir));
                    player.setMetadata(commandUtils.SETTINGS_METADATA_OPEN, new FixedMetadataValue(plugin, fileDir));
                }

                // Lógica para manipular o arquivo selecionado, se necessário
            } else if (clickedItem.getType() == Material.ARROW) {
                // Clique na seta de navegação



                String title = event.getView().getTitle();
                int currentPage = Integer.parseInt(title.split("Página ")[1]) - 1;
                commandUtils.openPlayerFilesGUI(player, currentPage + 1, plugin, clickedItem.getItemMeta().getLore().get(0));
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick2(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("DeathChests - Página")) {
            event.setCancelled(true); // Previne a retirada de itens

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                // Clique em uma cabeça de jogador
                String fileName = clickedItem.getItemMeta().getDisplayName();

                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();

                String fileDir = plugin.getDataFolder() + "/data/" + fileName;

                commandUtils.openPlayerFilesGUI(player, 0, plugin, fileDir);

                // Lógica para manipular o arquivo selecionado, se necessário
            } else if (clickedItem.getType() == Material.ARROW) {
                // Clique na seta de navegação



                String title = event.getView().getTitle();
                int currentPage = Integer.parseInt(title.split("Página ")[1]) - 1;
                commandUtils.openPlayerFilesGUI(player, currentPage + 1, plugin, clickedItem.getItemMeta().getLore().get(0));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick3(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("DeathChestSettings")) {
            commandUtils.deathChestSettingsEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event){
        if (event.getView().getTitle().equalsIgnoreCase("DeathChestSettings")) {
            if (event.getPlayer().hasMetadata(commandUtils.SETTINGS_METADATA_OPEN)) {
                event.getPlayer().removeMetadata(SETTINGS_METADATA_OPEN, plugin);
            }
        }
    }
}
