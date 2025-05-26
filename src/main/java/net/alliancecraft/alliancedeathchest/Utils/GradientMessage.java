package net.alliancecraft.alliancedeathchest.Utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradientMessage {
    public static Component createGradientMessage(String message) {
        Pattern patternmm = Pattern.compile("<.*?>");
        Matcher matchermm = patternmm.matcher(message);
        Pattern patternl = Pattern.compile("&");
        Matcher matcherl = patternl.matcher(message);

        if (matchermm.find()) {
            MiniMessage mm = MiniMessage.miniMessage();
            return mm.deserialize(message);
        } else if (matcherl.find()) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        } else if(message.equals("")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize("");
        } else if(!message.startsWith("&") || !message.startsWith("§")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        } else {
            return LegacyComponentSerializer.legacyAmpersand().deserialize("[Alliance-Death-Chest]: &cNão foi posivel achar essa String.");
        }
    }

    public static void sendGradient(Player player, String message) {
        Audience target = (Audience) player;
        Component component = createGradientMessage(message);
        target.sendMessage(component);
    }
    public static String createGradientMessageAsString(String message) {
        Component component = createGradientMessage(message);
        return LegacyComponentSerializer.legacyAmpersand().serialize(component).replace("&","§");
    }
}