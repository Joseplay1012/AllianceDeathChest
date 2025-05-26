package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginDesabledListener extends DeathConfigs implements Listener {
    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDesabled(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            // Usa o caminho como File para compatibilidade
            File dataDirectory = new File(plugin.getDataFolder(), "data");
            File[] deathChestFilesDir = dataDirectory.listFiles(File::isDirectory);

            assert deathChestFilesDir != null;
            for (File dirAll : deathChestFilesDir) {


                List<File> deathChestFiles = new ArrayList<>();
                try {
                    deathChestFiles = scanFiles(dirAll.getPath());
                } catch (Exception e) {

                }

                // Para cada arquivo de configuração, realiza as operações desejadas
                for (File deathFile : deathChestFiles) {
                    YamlConfiguration deathChestConfig = YamlConfiguration.loadConfiguration(deathFile);
                    String timeArmorStandUUID = deathChestConfig.getString("hologram.timer");

                    if (timeArmorStandUUID == null) continue;

                    UUID hologramUUID;
                    try {
                        hologramUUID = UUID.fromString(timeArmorStandUUID);
                    } catch (IllegalArgumentException e) {
                        // Se a string não for um UUID válido, ignore este arquivo
                        continue;
                    }

                    // Agora busca por todos os ArmorStands em todos os mundos
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
                            if (entity.getUniqueId().equals(hologramUUID)) {
                                entity.setCustomName("§c--"); // Modifica o nome do ArmorStand
                            }
                        }
                    }
                }
            }
        }
    }
}