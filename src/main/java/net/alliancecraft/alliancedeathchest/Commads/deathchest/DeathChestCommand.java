package net.alliancecraft.alliancedeathchest.Commads.deathchest;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import net.alliancecraft.alliancedeathchest.Listeners.DeathListeners.DeathConfigs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class DeathChestCommand extends DeathConfigs implements CommandExecutor {
    public JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);
    public CommandUtils commandUtils = new CommandUtils();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if (!player.hasPermission("alc.admin")) return false;

        if (strings.length > 0){
            String fileDir = plugin.getDataFolder() + "/data/" + strings[0];

            File testFile = new File(fileDir);

            if (!testFile.exists()){
                player.sendMessage(allianceFontReplace("§cnão existe esse baú da morte."));
                return false;
            }

            commandUtils.openPlayerFilesGUI(player, 0, plugin, fileDir);
        } else {
            commandUtils.openPlayersFilesGUI(player, 0, plugin);
        }

        return false;
    }
}
