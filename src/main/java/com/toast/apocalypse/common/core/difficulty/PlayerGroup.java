package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// Currently unused
/**
 * Used when determining the effective difficulty to work with
 * when mobs spawn into the world.
 */
public class PlayerGroup {

    /** Determines if the players in this group should use
     *  their collective difficulty's average over the
     *  player with the highest difficulty.
     */
    public static boolean USE_AVERAGE_DIFFICULTY;

    /** The players included in this group */
    private final List<ServerPlayerEntity> players;

    /** The effective difficulty used
     *  when monsters spawn.
     */
    private long effectiveDifficulty;

    /** The X and Z position of this group in the world */
    private double x;
    private double z;


    public PlayerGroup(@Nullable ServerPlayerEntity... playerEntities) {
        this.players = new ArrayList<>();

        if (playerEntities != null) {
            this.players.addAll(Arrays.asList(playerEntities));
        }
    }

    public void tick() {
        recalculatePosition();
        recalculateDifficulty();
    }

    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }

    public long getEffectiveDifficulty() {
        return this.effectiveDifficulty;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    /** Only takes horizontal distance into account. */
    public double distanceTo(Entity entity) {
        double x = this.getX() - entity.getX();
        double z = this.getZ() - entity.getZ();
        return MathHelper.sqrt(x * x + z * z);
    }

    private void recalculatePosition() {

    }

    private void recalculateDifficulty() {
        long difficulty = 0;

        if (USE_AVERAGE_DIFFICULTY) {
            for (PlayerEntity player : this.players) {
                difficulty += CapabilityHelper.getPlayerDifficulty(player);
            }
            this.effectiveDifficulty = difficulty / this.players.size();
        }
        else {
            for (PlayerEntity playerEntity : this.players) {
                difficulty = Math.max(CapabilityHelper.getPlayerDifficulty(playerEntity), difficulty);
            }
            this.effectiveDifficulty = difficulty;
        }
    }
}
