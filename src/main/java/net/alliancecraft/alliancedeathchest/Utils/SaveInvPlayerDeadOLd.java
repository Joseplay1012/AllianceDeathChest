package net.alliancecraft.alliancedeathchest.Utils; /**
 * package net.alliancecraft.allianceutils.Utils;
 *
 * import net.md_5.bungee.api.ChatMessageType;
 * import net.md_5.bungee.api.chat.TextComponent;
 * import org.bukkit.*;
 * import org.bukkit.block.Block;
 * import org.bukkit.block.Chest;
 * import org.bukkit.entity.ArmorStand;
 * import org.bukkit.entity.Entity;
 * import org.bukkit.entity.EntityType;
 * import org.bukkit.entity.Player;
 * import org.bukkit.event.EventHandler;
 * import org.bukkit.event.EventPriority;
 * import org.bukkit.event.Listener;
 * import org.bukkit.event.entity.PlayerDeathEvent;
 * import org.bukkit.inventory.ItemStack;
 * import org.bukkit.inventory.PlayerInventory;
 * import org.bukkit.persistence.PersistentDataType;
 * import org.bukkit.plugin.java.JavaPlugin;
 * import org.bukkit.scheduler.BukkitRunnable;
 * import org.bukkit.scheduler.BukkitTask;
 *
 * import java.text.DecimalFormat;
 * import java.util.*;
 *
 * import static net.alliancecraft.allianceutils.Listeners.deathPlayerSave.ConfigDeahChest.KEY_CHEST;
 * import static net.alliancecraft.allianceutils.Listeners.deathPlayerSave.ConfigDeahChest.sendDeathMessages;
 * import static net.alliancecraft.allianceutils.Utils.ParticlesUtils.playQuadrilexEffect;
 * import static net.alliancecraft.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;
 *
 * public class SaveInvPlayerDeadOLd implements Listener {
 *
 *     private final JavaPlugin plugin;
 *     private final Map<Location, ArmorStand> holograms;
 *     public static Map<Player, Map<Integer, ItemStack>> playerItemMap;
 *     public static Map<Player, Map<Integer, Location>> chestLocationSave;
 *     public static Map<Location, String> chesprivate = new HashMap<>();
 *     private Map<Player, ArmorStand> armorStandMap = new HashMap<>();
 *     private final List<String> allowedWorlds;
 *     public static Map<UUID, Map<Integer, ItemStack>> playerData = new HashMap<>();
 *     private static Map<Player, BukkitTask> actionbarTasks = new HashMap<>();  // Armazena as tarefas do contador para cada jogador
 *     public static BukkitTask stopHologram;
 *     public static BukkitTask stopParticles;
 *     public static Map<Player, Player> deathPlayerCount = new HashMap<>();
 *
 *     public SaveInvPlayerDeadOLd(JavaPlugin plugin) {
 *         this.plugin = plugin;
 *         playerItemMap = new HashMap<>();
 *         this.holograms = new HashMap<>();
 *         this.armorStandMap = new HashMap<>();
 *         chestLocationSave = new HashMap<>();
 *         this.allowedWorlds = plugin.getConfig().getStringList("death-chest.blackListWorlds");
 *     }
 *
 *     @EventHandler(priority = EventPriority.MONITOR)
 *     public void onPlayerDead(PlayerDeathEvent event) {
 *         int deathChestDuration = plugin.getConfig().getInt("death-chest.disappear", 30);
 *         final int[] ticks = {deathChestDuration * 20};
 *         Player player = event.getEntity();
 *         World world = event.getEntity().getWorld();
 *         List<ItemStack> drops = event.getDrops();
 *         List<String> lore1 = new ArrayList<>();
 *
 *         Map<Integer, ItemStack> items = new HashMap<>();
 *         PlayerInventory inventory = player.getInventory();
 *         for (int i = 0; i < inventory.getSize(); i++) {
 *             ItemStack item = inventory.getItem(i);
 *             if (item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
 *                 items.put(i, item.clone());
 *             }
 *         }
 *
 *         ItemStack helmet = inventory.getHelmet();
 *         if (helmet != null && helmet.getType() != Material.AIR) {
 *             items.put(1001, helmet.clone());
 *         }
 *
 *         ItemStack chestplate = inventory.getChestplate();
 *         if (chestplate != null && chestplate.getType() != Material.AIR) {
 *             items.put(1002, chestplate.clone());
 *         }
 *
 *         ItemStack leggings = inventory.getLeggings();
 *         if (leggings != null && leggings.getType() != Material.AIR) {
 *             items.put(1003, leggings.clone());
 *         }
 *
 *         ItemStack boots = inventory.getBoots();
 *         if (boots != null && boots.getType() != Material.AIR) {
 *             items.put(1004, boots.clone());
 *         }
 *
 *         ItemStack offHand = inventory.getItemInOffHand();
 *         if (offHand != null && offHand.getType() != Material.AIR) {
 *             items.put(1005, offHand.clone());
 *         }
 *
 *         if (allowedWorlds.contains(world.getName())) {
 *             List<String> messages = plugin.getConfig().getStringList("death-chest.notAllowedItems");
 *             for (String message : messages) {
 *                 player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
 *             }
 *             return;
 *         }
 *
 *         if(deathPlayerCount.containsKey(player)){
 *             player.sendMessage(allianceFontReplace("§cVoce ja tem um bau da morte."));
 *             deathPlayerCount.remove(player);
 *             return;
 *         }
 *
 *         if (items.isEmpty()) {
 *             return;
 *         }
 *         drops.clear();
 *         List<String> lore = new ArrayList<>();
 *         playerItemMap.put(player, items);
 *         playerData.put(player.getUniqueId(), playerItemMap.get(player));
 *         deathPlayerCount.put(player, player);
 *
 *         Block block = player.getLocation().getBlock();
 *         if (!block.getType().isSolid()) {
 *             block = block.getRelative(0, 0, 0);
 *         }
 *         block.setType(Material.CHEST);
 *         Chest chest = (Chest) block.getState();
 *         chest.setCustomName(allianceFontReplace("§eBaú da Morte de §b" + player.getName()));
 *         chest.getPersistentDataContainer().set(KEY_CHEST, PersistentDataType.STRING, player.getName());
 *         chest.update();
 *
 *         Location chestLocation = chest.getLocation();
 *         Map<Integer, Location> chestLocations = new HashMap<>();
 *         chestLocations.put(0, chestLocation);
 *         chestLocationSave.put(player, chestLocations);
 *         String locat = chestLocation.getX() + ", " + chestLocation.getY() + ", " + chestLocation.getZ();
 *         DecimalFormat format = new DecimalFormat("#.##");
 *         sendDeathMessages(player, locat, plugin);
 *         createHologram(chestLocation.add(0.5, 0.5, 0.5), new String[]{"§eBaú da Morte.", allianceFontReplace("§eBaú da Morte de §b" + player.getName())}, deathChestDuration);
 *
 *         Bukkit.getScheduler().runTaskLater(plugin, () -> {
 *             if (chest != null && chest.getBlock() != null && chest.getBlock().getType() == Material.CHEST) {
 *                 chest.getBlock().setType(Material.AIR);
 *                 List<Entity> nearbyEntities = (List) chestLocation.getWorld().getNearbyEntities(chestLocation.clone().add(0.5, 1.5, 0.5), 5.0, 5.0, 5.0);
 *                 Iterator var6 = nearbyEntities.iterator();
 *                 while (var6.hasNext()) {
 *                     Entity entity = (Entity) var6.next();
 *                     if (entity instanceof ArmorStand) {
 *                         entity.remove();
 *                     }
 *                 }
 *                 playerItemMap.remove(player);
 *                 stopParticles.cancel();
 *             }
 *         }, ticks[0]);
 *
 *         BukkitTask actionbarTask = new BukkitRunnable() {
 *             int secondsLeft = deathChestDuration;
 *
 *             @Override
 *             public void run() {
 *                 if (secondsLeft <= 0) {
 *                     this.cancel();
 *                     return;
 *                 }
 *                 int minutes = secondsLeft / 60;
 *                 int seconds = secondsLeft % 60;
 *                 String timeString = String.format("%02d:%02d", minutes, seconds);
 *                 player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(allianceFontReplace("§cTempo restante para o baú desaparecer: §e§n" + timeString)));
 *                 secondsLeft--;
 *             }
 *         }.runTaskTimer(plugin, 0L, 20L);
 *
 *         actionbarTasks.put(player, actionbarTask);  // Armazena a tarefa para permitir cancelamento posterior
 *     }
 *
 *     public static void stopCountAction(Player player) {
 *         if (actionbarTasks.containsKey(player)) {
 *             actionbarTasks.get(player).cancel();
 *             actionbarTasks.remove(player);
 *         }
 *     }
 *
 *     public static boolean hasCustomName(Chest chest, String namePrefix) {
 *         return chest.getCustomName() != null && chest.getCustomName().equalsIgnoreCase(namePrefix);
 *     }
 *
 *     private void createHologram(Location location, String[] lines, int durationInSeconds) {
 *         if (!holograms.containsKey(location)) {
 *             double yOffset = 0.0;
 *             for (String line : lines) {
 *                 ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, yOffset, 0), EntityType.ARMOR_STAND);
 *                 hologram.setVisible(false);
 *                 hologram.setCustomNameVisible(true);
 *                 hologram.setCustomName(line);
 *                 hologram.setGravity(false);
 *                 hologram.setInvulnerable(true);
 *                 hologram.setSmall(true);
 *
 *                 holograms.put(location.clone().add(0, yOffset, 0), hologram);
 *
 *                 yOffset -= 0.25;
 *             }
 *         }
 *
 *         int[] ticks = {durationInSeconds};
 *
 *         stopHologram = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
 *             int secondsLeft = ticks[0];
 *             int minutes = secondsLeft / 60;
 *             int seconds = secondsLeft % 60;
 *
 *             String timeString = String.format("%02d:%02d", minutes, seconds);
 *
 *             holograms.get(location).setCustomName(timeString);
 *
 *             ticks[0]--;
 *         }, 0L, 20L);
 *
 *         stopParticles = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
 *             playQuadrilexEffect(location);
 *         }, 0L, 0L);
 *     }
 * }
 */