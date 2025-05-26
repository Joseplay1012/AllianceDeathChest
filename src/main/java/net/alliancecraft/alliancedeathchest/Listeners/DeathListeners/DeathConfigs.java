package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import io.netty.handler.codec.string.LineSeparator;
import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Utils.InventoryMetadataManager;
import net.alliancecraft.alliancedeathchest.Utils.ParticlesUtils;
import net.alliancecraft.alliancedeathchest.Utils.RegionsUtils;
import net.alliancecraft.alliancedeathchest.Utils.UnicodeFontReplace;
import net.alliancecraft.allianceutils.Allianceutils;
import net.alliancecraft.allianceutils.Statics.SkullApi;
import net.alliancecraft.allianceutils.Utils.GradientMessage;
import net.alliancecraft.allianceutils.api.pluginComunicate.PluginChannelDispatcher;
import net.alliancecraft.allianceutils.features.superItens.Datas.SuperBowDataConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.PlayerDeathListener.*;
import static net.alliancecraft.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;
import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class DeathConfigs extends CemeteryConfig {

    public Random random = new Random();

    public Map<UUID, BukkitTask> playerHologram() {
        return playerHologram;
    }

    public Map<UUID, BukkitTask> chestTaskLaterRemove() {
        return chestTaskLaterRemove;
    }

    public Map<UUID, BukkitTask> actionbarTasks() {
        return actionbarTasks;
    }

    public Map<Player, Player> deathPlayerCount() {
        return deathPlayerCount;
    }

    public NamespacedKey graveNameSpaceKey = new NamespacedKey("alliancedeathchest", "file_name");

    public String graveMetaData = "alliancedeathchest";

    public JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public RegionsUtils regionsUtils = new RegionsUtils();

    public String graveMaterial(JavaPlugin plugin) {
        return plugin.getConfig().getString("death-chest.material").toUpperCase();
    }

    public boolean isCreateCemeteryGround(JavaPlugin plugin) {
        return plugin.getConfig().getBoolean("death-chest.createcemeteryground");
    }

    public int getCreateCemeteryGroundSize(JavaPlugin plugin) {
        return plugin.getConfig().getInt("death-chest.cemeterysize");
    }

    public boolean isPlaySound = plugin.getConfig().getBoolean("death-chest.sounduse", false);

    public Sound getSound = Sound.valueOf(plugin.getConfig().getString("death-chest.sound", "BLOCK_NOTE_BLOCK_BASS").toUpperCase());

    public Integer graveModel(JavaPlugin plugin) {
        return plugin.getConfig().getInt("death-chest.model");
    }

    public double graveScale(JavaPlugin plugin) {
        return plugin.getConfig().getDouble("death-chest.scale", 1.0);
    }

    public boolean graveMarke(JavaPlugin plugin) {
        return plugin.getConfig().getBoolean("death-chest.marke", false);
    }

    public double graveOffSet(JavaPlugin plugin) {
        return plugin.getConfig().getDouble("death-chest.offset", 0.0);
    }

    public List<String> getDeathMessage(JavaPlugin plugin) {
        return plugin.getConfig().getStringList("death-chest.deahmessage");
    }

    public Location createDeathChest(Location location, Player player, String fileName, JavaPlugin plugin) {

        File graveFile = new File(fileName);
        if (!graveFile.exists()) {
            Bukkit.broadcast(sendDebugMessage("Arquivo do bau da morte: §e" + fileName + "não foi criado."), "alc.admin");
            return location;
        }

        YamlConfiguration graveConfig = YamlConfiguration.loadConfiguration(graveFile);

        //Verifica de não tem nem um bloco no lugar, como lava, água etc..
        while (!location.getBlock().getType().isAir()) {
            location.add(0, 1, 0);
        }

        int locationY = (int) location.getY();
        if (location.getY() < -64) {

            location.setY(1.0);

            if (location.getWorld().getEnvironment().equals(World.Environment.THE_END)){
                location.setY(60);
            }
        }

        //Cria o Armo stand do bau
        ArmorStand grave = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, graveOffSet(plugin), 0), EntityType.ARMOR_STAND);
        //ArmorStand grave2 = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, -1, 0), EntityType.ARMOR_STAND);

        //Definir um tamanho para o armostand (1.20.5 +)
        grave.getAttribute(Attribute.SCALE).setBaseValue(graveScale(plugin));

        //Definir se ele vai ser um Marke
        grave.setMarker(graveMarke(plugin));

        //Item que vai ser o da Textura
        ItemStack graveItem = new ItemStack(Material.valueOf(graveMaterial(plugin)));

        //Meta do item
        ItemMeta graveItemMeta = graveItem.getItemMeta();

        //Coloca o ModelData do Item da textura
        graveItemMeta.setCustomModelData(graveModel(plugin));

        //Coloca nome do arquivo no item
        graveItemMeta.setDisplayName(fileName);

        //Seta o ItemMeta
        graveItem.setItemMeta(graveItemMeta);

        //Coloca o item na cabeça do armo stand
        grave.setHelmet(graveItem);
        //grave2.setHelmet(graveItem);

        //Configs do armor stand
        grave.setVisible(false);
        grave.setInvulnerable(true);
        grave.setSmall(false);
        grave.setMarker(false);
        grave.setGravity(false);

        //Grave2
        /*
        grave2.setVisible(false);
        grave2.setInvulnerable(true);
        grave2.setSmall(false);
        grave2.setMarker(false);
        grave2.setGravity(false);*/

        grave.setMetadata(graveMetaData, new FixedMetadataValue(plugin, fileName));
        grave.getPersistentDataContainer().set(graveNameSpaceKey, PersistentDataType.STRING, fileName);

        //Grave2
        //grave2.setMetadata(graveMetaData, new FixedMetadataValue(plugin, fileName));
        //grave2.getPersistentDataContainer().set(graveNameSpaceKey, PersistentDataType.STRING, fileName);


        //Salva dados do bau na config
        graveConfig.set("chestentity.location", grave.getLocation());
        graveConfig.set("chestentity.uuid", grave.getUniqueId().toString());

        //Grave
        //graveConfig.set("chestentity2.location", grave2.getLocation());
        //graveConfig.set("chestentity2.uuid", grave2.getUniqueId().toString());

        Location secureLocation = location.clone().add(0, -1, 0);

        if (secureLocation.getBlock().getType().isAir()) {
            if (regionsUtils.isValidLocation(location, player)) {
                secureLocation.getBlock().setType(Material.GLASS);
            }
        } else if (secureLocation.getBlock().getType().isAir() && secureLocation.clone().add(0, -1, 0).getBlock().getType().isBlock() && isCreateCemeteryGround(plugin) && regionsUtils.isValidLocation(location, player)) {
            createCemeteryGround(secureLocation, getCreateCemeteryGroundSize(plugin), player, graveConfig);
        } else if (secureLocation.getBlock().getType().isBlock() && isCreateCemeteryGround(plugin) && regionsUtils.isValidLocation(location, player)) {
            createCemeteryGround(secureLocation, getCreateCemeteryGroundSize(plugin), player, graveConfig);
        }

        for (String s : getDeathMessage(plugin)) {
            String formattedLocation = (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ();
            player.sendMessage(GradientMessage.createGradientMessageAsString(
                    s.replace("%loc%", formattedLocation).replace("%world%", location.getWorld().getName())
            ));
        }

        try {
            graveConfig.save(graveFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return location;
    }

    public void openDeathChest(Player player, String chestOwner, String fileDir, String mode) {
        if (player.getName().equalsIgnoreCase(chestOwner)) {
            player.sendMessage(allianceFontReplace("§aAbrindo baú no modo: " + mode));
            player.openInventory(createDeathChestInventory(fileDir, plugin));
        } else if (player.hasPermission("alc.admin")) {
            player.sendMessage(allianceFontReplace("§aAbrindo baú no modo: " + mode));
            player.openInventory(createDeathChestInventory(fileDir, plugin));
        } else {
            player.sendMessage(allianceFontReplace("§cVoce não pode abrir esse bau da morte."));
        }
    }

    public void createDeathChestFile(PlayerInventory inventory, String fileName, String cause) {
        File inventoryFile = new File(fileName);

        if (inventoryFile.exists()) {
            inventoryFile.delete();
        }

        if (!inventoryFile.exists()) {
            try {
                inventoryFile.getParentFile().mkdirs();
                inventoryFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Não foi possível criar o arquivo: " + fileName, e);
            }
        }

        YamlConfiguration inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);

        // Salvando o inventário principal
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
                inventoryConfig.set("inventory." + i, item);
            }
        }

        // Salvando a armadura e a mão secundária
//        saveArmorItem(inventoryConfig, "armor.helmet", inventory.getHelmet());
//        saveArmorItem(inventoryConfig, "armor.chestplate", inventory.getChestplate());
//        saveArmorItem(inventoryConfig, "armor.leggings", inventory.getLeggings());
//        saveArmorItem(inventoryConfig, "armor.boots", inventory.getBoots());
//        saveArmorItem(inventoryConfig, "armor.offhand", inventory.getItemInOffHand());

        // Salvando as informações do jogador
        if (inventory.getHolder() instanceof Player) {
            Player player = (Player) inventory.getHolder();
            UUID playerUUID = player.getUniqueId();

            inventoryConfig.set("info.owner.name", player.getName());
            inventoryConfig.set("info.owner.uuid", playerUUID.toString());
            inventoryConfig.set("info.timestamp", Instant.now().toString());
            inventoryConfig.set("info.world", player.getLocation().getWorld().getName());
            inventoryConfig.set("info.cause", cause);
            inventoryConfig.set("info.active", true);
        }

        // Salvando o arquivo de configuração
        try {
            inventoryConfig.save(inventoryFile);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar o arquivo: " + fileName, e);
        }
    }

    // Metodo auxiliar para salvar um item de armadura
    private void saveArmorItem(YamlConfiguration config, String path, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            config.set(path, item);
        }
    }

    public Inventory createDeathChestInventory(String fileName, JavaPlugin plugin) {
        FileConfiguration pluginConfig = plugin.getConfig();

        Inventory inventoryNull = Bukkit.createInventory(null, 9, "AllianceDeathChest");

        File inventoryFile = new File(fileName);

        if (!inventoryFile.exists()) {
            ItemStack itemStack = new ItemStack(Material.STONE);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(allianceFontReplace("§c Arquivo do báu da morte não encontrado."));
            itemMeta.setLore(Arrays.asList(allianceFontReplace("§cNão foi possivel achar o arquivo desse báu da morte.")));

            itemStack.setItemMeta(itemMeta);

            inventoryNull.setItem(4, itemStack);

            return inventoryNull;
        }

        YamlConfiguration inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, getDeathChestTitle());

        if (inventoryConfig.get("inventory") != null) {
            for (String itemKey : inventoryConfig.getConfigurationSection("inventory").getKeys(false)) {
                inventory.addItem(inventoryConfig.getItemStack("inventory." + itemKey));
            }
        }

        if (pluginConfig.get("buttons") != null) {
            for (String buttonKey : pluginConfig.getConfigurationSection("buttons").getKeys(false)) {

                String buttonName = GradientMessage.createGradientMessageAsString(pluginConfig.getString("buttons." + buttonKey + ".name"));

                int buttonSlot = pluginConfig.getInt("buttons." + buttonKey + ".slot");

                List<String> buttonLore = new ArrayList<>();
                for (String s : pluginConfig.getStringList("buttons." + buttonKey + ".lore")) {
                    buttonLore.add(GradientMessage.createGradientMessageAsString(s));
                }

                String buttonMaterial = GradientMessage.createGradientMessageAsString(pluginConfig.getString("buttons." + buttonKey + ".material", "STONE").toUpperCase());

                String buttonUrl = GradientMessage.createGradientMessageAsString(pluginConfig.getString("buttons." + buttonKey + ".url"));

                int buttonModel = pluginConfig.getInt("buttons." + buttonKey + ".model");

                Boolean buttonGlow = pluginConfig.getBoolean("buttons." + buttonKey + ".glow");

                if (buttonMaterial.equalsIgnoreCase("PLAYER_HEAD")) {
                    ItemStack p = SkullApi.getSkull(buttonName, buttonLore, buttonUrl);
                    ItemMeta pm = p.getItemMeta();
                    pm.getPersistentDataContainer().set(graveNameSpaceKey, PersistentDataType.STRING, fileName);

                    if (buttonGlow) {
                        pm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        pm.addEnchant(Enchantment.MENDING, 1, false);
                    }

                    p.setItemMeta(pm);

                    inventory.setItem(buttonSlot, p);
                } else {

                    ItemStack itemStack = new ItemStack(Material.valueOf(buttonMaterial));

                    ItemMeta itemMeta = itemStack.getItemMeta();

                    itemMeta.setDisplayName(buttonName);
                    itemMeta.setLore(buttonLore);
                    itemMeta.setCustomModelData(buttonModel);

                    if (buttonGlow) {
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        itemMeta.addEnchant(Enchantment.MENDING, 1, false);
                    }

                    itemMeta.getPersistentDataContainer().set(graveNameSpaceKey, PersistentDataType.STRING, fileName);
                    itemStack.setItemMeta(itemMeta);

                    inventory.setItem(buttonSlot, itemStack);

                }
            }
        }

        ItemStack info = new ItemStack(Material.STICK);
        ItemMeta infoMeta = info.getItemMeta();

        infoMeta.setDisplayName(inventoryConfig.getString("info.owner.name"));
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);


        info.setItemMeta(infoMeta);

        inventory.setItem(53, info);

        ItemStack info2 = new ItemStack(Material.STICK);

        infoMeta.setDisplayName(inventoryConfig.getString("info.owner.uuid"));
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);

        info2.setItemMeta(infoMeta);

        inventory.setItem(52, info2);

        ItemStack info3 = new ItemStack(Material.STICK);
        infoMeta.setDisplayName(inventoryFile.getName());
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);

        info3.setItemMeta(infoMeta);

        inventory.setItem(51, info3);

        return inventory;
    }

    public Inventory createAdminDeathChestInventory(String fileName, JavaPlugin plugin) {
        FileConfiguration pluginConfig = plugin.getConfig();

        Inventory inventoryNull = Bukkit.createInventory(null, 9, "AllianceDeathChest");

        File inventoryFile = new File(fileName);

        if (!inventoryFile.exists()) {
            ItemStack itemStack = new ItemStack(Material.STONE);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(allianceFontReplace("§c Arquivo do báu da morte não encontrado."));
            itemMeta.setLore(Arrays.asList(allianceFontReplace("§cNão foi possivel achar o arquivo desse báu da morte.")));

            itemStack.setItemMeta(itemMeta);

            inventoryNull.setItem(4, itemStack);

            return inventoryNull;
        }

        YamlConfiguration inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, allianceFontReplace("§e Admin - bau - " + inventoryConfig.getString("info.owner.name")));

        if (inventoryConfig.get("inventory") != null) {
            for (String itemKey : inventoryConfig.getConfigurationSection("inventory").getKeys(false)) {
                inventory.addItem(inventoryConfig.getItemStack("inventory." + itemKey));
            }
        }

        inventory.setItem(49, SkullApi.getSkull(allianceFontReplace("§aDevolver items"), Arrays.asList("", allianceFontReplace("§aClique para devolver os itens ao jogador")), "https://textures.minecraft.net/texture/d9b2983c01b8da7dc1c0f12d02c4ab20cd8e6875e8df69eae2a867baee6236d4"));

        ItemStack info = new ItemStack(Material.STICK);
        ItemMeta infoMeta = info.getItemMeta();

        infoMeta.setDisplayName(inventoryConfig.getString("info.owner.name"));
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);

        info.setItemMeta(infoMeta);

        inventory.setItem(53, info);

        ItemStack info2 = new ItemStack(Material.STICK);

        infoMeta.setDisplayName(inventoryConfig.getString("info.owner.uuid"));
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);

        info2.setItemMeta(infoMeta);

        inventory.setItem(52, info2);

        ItemStack info3 = new ItemStack(Material.STICK);
        infoMeta.setDisplayName(inventoryFile.getName());
        infoMeta.setLore(Arrays.asList(allianceFontReplace(""), allianceFontReplace("§cEsse item é de infos para esse bau.")));
        infoMeta.setCustomModelData(1);

        info3.setItemMeta(infoMeta);

        inventory.setItem(51, info3);


        return inventory;
    }

    public void openNotFoundFile(Player player) {
        // Cria um ItemStack do tipo livro escrito
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        // Configura o título do livro
        bookMeta.setTitle("AllianceDeathChest");
        bookMeta.setAuthor("AllianceUtils"); // O autor é obrigatório em WRITTEN_BOOK

        // Adiciona uma página ao livro
        bookMeta.addPage(
                allianceFontReplace("§cnão foi possível achar o arquivo desse baú da morte.\n") +
                        "\n" +
                        allianceFontReplace("§co arquivo desse baú da morte não existe!\n\n") +
                        allianceFontReplace("§ctente contatar um administrador para te ajudar.\n\n")
        );

        // Define a meta no livro
        book.setItemMeta(bookMeta);

        // Abre o livro para o jogador
        player.openBook(book);
    }

    public String getDeathCauseMessage(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Entity killer = player.getKiller();  // Obtém o assassino se for um jogador

        if (killer != null) {
            // Se o assassino foi um jogador
            return player.getName() + " foi morto por " + killer.getName();
        } else {
            // Se não foi um jogador, verifica o tipo de dano
            EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();

            switch (cause) {
                case ENTITY_ATTACK:
                    Entity damager = player.getLastDamageCause().getEntity();
                    if (damager != null) {
                        return allianceFontReplace("morto por uma entidade (" + damager.getType().name() + ")");
                    } else {
                        return allianceFontReplace("§centidade desconhecida.");
                    }

                case FALL:
                    return allianceFontReplace("morreu de queda");

                case FIRE:
                    return allianceFontReplace("morreu queimado.");

                case DROWNING:
                    return allianceFontReplace("morreu afogado.");

                default:
                    return allianceFontReplace("morreu de " + cause.name().toLowerCase().replace("_", " ") + ".");
            }
        }
    }

    public void restorePlayerDeathChest(Player player, String fileName) {
        String playerName = player.getName();
        PlayerInventory playerInventory = player.getInventory();

        File restoreFile = new File(fileName);
        if (!restoreFile.exists()) {
            player.sendMessage(allianceFontReplace("não foi possivel achar o arquivo desse bau"));
            return;
        }

        YamlConfiguration restoreConfig = YamlConfiguration.loadConfiguration(restoreFile);

        if (!restoreConfig.getBoolean("info.active")){
            player.sendMessage(allianceFontReplace("§cEsse bau da morte já foi coletado."));
            return;
        }

        if (restoreConfig.get("inventory") != null) {
            for (String slotKey : restoreConfig.getConfigurationSection("inventory").getKeys(false)) {
                int slot = Integer.parseInt(slotKey);
                if (playerInventory.getItem(slot) != null && !playerInventory.getItem(slot).getType().equals(Material.AIR)) {
                    // Se o slot já está ocupado, dropa o item no mundo
                    player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("inventory." + slotKey));
                    continue;
                }

                // Coloca o item restaurado no slot vazio
                playerInventory.setItem(slot, restoreConfig.getItemStack("inventory." + slotKey));
            }
        }

        /**if (restoreConfig.get("armor") != null){
         if (playerInventory.getHelmet() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.helmet"));
         } else {
         playerInventory.setHelmet(restoreConfig.getItemStack("armor.helmet"));
         }

         if (playerInventory.getChestplate() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.chestplate"));
         } else {
         playerInventory.setChestplate(restoreConfig.getItemStack("armor.chestplate"));
         }

         if (playerInventory.getLeggings() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.leggings"));
         } else {
         playerInventory.setLeggings(restoreConfig.getItemStack("armor.leggings"));
         }

         if (playerInventory.getBoots() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.boots"));
         } else {
         playerInventory.setBoots(restoreConfig.getItemStack("armor.boots"));
         }

         if (playerInventory.getItemInOffHand() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.offhand"));
         } else {
         playerInventory.setItemInOffHand(restoreConfig.getItemStack("armor.offhand"));
         }
         }*/
    }

    public void dropItens(Location location, String fileName) {

        File restoreFile = new File(fileName);
        if (!restoreFile.exists()) {
            return;
        }

        YamlConfiguration restoreConfig = YamlConfiguration.loadConfiguration(restoreFile);

        if (restoreConfig.get("inventory") != null) {
            for (String slotKey : restoreConfig.getConfigurationSection("inventory").getKeys(false)) {
                int slot = Integer.parseInt(slotKey);
                location.getWorld().dropItem(location, restoreConfig.getItemStack("inventory." + slotKey));
            }
        }

        /**if (restoreConfig.get("armor") != null){
         if (playerInventory.getHelmet() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.helmet"));
         } else {
         playerInventory.setHelmet(restoreConfig.getItemStack("armor.helmet"));
         }

         if (playerInventory.getChestplate() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.chestplate"));
         } else {
         playerInventory.setChestplate(restoreConfig.getItemStack("armor.chestplate"));
         }

         if (playerInventory.getLeggings() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.leggings"));
         } else {
         playerInventory.setLeggings(restoreConfig.getItemStack("armor.leggings"));
         }

         if (playerInventory.getBoots() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.boots"));
         } else {
         playerInventory.setBoots(restoreConfig.getItemStack("armor.boots"));
         }

         if (playerInventory.getItemInOffHand() != null){
         player.getWorld().dropItem(player.getLocation(), restoreConfig.getItemStack("armor.offhand"));
         } else {
         playerInventory.setItemInOffHand(restoreConfig.getItemStack("armor.offhand"));
         }
         }*/
    }

    public ArmorStand createHologram(Location location, List<String> lines, String fileName, JavaPlugin plugin) {
        File deathFile = new File(fileName);

        if (!deathFile.exists()) {
            return null;
        }

        YamlConfiguration deathConfig = YamlConfiguration.loadConfiguration(deathFile);

        double yOffset = 0.0;
        int lineNumber = 0;
        location.add(0, 0.5, 0);

        for (String line : lines) {
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, yOffset, 0), EntityType.ARMOR_STAND);

            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(line);
            hologram.setGravity(false);
            hologram.setInvulnerable(true);
            hologram.setSmall(true);

            hologram.setMetadata("deathchest_hologram", new FixedMetadataValue(plugin, false));

            deathConfig.set("hologram.lines." + lineNumber, hologram.getUniqueId().toString());

            lineNumber++;

            yOffset -= 0.25;
        }

        ArmorStand hologramTimer = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, yOffset, 0), EntityType.ARMOR_STAND);

        hologramTimer.setVisible(false);
        hologramTimer.setCustomNameVisible(true);
        hologramTimer.setCustomName("timer");
        hologramTimer.setGravity(false);
        hologramTimer.setInvulnerable(true);
        hologramTimer.setSmall(true);

        hologramTimer.setMetadata("deathchest_hologram", new FixedMetadataValue(plugin, false));

        deathConfig.set("hologram.timer", hologramTimer.getUniqueId().toString());

        try {
            deathConfig.save(deathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hologramTimer;
    }

    public void startDeathTimer(String fileName, ArmorStand armorStand, JavaPlugin plugin, Player player) {
        File chestFile = new File(fileName);
        YamlConfiguration chestConfig = YamlConfiguration.loadConfiguration(chestFile);
        int deathChestDuration = plugin.getConfig().getInt("death-chest.disappear", 30);
        Location location = armorStand.getLocation();

        int[] ticks = {deathChestDuration};

        // Primeiro, cancela qualquer tarefa existente para o jogador antes de criar uma nova
        UUID playerUUID = player.getUniqueId();
        if (playerHologram().containsKey(playerUUID)) {
            playerHologram().get(playerUUID).cancel();
            playerHologram().remove(playerUUID);
        }

        // Cria uma nova tarefa
        BukkitTask hologramTask = new BukkitRunnable() {
            @Override
            public void run() {
                int secondsLeft = ticks[0];
                int minutes = secondsLeft / 60;
                int seconds = secondsLeft % 60;

                String timeString = String.format("%02d:%02d", minutes, seconds);

                // Se o tempo for negativo ou o ArmorStand for nulo, cancela a tarefa
                if (timeString.contains("-") || armorStand == null || !armorStand.isValid()) {
                    if (armorStand != null) {
                        armorStand.setCustomName("§c--");
                    }
                    this.cancel();
                    return;
                }

                if (!plugin.isEnabled()) {
                    armorStand.setCustomName("§c--");
                    this.cancel();
                    return;
                }

                // Atualiza o nome do ArmorStand com o tempo restante
                armorStand.setCustomName("§b" + timeString);
                ParticlesUtils.playQuadrilexEffect(armorStand.getLocation());
                startCountAction(playerUUID, timeString);
                ticks[0]--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        // Adiciona a nova tarefa ao mapa
        playerHologram.put(player.getUniqueId(), hologramTask);
    }

    public void removeDeathChest(String fileName, UUID playerUUID) {
        File deathFile = new File(fileName);

        if (!deathFile.exists()) {
            System.out.println("Arquivo do bau da morte" + fileName + "não encontrado");
            return;
        }

        YamlConfiguration deathConfig = YamlConfiguration.loadConfiguration(deathFile);

        cancelTasks(playerUUID);
        try {
            removeDeathCount(Bukkit.getOfflinePlayer(playerUUID).getPlayer());
        } catch (Exception e) {

        }

        //Marca o baú como ja aberto ou removido
        if (deathConfig.getBoolean("info.active")) {
            deathConfig.set("info.active", false);
        }

        // Remover hologramas
        if (deathConfig.get("hologram") != null) {
            // Remover linhas do holograma
            if (deathConfig.getConfigurationSection("hologram.lines") != null) {
                for (String hologramKey : deathConfig.getConfigurationSection("hologram.lines").getKeys(false)) {
                    String hologramUUID = deathConfig.getString("hologram.lines." + hologramKey);
                    if (hologramUUID != null) {
                        UUID uuid = UUID.fromString(hologramUUID);
                        removeArmorStandByUUID(uuid);
                    }
                }
            }

            // Remover timer do holograma
            String timerUUID = deathConfig.getString("hologram.timer");
            if (timerUUID != null) {
                UUID uuid = UUID.fromString(timerUUID);
                removeArmorStandByUUID(uuid);
            }
        }

        // Remover entidade do baú
        String chestUUID = deathConfig.getString("chestentity.uuid");
        if (chestUUID != null) {
            UUID uuid = UUID.fromString(chestUUID);
            removeArmorStandByUUID(uuid);
        }

        String chestUUID2 = deathConfig.getString("chestentity2.uuid");
        if (chestUUID2 != null) {
            UUID uuid = UUID.fromString(chestUUID2);
            removeArmorStandByUUID(uuid);
        }

        try {
            deathConfig.save(deathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Restalrar os blocos para os antigos
        restoreCemeteryGround(deathFile);
    }

    public void cancelTasks(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        Player player = offlinePlayer.getPlayer();


        if (playerHologram.containsKey(playerUUID)) {
            BukkitTask task = playerHologram.get(playerUUID);
            if (task != null) {
                task.cancel();
            }
            playerHologram.remove(playerUUID); // Remoção apenas pela chave
        }

        if (chestTaskLaterRemove.containsKey(playerUUID)) {
            BukkitTask task = chestTaskLaterRemove.get(playerUUID);
            if (task != null) {
                task.cancel();
            }
            chestTaskLaterRemove.remove(playerUUID);
        }

        try {
            removeDeathCount(player);
            chestTaskLaterRemove.remove(playerUUID);
            playerHologram.remove(playerUUID);
            actionbarTasks.remove(playerUUID);
        } catch (Exception e) {

        }
    }

    private void removeArmorStandByUUID(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
                if (entity.getUniqueId().equals(uuid)) {
                    entity.remove();
                    return; // Encerra a busca após encontrar e remover o ArmorStand
                }
            }
        }
    }

    public final List<String> allowedWorlds(JavaPlugin plugin) {
        return plugin.getConfig().getStringList("death-chest.blackListWorlds");
    }

    public void startCountAction(UUID playerUUID, String timeString) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        if (playerHologram.containsKey(playerUUID)) {
            if (Allianceutils.getInstance().dispatcher.isUseRedis()) {

                if (isPlaySound) {
                    Allianceutils.getInstance().dispatcher.getRedisManager().sendSound("alc:async", player, getSound);
                }
                Allianceutils.getInstance().dispatcher.getRedisManager().sendActionBar("alc:async", player, UnicodeFontReplace.allianceFontReplace("§cTempo restante para o baú desaparecer: §e§n" + timeString));
            } else if (!Allianceutils.getInstance().dispatcher.isUseRedis()) {

                if (isPlaySound){
                    Allianceutils.getInstance().dispatcher.getPluginChannelManager().sendSound(player, getSound);
                }

                Allianceutils.getInstance().dispatcher.getPluginChannelManager().sendActionBar(player, UnicodeFontReplace.allianceFontReplace("§cTempo restante para o baú desaparecer: §e§n" + timeString));
            } else if (player.getPlayer() != null && player.getPlayer().isOnline()){

                if (isPlaySound){
                    player.getPlayer().playSound(player.getLocation(), getSound, 1.0F, 1.0F);
                }

                player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(UnicodeFontReplace.allianceFontReplace("§cTempo restante para o baú desaparecer: §e§n" + timeString)));
            }
        }
    }

    // Metodo que recebe o caminho do diretório e retorna uma lista de arquivos YML
    public List<File> scanFiles(String directoryPath) {
        // Cria uma lista para armazenar os arquivos YML
        List<File> ymlFiles = new ArrayList<>();

        // Cria um objeto File a partir do caminho fornecido
        File directory = new File(directoryPath);

        // Verifica se o diretório existe e se é realmente um diretório
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Diretório não encontrado ou não é um diretório.");
            return ymlFiles;
        }

        // Lista todos os arquivos YML no diretório
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });

        // Verifica se há arquivos YML no diretório e os adiciona na lista
        if (files != null) {
            for (File file : files) {
                ymlFiles.add(file);
            }
        }

        return ymlFiles;
    }

    public String getDeathChestTitle() {
        return GradientMessage.createGradientMessageAsString(plugin.getConfig().getString("gui.cheststitle", "§aBau da Morte"));
    }

    public void displayAnimation(Location location) {
        double range = 1; // Alcance em torno do jogador (2 blocos de distância)
        int particleCount = 5; // Quantidade de partículas geradas a cada tick

        for (int i = 0; i < particleCount; i++) {
            // Gerar um deslocamento aleatório dentro do alcance
            double offsetX = (random.nextDouble() * 2 - 1) * range;
            double offsetY = (random.nextDouble() * 2 - 1) * range;
            double offsetZ = (random.nextDouble() * 2 - 1) * range;

            // Gerar as partículas (efetivamente "flutuando")
            if (random.nextDouble() < 0.5) {
                if (location.getBlock().getType().isAir()) {
                    location.getWorld().spawnParticle(
                            Particle.SOUL,
                            location.add(offsetX, offsetY, offsetZ), // +1.5 para ajustar a altura
                            1,
                            0, 0, 0, 0
                    );
                }
            }
        }
    }

    public void stopCountAction(UUID player) {
        if (actionbarTasks.containsKey(player)) {
            actionbarTasks.get(player).cancel();
            actionbarTasks.remove(player);
        }
    }

    public void removeEntityByUUIDString(String uuid){
        if (uuid == null) return;
        if (uuid.isEmpty()) return;
        UUID entityUUID = UUID.fromString(uuid);
        removeArmorStandByUUID(entityUUID);
    }

    @Override
    public void createCemeteryGround(Location center, int radius, Player player, FileConfiguration deathConfig) {
        super.createCemeteryGround(center, radius, player, deathConfig);
    }

    @Override
    public void restoreCemeteryGround(File deathFile) {
        super.restoreCemeteryGround(deathFile);
    }


    public boolean isBelow1_20_5(Player player) {
        int clientProtocol = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
        return clientProtocol < 765; // 765 é o protocolo da 1.20.5 e 1.20.6
    }
}
