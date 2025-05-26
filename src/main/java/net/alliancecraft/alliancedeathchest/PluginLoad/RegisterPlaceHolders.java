package net.alliancecraft.alliancedeathchest.PluginLoad;

import me.clip.placeholderapi.PlaceholderAPI;
import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Api.PlaceHolders.PlaceHolders;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterPlaceHolders {
    private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public static void registerPlaceHolders(){
        PlaceholderAPI.registerExpansion(new PlaceHolders(plugin));
    }
}
