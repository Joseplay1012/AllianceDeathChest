package net.alliancecraft.alliancedeathchest;

import net.alliancecraft.alliancedeathchest.PluginLoad.RegisterCommands;
import net.alliancecraft.alliancedeathchest.PluginLoad.RegisterEvents;
import net.alliancecraft.alliancedeathchest.PluginLoad.RegisterPlaceHolders;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllianceDeathChest extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        RegisterEvents.registerEvents();
        RegisterCommands.registerCommands();
        RegisterPlaceHolders.registerPlaceHolders();
        this.getLogger().info("\u001b[36mPlugin iniciado com sucesso! - \u001b[37mALLIANCE\u001b[33mCRAFT\u001b[0m");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getLogger().info("\u001b[24mPlugin Desligado com sucesso! - \u001b[37mALLIANCE\u001b[33mCRAFT\u001b[0m");
    }
}
