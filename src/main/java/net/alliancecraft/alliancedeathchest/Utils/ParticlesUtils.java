package net.alliancecraft.alliancedeathchest.Utils;

import net.alliancecraft.alliancedeathchest.AllianceDeathChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ParticlesUtils {
    private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AllianceDeathChest.class);

    public static void spawnFoguete(Location location) {
        World world = location.getWorld();
        if (world != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Particle particle = Particle.valueOf(plugin.getConfig().getString("dev-tester").toUpperCase());
                world.spawnParticle(particle, location.clone().add(0, 0.5, 0), 50);
            }, 1L);
        }
    }

    public static void playChainEffect(Location location) {
        List<Location> particleLocations = new ArrayList<>();
        Particle particle = Particle.valueOf(plugin.getConfig().getString("dev-tester").toUpperCase());

        location = location.clone().add(0, 1, 0);

        for (double n = -0.2; n < 0.6; n += 0.8 / 20) {
            particleLocations.add(location.clone().add(1 - n, n - 1.1, 1 - n));
            particleLocations.add(location.clone().add(1 - n, n - 1.1, -1 + n));
            particleLocations.add(location.clone().add(-1 + n, n - 1.1, 1 - n));
            particleLocations.add(location.clone().add(-1 + n, n - 1.1, -1 + n));
        }
        for (Location loc : particleLocations) {
            location.getWorld().spawnParticle(particle, loc, 1);
        }
    }

    public static void playQuadrilexEffect(Location location) {
        Particle particle = Particle.valueOf(plugin.getConfig().getString("dev-tester").toUpperCase());

        int particles = 30;
        double radius = 1.2;

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;

            double x = Math.cos(angle) * radius;
            double y = 0;
            double z = Math.sin(angle) * radius;

            Location pLoc = location.clone().add(x, y, z);

            location.getWorld().spawnParticle(particle, pLoc, 1);
        }


        /*int orbs = 20; // Quantidade de partículas no quadrilátero
        int maxStepX = 100; // Número máximo de passos no eixo X
        int maxStepY = 100; // Número máximo de passos no eixo Y
        int stepX = 0; // Passo atual no eixo X
        int stepY = 0;

        for (int i = 0; i < orbs; i++) {
            double dx = -(Math.cos((stepX / (double) maxStepX) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * ((maxStepY - Math.abs(stepY)) / (double) maxStepY);
            double dy = (stepY / (double) maxStepY) * 1.5;
            double dz = -(Math.sin((stepX / (double) maxStepX) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * ((maxStepY - Math.abs(stepY)) / (double) maxStepY);
            particleLocations.add(location.clone().add(dx, dy, dz));
        }

        // Spawn partículas do tipo End em cada localização
        for (Location loc : particleLocations) {
            try{
                location.getWorld().spawnParticle(particle, loc, 1);
            }catch (Exception e){
                return;
            }
        }*/
    }
}
