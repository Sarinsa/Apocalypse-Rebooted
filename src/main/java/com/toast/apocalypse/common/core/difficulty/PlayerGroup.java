package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /** The horizontal position in the world where
     *  this group originates. Used when monsters
     *  spawn in and need to locate the nearest group.
     */
    private GroupPosition position;


    public PlayerGroup(@Nullable ServerPlayerEntity... playerEntities) {
        this.players = new ArrayList<>();

        if (playerEntities != null) {
            this.players.addAll(Arrays.asList(playerEntities));
        }
    }

    public void tick() {
        recalculateDifficulty();
    }

    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }

    public long getEffectiveDifficulty() {
        return this.effectiveDifficulty;
    }

    public GroupPosition getPosition() {
        return this.position;
    }

    public void recalculateDifficulty() {
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

    public static class GroupPosition {

        private double x;
        private double z;

        public GroupPosition(double x, double z) {
            this.x = x;
            this.z = z;
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }

        public void setPos(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }
}
