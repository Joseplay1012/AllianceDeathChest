package net.alliancecraft.alliancedeathchest.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UnicodeFontReplace {
    public static String ignorePlayerUuid(Player player, String messege){
        for(Player playerUuid : Bukkit.getServer().getOnlinePlayers()){
            if(playerUuid.getUniqueId().equals(player.getUniqueId())){
                String Uuid = String.valueOf(playerUuid.getUniqueId());
                return allianceFontReplace(messege.replace(Uuid, player.getName()));
            }
        }
        return "§aSem Message";
    }
    public static String allianceFontReplace(String message) {
        String message1 = message.replace("&", "§");
        String colorCodePattern = "§[0-9a-fk-or]";

        StringBuilder newMessage = new StringBuilder();

        String[] parts = message1.split("(?=" + colorCodePattern + ")|(?<=" + colorCodePattern + ")");

        for (String part : parts) {
            if (part.matches(colorCodePattern)) {
                newMessage.append(part);
            } else {
                part = part.replaceAll("(?i)a", "ᴀ")
                        .replaceAll("(?i)b", "ʙ")
                        .replaceAll("(?i)c", "ᴄ")
                        .replaceAll("(?i)d", "ᴅ")
                        .replaceAll("(?i)e", "ᴇ")
                        .replaceAll("(?i)f", "ꜰ")
                        .replaceAll("(?i)g", "ɢ")
                        .replaceAll("(?i)h", "ʜ")
                        .replaceAll("(?i)i", "ɪ")
                        .replaceAll("(?i)j", "ᴊ")
                        .replaceAll("(?i)k", "ᴋ")
                        .replaceAll("(?i)l", "ʟ")
                        .replaceAll("(?i)m", "ᴍ")
                        .replaceAll("(?i)n", "ɴ")
                        .replaceAll("(?i)o", "ᴏ")
                        .replaceAll("(?i)p", "ᴘ")
                        .replaceAll("(?i)q", "ǫ")
                        .replaceAll("(?i)r", "ʀ")
                        .replaceAll("(?i)s", "ꜱ")
                        .replaceAll("(?i)t", "ᴛ")
                        .replaceAll("(?i)u", "ᴜ")
                        .replaceAll("(?i)v", "ᴠ")
                        .replaceAll("(?i)w", "ᴡ")
                        .replaceAll("(?i)x", "x")
                        .replaceAll("(?i)y", "ʏ")
                        .replaceAll("(?i)z", "ᴢ");
                newMessage.append(part);
            }
        }

        return newMessage.toString();
    }
}
