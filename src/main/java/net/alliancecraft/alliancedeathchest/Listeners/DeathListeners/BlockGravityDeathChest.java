package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import static net.alliancecraft.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;

public class BlockGravityDeathChest extends DeathConfigs implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFall(EntityChangeBlockEvent event) {
        // Verifica se a entidade é um FallingBlock
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            Block blockBelow = event.getBlock().getRelative(0, -1, 0);

            // Verifica se há um ArmorStand com metadado "deathchest_hologram" no bloco abaixo
            boolean isDeathChestHologram = blockBelow.getWorld().getNearbyEntities(
                    blockBelow.getLocation().add(0.5, 0, 0.5), // Ponto central do bloco abaixo
                    0.5, // Raio de verificação em X
                    1,   // Raio de verificação em Y
                    0.5  // Raio de verificação em Z
            ).stream().anyMatch(entity -> entity instanceof ArmorStand && entity.hasMetadata("deathchest_hologram") || entity.hasMetadata("alliancedeathchest"));

            // Cancela a mudança de bloco se houver um ArmorStand abaixo
            if (isDeathChestHologram) {
                Bukkit.broadcast(sendDebugMessage("§eCancelado a queda de falling block, para §b"+ (int) blockBelow.getLocation().getX() +", "+ (int) blockBelow.getLocation().getY()) + ", "+ (int) blockBelow.getLocation().getZ(), "alc.admin");
                event.setCancelled(true);
            }
        }
    }
}
