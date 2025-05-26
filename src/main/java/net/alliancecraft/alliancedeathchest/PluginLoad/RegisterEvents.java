package net.alliancecraft.alliancedeathchest.PluginLoad;


import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterEvents {
    private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public static void registerEvents(){
        try {
            Bukkit.getLogger().info("\u001B[36mRegistrando Eventos.");
            plugin.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new PlayerInteractiListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new SaveData(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BlockGravityDeathChest(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new PistonExtendDeathChest(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new PluginDesabledListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new CommandListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new CemeteryListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new DamageListener(), plugin);
            Bukkit.getLogger().info("\u001B[36mEventos Registrados!");
        }catch (Exception e){
            Bukkit.getLogger().severe("Erro ao Registrar o Evento " + e.getMessage());
        }
    }
}
