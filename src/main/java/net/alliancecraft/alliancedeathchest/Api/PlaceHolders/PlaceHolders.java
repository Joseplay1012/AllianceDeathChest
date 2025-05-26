package net.alliancecraft.alliancedeathchest.Api.PlaceHolders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.alliancecraft.alliancedeathchest.Api.Api.DeathChestApi;
import net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.DeathConfigs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceHolders extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private DeathChestApi deathChestApi = new DeathChestApi();

    public PlaceHolders(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "audeathchest";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("actives")){
            return String.valueOf(deathChestApi.getActiveDeathChests(player));
        }

        if (params.equalsIgnoreCase("total")){
            return String.valueOf(deathChestApi.getTotalDeathChests(player));
        }

        return "";
    }
}
