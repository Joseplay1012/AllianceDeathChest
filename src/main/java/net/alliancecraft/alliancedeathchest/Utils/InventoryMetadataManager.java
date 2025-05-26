package net.alliancecraft.alliancedeathchest.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryMetadataManager {
    private final File metadataFile;
    private final FileConfiguration metadataConfig;
    private final Map<Inventory, UUID> inventoryUUIDMap = new HashMap<>();

    public InventoryMetadataManager(JavaPlugin plugin) {
        // Caminho para o arquivo metadata.yml na pasta de dados do plugin
        metadataFile = new File(plugin.getDataFolder(), "metadata.yml");

        // Carrega o arquivo de configuração
        if (!metadataFile.exists()) {
            try {
                metadataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Carrega a configuração YAML
        metadataConfig = YamlConfiguration.loadConfiguration(metadataFile);
    }

    // Define um metadado para um inventário
    public void setMetadata(Inventory inventory, String metadata) {
        // Gera um UUID para o inventário se ainda não existir
        UUID inventoryUUID = inventoryUUIDMap.computeIfAbsent(inventory, inv -> UUID.randomUUID());

        // Salva o metadado no arquivo de configuração
        String inventoryKey = "inventories." + inventoryUUID.toString();
        metadataConfig.set(inventoryKey + ".metadata", metadata);

        // Salva as alterações no arquivo
        saveConfig();
    }

    // Obtém um metadado para um inventário
    public String getMetadata(Inventory inventory) {
        UUID inventoryUUID = inventoryUUIDMap.get(inventory);
        if (inventoryUUID == null) {
            return null;
        }

        String inventoryKey = "inventories." + inventoryUUID.toString();
        return metadataConfig.getString(inventoryKey + ".metadata", null);
    }

    // Verifica se um inventário possui um metadado
    public boolean hasMetadata(Inventory inventory) {
        UUID inventoryUUID = inventoryUUIDMap.get(inventory);
        if (inventoryUUID == null) {
            return false;
        }

        String inventoryKey = "inventories." + inventoryUUID.toString();
        return metadataConfig.contains(inventoryKey + ".metadata");
    }

    // Salva o arquivo de configuração
    private void saveConfig() {
        try {
            metadataConfig.save(metadataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
