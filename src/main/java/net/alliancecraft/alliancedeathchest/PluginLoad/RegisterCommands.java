package net.alliancecraft.alliancedeathchest.PluginLoad;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Commads.deathchest.DeathChestCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterCommands {
    private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public static void registerCommands(){
        try {
            plugin.getCommand("bauver").setExecutor(new DeathChestCommand());
            Bukkit.getLogger().info("\u001B[36mComandos Registrados!");
        } catch (Exception e){
            Bukkit.getLogger().severe("Erro ao Registrar o Evento " + e.getMessage());
        }
    }
}
