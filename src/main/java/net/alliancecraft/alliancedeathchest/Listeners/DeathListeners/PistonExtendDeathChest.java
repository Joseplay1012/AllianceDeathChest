package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import static net.alliancecraft.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;

public class PistonExtendDeathChest extends DeathConfigs implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        // Verifica cada bloco que o pistão está tentando empurrar, mas também verifica o bloco na frente do pistão
        for (Block block : event.getBlocks()) {
            Block blockInNewPosition = block.getRelative(event.getDirection());

            // Verifica se há um ArmorStand com metadado "deathchest_hologram" ou "alliancedeathchest" na nova posição do bloco
            boolean isDeathChestHologram = blockInNewPosition.getWorld().getNearbyEntities(
                    blockInNewPosition.getLocation().add(0.5, 0, 0.5), // Ponto central do bloco
                    0.5, // Raio de verificação em X
                    1,   // Raio de verificação em Y
                    0.5  // Raio de verificação em Z
            ).stream().anyMatch(entity -> entity instanceof ArmorStand &&
                    (entity.hasMetadata("deathchest_hologram") || entity.hasMetadata("alliancedeathchest")));

            // Cancela o evento se houver um ArmorStand na nova posição
            if (isDeathChestHologram) {
                Bukkit.broadcast(sendDebugMessage("§eCancelado a queda de falling block, para §b"+ (int) block.getLocation().getX() +", "+ (int) block.getLocation().getY()) + ", "+ (int) block.getLocation().getZ(), "alc.admin");
                event.setCancelled(true);
                return;
            }
        }

        // Verifica o bloco na frente do pistão se ele não estiver empurrando blocos
        if (event.getBlocks().isEmpty()) {
            Block blockInFront = event.getBlock().getRelative(event.getDirection());

            // Verifica se há um ArmorStand com metadado "deathchest_hologram" ou "alliancedeathchest" na frente do pistão
            boolean isDeathChestHologram = blockInFront.getWorld().getNearbyEntities(
                    blockInFront.getLocation().add(0.5, 0, 0.5), // Ponto central do bloco
                    0.5, // Raio de verificação em X
                    1,   // Raio de verificação em Y
                    0.5  // Raio de verificação em Z
            ).stream().anyMatch(entity -> entity instanceof ArmorStand &&
                    (entity.hasMetadata("deathchest_hologram") || entity.hasMetadata("alliancedeathchest")));

            // Cancela o evento se houver um ArmorStand na frente do pistão
            if (isDeathChestHologram) {
                Bukkit.broadcast(sendDebugMessage("§eCancelado a queda de falling block, para §b"+ (int) blockInFront.getLocation().getX() +", "+ (int) blockInFront.getLocation().getY()) + ", "+ (int) blockInFront.getLocation().getZ(), "alc.admin");
                event.setCancelled(true);
            }
        }
    }
}
