package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class CemeteryListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (player.hasPermission("alc.admin")) return;

        if (block.hasMetadata("alc_cemetery_block")){
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
            player.sendMessage(allianceFontReplace("§cvoce não pode quebrar blocos do bau da morte!"));
            event.setCancelled(true);
        }
    }
}
