package net.alliancecraft.alliancedeathchest.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.alliancecraft.allianceutils.api.claims.ClaimApi;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class RegionsUtils {
    public ClaimApi claimApi = new ClaimApi();

    public Boolean isRegionBreak(Location location) {
        return claimApi.isRegion(location);
    }

    public Boolean isCliamRegion(Location location) {
        return claimApi.isClaimRegion(location);
    }

    public Boolean isClaimOwner(Player player, Location location) {
        return claimApi.isPlayerClaimOwner(player, location);
    }

    public Boolean isClaimBuildAcess(Player player, Location location) {
        return claimApi.playerCanClaimBuild(player, location);
    }

    public boolean isValidLocation(Location location, Player player) {
        if (this.isRegionBreak(location)) {
            return false;
        } else if (this.isCliamRegion(location) && !this.isClaimOwner(player, location) && !this.isClaimBuildAcess(player, location)) {
            return false;
        } else {
            return true;
        }
    }
}
