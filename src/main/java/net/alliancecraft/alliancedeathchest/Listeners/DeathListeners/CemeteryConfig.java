package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Utils.RegionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class CemeteryConfig {
    public JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);
    public RegionsUtils regionsUtils = new RegionsUtils();

    public void createCemeteryGround(Location center, int radius, Player player, FileConfiguration deathConfig) {
        World world = center.getWorld();
        Random random = new Random();
        List<Material> materials = getEnvironmentMaterials(world);

        // Criar a área central do cemitério
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                Location blockLocation = center.clone().add(x, 0, z);
                Block block = blockLocation.getBlock();

                if (distance <= radius) {
                    double chance = (radius - distance) / radius;

                    if (distance <= radius - 2) {
                        setRandomGroundMaterial(random, materials, block, world, player, deathConfig);
                    } else if (distance > radius - 2 && distance < radius) {
                        if (random.nextDouble() < chance) {
                            setRandomGroundMaterial(random, materials, block, world, player, deathConfig);
                        } else {
                            setEnvironmentSpecificMaterial(block, world, player, deathConfig);
                        }
                    }
                }
            }
        }

        radius = radius + 1;

        // Criar a borda do cemitério
        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                double distance = Math.sqrt(x * x + z * z);
                Location blockLocation = center.clone().add(x, 0, z);
                Block block = blockLocation.getBlock();

                if (distance > radius - 1 && distance <= radius + 1) {
                    if (random.nextDouble() < 0.4) {
                        setEnvironmentSpecificMaterial(block, world, player, deathConfig);
                    } else if (random.nextDouble() < 0.3) {
                        setAlternativeBorderMaterial(block, world, player, deathConfig);
                    }
                }
            }
        }
    }

    public List<Material> getEnvironmentMaterials(World world) {
        List<Material> materials = new ArrayList<>();
        switch (world.getEnvironment()) {
            case NORMAL:
                materials.addAll(Arrays.asList(Material.GRASS_BLOCK, Material.DIRT_PATH, Material.DIRT, Material.STONE, Material.MOSSY_COBBLESTONE, Material.CRACKED_STONE_BRICKS, Material.COARSE_DIRT, Material.PODZOL));
                break;
            case NETHER:
                materials.addAll(Arrays.asList(Material.NETHERRACK, Material.SOUL_SAND, Material.SOUL_SOIL, Material.BASALT, Material.BLACKSTONE, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, Material.MAGMA_BLOCK));
                break;
            case THE_END:
                materials.addAll(Arrays.asList(Material.END_STONE, Material.END_STONE_BRICKS, Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, Material.OBSIDIAN, Material.CHISELED_QUARTZ_BLOCK));
                break;
        }
        return materials;
    }

    public void saveBlockToConfig(FileConfiguration deathConfig, Location blockLocation, Block block) {
        String blockKey = blockLocation.getWorld().getName() + "_" + blockLocation.getBlockX() + "_" + blockLocation.getBlockY() + "_" + blockLocation.getBlockZ();
        deathConfig.set("cemetery." + blockKey + ".material", block.getType().toString());
        deathConfig.set("cemetery." + blockKey + ".world", blockLocation.getWorld().getName());
        deathConfig.set("cemetery." + blockKey + ".data", block.getBlockData().getAsString()); // Salva o BlockData como String
    }

    public void handleContainerItems(Block block, FileConfiguration deathConfig) {
        if (block.getState() instanceof Container container) {
            ItemStack[] contents = container.getInventory().getContents();
            List<Map<String, Object>> items = new ArrayList<>();

            for (ItemStack item : contents) {
                if (item != null) {
                    items.add(item.serialize());
                }
            }
            String blockKey = block.getLocation().getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
            deathConfig.set("cemetery." + blockKey + ".items", items);
        }
    }

    public void setRandomGroundMaterial(Random random, List<Material> materials, Block block, World world, Player player, FileConfiguration deathConfig) {
        if (block.getType() != Material.AIR && block.getType() != Material.CHEST && block.getType() != Material.BEDROCK && regionsUtils.isValidLocation(block.getLocation(), player)) {
            saveBlockToConfig(deathConfig, block.getLocation(), block);
            handleContainerItems(block, deathConfig);
            block.setType(materials.get(random.nextInt(materials.size())));
            block.setMetadata("alc_cemetery_block", new FixedMetadataValue(plugin, false));
        }
    }

    public void setEnvironmentSpecificMaterial(Block block, World world, Player player, FileConfiguration deathConfig) {
        if (block.getType() != Material.AIR && block.getType() != Material.CHEST && block.getType() != Material.BEDROCK && regionsUtils.isValidLocation(block.getLocation(), player)) {
            saveBlockToConfig(deathConfig, block.getLocation(), block);
            handleContainerItems(block, deathConfig);
            switch (world.getEnvironment()) {
                case NORMAL:
                    block.setType(Material.CRACKED_STONE_BRICKS);
                    break;
                case NETHER:
                    block.setType(Material.NETHER_BRICKS);
                    break;
                case THE_END:
                    block.setType(Material.END_STONE_BRICKS);
                    break;
            }
            block.setMetadata("alc_cemetery_block", new FixedMetadataValue(plugin, false));
        }
    }

    public void setAlternativeBorderMaterial(Block block, World world, Player player, FileConfiguration deathConfig) {
        if (block.getType() != Material.AIR && block.getType() != Material.CHEST && block.getType() != Material.BEDROCK && regionsUtils.isValidLocation(block.getLocation(), player)) {
            saveBlockToConfig(deathConfig, block.getLocation(), block);
            handleContainerItems(block, deathConfig);
            switch (world.getEnvironment()) {
                case NORMAL:
                    block.setType(Material.MOSSY_COBBLESTONE);
                    break;
                case NETHER:
                    block.setType(Material.BLACKSTONE);
                    break;
                case THE_END:
                    block.setType(Material.PURPUR_BLOCK);
                    break;
            }
            block.setMetadata("alc_cemetery_block", new FixedMetadataValue(plugin, false));
        }
    }

    public void restoreCemeteryGround(File deathFile) {
        YamlConfiguration deathConfig = YamlConfiguration.loadConfiguration(deathFile);


        if (deathConfig.getConfigurationSection("cemetery") != null) {
            for (String key : deathConfig.getConfigurationSection("cemetery").getKeys(false)) {
                String path = "cemetery." + key;
                String worldName = deathConfig.getString(path + ".world");
                String materialName = deathConfig.getString(path + ".material");
                String blockDataString = deathConfig.getString(path + ".data"); // Recupera o BlockData como String
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) deathConfig.getList(path + ".items");

                // Restaura o estado do contêiner (caso haja)
                restoreContainerItems(itemList, worldName, key);

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    String[] parts = key.split("_");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);
                    Location blockLocation = new Location(world, x, y, z);
                    Block block = blockLocation.getBlock();
                    block.setType(Material.valueOf(materialName), true);
                    block.removeMetadata("alc_cemetery_block", plugin);
                    block.setBlockData(Bukkit.createBlockData(blockDataString)); // Restaura o BlockData
                }
            }
        }
    }

    public void restoreContainerItems(List<Map<String, Object>> itemList, String worldName, String blockKey) {
        if (itemList != null) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                String[] parts = blockKey.split("_");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                Block block = new Location(world, x, y, z).getBlock();
                if (block.getState() instanceof Container container) {
                    for (Map<String, Object> itemMap : itemList) {
                        ItemStack item = ItemStack.deserialize(itemMap);
                        container.getInventory().addItem(item);
                    }
                }
            }
        }
    }
}