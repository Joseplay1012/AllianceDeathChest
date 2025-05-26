package net.alliancecraft.alliancedeathchest.Statics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class SkullApi {

    public static ItemStack getSkull(String displayname, List<String> lore, String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        if (url != null && !url.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            try {
                URL skinUrl = new URL(url);
                textures.setSkin(skinUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return skull;
            }
            profile.setTextures(textures);

            skullMeta.setOwnerProfile(profile);

            skullMeta.setDisplayName(displayname);
            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }
}