package net.alliancecraft.alliancedeathchest.Listeners.DeathListeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class DamageListener extends DeathConfigs implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathChestDamage(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();

        if (entity instanceof ArmorStand armorStand){
            if (armorStand.hasMetadata(graveMetaData)){
                event.setCancelled(true);
            } else if (armorStand.getPersistentDataContainer().has(graveNameSpaceKey, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathChestDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();

        if (entity instanceof ArmorStand armorStand){
            if (armorStand.hasMetadata(graveMetaData)){
                event.setCancelled(true);
            } else if (armorStand.getPersistentDataContainer().has(graveNameSpaceKey, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathChestDamage(EntityDamageByBlockEvent event){
        Entity entity = event.getEntity();

        if (entity instanceof ArmorStand armorStand){
            if (armorStand.hasMetadata(graveMetaData)){
                event.setCancelled(true);
            } else if (armorStand.getPersistentDataContainer().has(graveNameSpaceKey, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }
}
