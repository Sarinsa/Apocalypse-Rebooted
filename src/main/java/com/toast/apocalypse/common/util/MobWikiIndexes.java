package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.entity.living.*;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for keeping track of which
 * mob has which index in the mob wiki.
 */
public final class MobWikiIndexes {

    private static final Map<Class<? extends IFullMoonMob>, Integer> INDEXES = new HashMap<>();

    public static final int GHOST_INDEX = 0;
    public static final int BREECHER_INDEX = 1;
    public static final int GRUMP_INDEX = 2;
    public static final int SEEKER_INDEX = 3;
    public static final int DESTROYER_INDEX = 4;

    static {
        INDEXES.put(GhostEntity.class, GHOST_INDEX);
        INDEXES.put(BreecherEntity.class, BREECHER_INDEX);
        INDEXES.put(GrumpEntity.class, GRUMP_INDEX);
        INDEXES.put(SeekerEntity.class, SEEKER_INDEX);
        INDEXES.put(DestroyerEntity.class, DESTROYER_INDEX);
    }

    public static void init() {}

    /**
     * Returns the index associated with
     * the given mob class. Returns -1
     * if there is no match.
     */
    public static int getFromClass(Class<? extends IFullMoonMob> clazz) {
        if (INDEXES.containsKey(clazz)) {
            return INDEXES.get(clazz);
        }
        return -1;
    }

    /**
     * Adds the mob wiki index of the given entity class
     * to the given player's unlocked mob wiki indexes,
     * unless the player has already unlocked this index.
     *
     * @param player The player to unlock the index for.
     * @param clazz The class of the entity to get the index for.
     */
    public static void awardIndex(@Nonnull ServerPlayerEntity player, Class<? extends IFullMoonMob> clazz) {
        int[] unlockedIndexes = CapabilityHelper.getMobWikiIndexes(player);
        int awardedIndex = getFromClass(clazz);

        if (awardedIndex < 0)
            return;

        for (int i : unlockedIndexes) {
            if (i == awardedIndex)
                return;
        }
        CapabilityHelper.addMobWikiIndex(player, awardedIndex);
    }

    // Utility class, instantiating unnecessary
    private MobWikiIndexes() {}
}
