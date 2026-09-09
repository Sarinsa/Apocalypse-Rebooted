package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.config.value.PairListValueCodec;
import com.toast.apocalypse.common.event.GameEventListener;
import com.toast.apocalypse.common.util.DataStructureUtils;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IRandomKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

import java.util.ArrayList;
import java.util.List;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.MOB_BUFFING;

public final class MobPotionHandler {
    /**
     * Attempts to pick a potion/mob effect from the config and
     * apply to the given entity.<br>
     * Called from {@link GameEventListener#onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn)}
     *
     * @param livingEntity     The entity to find a potion effect for.
     * @param scaledDifficulty The difficulty (in days) of the nearest player.
     * @param fullMoon         True if it is both nighttime and a full moon in the level the entity is in.
     * @param random           The RNG of the level object the entity is in.
     */
    public static void handlePotions( LivingEntity livingEntity, double scaledDifficulty, boolean fullMoon, RandomSource random ) {
        if( MOB_BUFFING.POTION_EFFECTS.mobEffectBlacklist.contains( livingEntity ) ||
                MOB_BUFFING.POTION_EFFECTS.mobEffectTierList.isEmpty() ) return;
        
        final double diffMultiplier = scaledDifficulty / MOB_BUFFING.POTION_EFFECTS.mobEffectDifficultySpan.get();
        
        final double maxPotionChance = MOB_BUFFING.POTION_EFFECTS.mobEffectMaxChance.get();
        double bonus = MOB_BUFFING.POTION_EFFECTS.mobEffectChance.get() * diffMultiplier;
        if( maxPotionChance >= 0.0 && bonus > maxPotionChance ) {
            bonus = maxPotionChance;
        }
        if( fullMoon ) {
            bonus += MOB_BUFFING.POTION_EFFECTS.mobEffectLunarChance.get();
        }
        
        if( random.nextDouble() < bonus ) {
            List<PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer>> availableEffects = getAllEffectsFor( scaledDifficulty );
            PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer> effect = DataStructureUtils.getRandomListValue( random, availableEffects );
            if( effect != null && KeyUsage.POLL.allowsKey( effect.left() ) ) {
                //noinspection unchecked - We already verified polling is allowed
                MobEffect mobEffect = ((IRandomKey<MobEffect>) effect.left()).nextValue( random );
                if( mobEffect != null ) {
                    livingEntity.addEffect( new MobEffectInstance( mobEffect,
                            MobEffectInstance.INFINITE_DURATION, effect.right() ) );
                }
            }
        }
    }
    
    /** @return All effect-amplifier pairs active for the difficulty level. */
    private static List<PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer>> getAllEffectsFor( double scaledDifficulty ) {
        List<PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer>> list = new ArrayList<>();
        MOB_BUFFING.POTION_EFFECTS.mobEffectTierList.entries().forEach( entry -> {
            if( entry.matches( scaledDifficulty ) ) list.addAll( entry.get() );
        } );
        return list;
    }
}