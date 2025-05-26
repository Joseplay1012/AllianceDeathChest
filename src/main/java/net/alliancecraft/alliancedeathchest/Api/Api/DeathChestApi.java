package net.alliancecraft.alliancedeathchest.Api.Api;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.allianceutils.Utils.FullEmptyInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class DeathChestApi {
    private JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public int getActiveDeathChests(Player player){
        File dataDirectory = new File(plugin.getDataFolder() + "/data/" + player.getName());

        File[] files = dataDirectory.listFiles((dir, name) -> name.endsWith(".yml"));
        int chestActives = 0;

        if (files == null || files.length == 0) {
            return 0;
        }

        for (File file : files) {
            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
            boolean isChestActive = fileConfig.getBoolean("info.active", false);

            if (isChestActive){
                chestActives++;
            }
        }

        return chestActives;
    }

    public int getTotalDeathChests(Player player){
        File dataDirectory = new File(plugin.getDataFolder() + "/data/" + player.getName());

        File[] files = dataDirectory.listFiles((dir, name) -> name.endsWith(".yml"));
        int chestActives = files.length;

        if (files == null || files.length == 0) {
            return 0;
        }

        return chestActives;
    }
}
