package net.alliancecraft.alliancedeathchest.Commads.deathchest;

import net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.DeathConfigs;
import net.alliancecraft.allianceutils.Utils.FullEmptyInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class CommandUtils extends DeathConfigs {
    public String SETTINGS_METADATA_OPEN = "alc_deathchest_settings_open";

    public void openPlayerFilesGUI(Player player, int page, JavaPlugin plugin, String dataDirectoryS) {
        File dataDirectory = new File(dataDirectoryS);

        File[] files = dataDirectory.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null || files.length == 0) {
            player.sendMessage("§cNenhum arquivo encontrado.");
            return;
        }

        // Cria o inventário com um título personalizado e um tamanho de 3x9 (27 slots)
        Inventory gui = Bukkit.createInventory(null, 27, "DeathChest - Página " + (page + 1));

        // Coloca os itens no inventário
        int start = page * 26;
        int end = Math.min(start + 26, files.length);

        //Organizar pelos mais recentes
        Arrays.sort(files, start, end, Comparator.comparing(file -> {
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File) file);
            String timestamp = config.getString("info.timestamp");
            return timestamp == null ? "" : timestamp; // Coloca valores nulos no início
        }).reversed()); // Inverte para ordem decrescente (mais recente primeiro)

        for (int i = start; i < end; i++) {
            File file = files[i];

            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            String chestOwnerName = fileConfig.getString("info.owner.name");
            String chestOwnerUUID = fileConfig.getString("info.owner.uuid");
            String chestWorld = fileConfig.getString("info.world");
            String chestTimeStamp = fileConfig.getString("info.timestamp");
            String chestDeathCause = allianceFontReplace("§cNão está no arquivo do bau.");
            if (fileConfig.getString("info.cause") != null){
                chestDeathCause = fileConfig.getString("info.cause");
            }
            String isChestActive = fileConfig.getBoolean("info.active", false) ? "§aAtivado" : "§cDesativado";

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            // Define o nome do item como o nome do arquivo
            meta.setDisplayName(chestOwnerName.replace("§e", "").replace("&e", ""));
            List<String> lore = new ArrayList<>();
            lore.add(file.getName());
            lore.add("");
            lore.add("§e---------------------------------------");
            lore.add(allianceFontReplace("§eDono: §a" + chestOwnerName));
            lore.add(allianceFontReplace("§eUUID: §a" + chestOwnerUUID));
            lore.add(allianceFontReplace("§eMundo: §a" + chestWorld));
            lore.add(allianceFontReplace("§eData: §a" + convertTimestamp(chestTimeStamp)));
            lore.add(allianceFontReplace("§eCausa: §a" + chestDeathCause));
            lore.add(allianceFontReplace("§eStatus: §a" + isChestActive));
            lore.add("§e---------------------------------------");
            lore.add("");
            lore.add(allianceFontReplace("§eClique esquerdo para abrir"));
            lore.add(allianceFontReplace("§eClique direito para mais opções"));
            meta.setLore(lore);
            meta.setOwner(chestOwnerName);
            head.setItemMeta(meta);

            // Coloca o item no inventário
            gui.setItem(i - start, head);
        }

        // Adiciona uma seta de navegação se houver mais páginas
        if (end < files.length) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta meta = arrow.getItemMeta();
            meta.setDisplayName("§aPróxima Página");
            meta.setLore(Arrays.asList(dataDirectoryS));
            arrow.setItemMeta(meta);
            gui.setItem(26, arrow);
        }

        FullEmptyInventory.fillEmptySlotsWith(gui, Material.WHITE_STAINED_GLASS_PANE, 1);

        // Abre o inventário para o jogador
        player.openInventory(gui);
    }

    public void openPlayersFilesGUI(Player player, int page, JavaPlugin plugin) {
        String dir = plugin.getDataFolder() + "/data";
        File dataDirectory = new File(dir);
        File[] files = dataDirectory.listFiles(File::isDirectory);

        if (files == null || files.length == 0) {
            player.sendMessage("§cNenhum arquivo encontrado.");
            return;
        }

        // Cria o inventário com um título personalizado e um tamanho de 3x9 (27 slots)
        Inventory gui = Bukkit.createInventory(null, 27, "DeathChests - Página " + (page + 1));

        // Coloca os itens no inventário
        int start = page * 26;
        int end = Math.min(start + 26, files.length);
        for (int i = start; i < end; i++) {
            File file = files[i];

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            // Define o nome do item como o nome do arquivo
            meta.setDisplayName(file.getName().replace("§e", "").replace("&e", ""));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(allianceFontReplace("§eClique para abrir"));
            meta.setLore(lore);
            meta.setOwner(file.getName());
            head.setItemMeta(meta);

            // Coloca o item no inventário
            gui.setItem(i - start, head);
        }

        // Adiciona uma seta de navegação se houver mais páginas
        if (end < files.length) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta meta = arrow.getItemMeta();
            meta.setDisplayName("§aPróxima Página");
            meta.setLore(Arrays.asList(dir));
            arrow.setItemMeta(meta);
            gui.setItem(26, arrow);
        }

        FullEmptyInventory.fillEmptySlotsWith(gui, Material.WHITE_STAINED_GLASS_PANE, 1);

        // Abre o inventário para o jogador
        player.openInventory(gui);
    }

    public Inventory openSettingsGUI(Player player, String fileString){
        Inventory settingsMenu = Bukkit.createInventory(null, 3*9, "DeathChestSettings");

        ItemStack teleportButton = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta = teleportButton.getItemMeta();

        teleportMeta.setDisplayName(allianceFontReplace("§eTeleportar para o local."));
        List<String> teleportLore = new ArrayList<>();
        teleportLore.add("");
        teleportLore.add("§e---------------------------------------");
        teleportLore.add("");
        teleportLore.add(allianceFontReplace("§a Clique para se teleporta para o local"));
        teleportLore.add(allianceFontReplace("§a onde o baú está"));
        teleportLore.add("");
        teleportLore.add("§e---------------------------------------");
        teleportMeta.setLore(teleportLore);
        teleportButton.setItemMeta(teleportMeta);


        ItemStack openButton = new ItemStack(Material.CHEST);
        ItemMeta openMeta = openButton.getItemMeta();

        openMeta.setDisplayName(allianceFontReplace("§eAbrir"));
        List<String> openLore = new ArrayList<>();
        openLore.add("");
        openLore.add("§e---------------------------------------");
        openLore.add("");
        openLore.add(allianceFontReplace("§a Clique aqui para ver o baú da morte."));
        openLore.add("");
        openLore.add("§e---------------------------------------");
        openMeta.setLore(openLore);
        openButton.setItemMeta(openMeta);

        ItemStack delButton = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta delMeta = delButton.getItemMeta();

        delMeta.setDisplayName(allianceFontReplace("§eExcluir"));
        List<String> delLore = new ArrayList<>();
        delLore.add("");
        delLore.add("§e---------------------------------------");
        delLore.add("");
        delLore.add(allianceFontReplace("§a Clique aqui para excluir o baú."));
        delLore.add("");
        delLore.add("§e---------------------------------------");
        delMeta.setLore(delLore);
        delButton.setItemMeta(delMeta);


        ItemStack infoButton = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta infoMeta = infoButton.getItemMeta();
        infoMeta.setDisplayName(fileString);
        infoButton.setItemMeta(infoMeta);

        settingsMenu.setItem(11, teleportButton);
        settingsMenu.setItem(13, openButton);
        settingsMenu.setItem(15, delButton);
        settingsMenu.setItem(26, infoButton);

        FullEmptyInventory.fillEmptySlotsWith(settingsMenu, Material.WHITE_STAINED_GLASS_PANE, 1);

        return settingsMenu;
    }

    public void deathChestSettingsEvent(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (player.hasMetadata(SETTINGS_METADATA_OPEN) && player.getMetadata(SETTINGS_METADATA_OPEN).get(0) != null){
            MetadataValue metadataValue = player.getMetadata(SETTINGS_METADATA_OPEN).get(0);

            String deathFileString = metadataValue.asString();

            File deathFile = new File(deathFileString);

            if (!deathFile.exists()){
                player.closeInventory();
                player.sendMessage(allianceFontReplace("§cAquivo do baú da morte não encontrado."));
                return;
            }

            YamlConfiguration deathConfig = YamlConfiguration.loadConfiguration(deathFile);

            if (event.getSlot() == 11){

                Location chestLocation = deathConfig.getLocation("chestentity.location");

                if (chestLocation != null){
                    player.sendMessage(allianceFontReplace("§a Teleportando você para o baú da morte..."));
                    player.teleport(chestLocation);
                }
                player.closeInventory();
            } else if (event.getSlot() == 13){
                player.openInventory(createAdminDeathChestInventory(deathFileString, plugin));
            } else if (event.getSlot() == 15){
                if (deathConfig.getBoolean("info.active")){
                    removeDeathChest(deathFileString, UUID.fromString(deathConfig.getString("info.owner.uuid")));
                }
                player.closeInventory();
            }

            player.removeMetadata(SETTINGS_METADATA_OPEN, plugin);
        }
    }

    public void openPlayerFilesEvent(InventoryClickEvent event){
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
                openSettingsGUI(player, fileDir);
                player.setMetadata(SETTINGS_METADATA_OPEN, new FixedMetadataValue(plugin, fileDir));
            }

            // Lógica para manipular o arquivo selecionado, se necessário
        } else if (clickedItem.getType() == Material.ARROW) {
            // Clique na seta de navegação



            String title = event.getView().getTitle();
            int currentPage = Integer.parseInt(title.split("Página ")[1]) - 1;
            openPlayerFilesGUI(player, currentPage + 1, plugin, clickedItem.getItemMeta().getLore().get(0));
        }
    }

    public void openPlayersFilesEvent(InventoryClickEvent event){

    }


    public static String convertTimestamp(String timestamp) {
        // Parse o timestamp para ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp);

        // Converte para a zona de horário brasileira
        LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();

        // Define o formato de saída
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Retorna o resultado formatado
        return localDateTime.format(formatter);
    }
}
