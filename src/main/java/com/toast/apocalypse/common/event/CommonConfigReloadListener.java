package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.MobDifficultyHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerGroup;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.mod_event.events.FullMoonEvent;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.misc.DestroyerExplosionContext;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import static com.toast.apocalypse.common.core.config.ApocalypseCommonConfig.COMMON;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfigReloadListener {

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            updateInfo();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfig.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            updateInfo();
        }
    }

    /**
     * Updates static references when needed to
     * avoid accessing the config constantly
     * in the server tick loop and whatnot.
     *
     * The config is pretty large, so config reloads
     * might make the game lag for a sec, I dunno.
     */
    public static void updateInfo() {
        PlayerDifficultyManager.MULTIPLAYER_DIFFICULTY_SCALING = COMMON.multiplayerDifficultyScaling();
        PlayerDifficultyManager.MULTIPLAYER_DIFFICULTY_MULT = COMMON.getMultiplayerDifficultyRateMult();
        PlayerDifficultyManager.SLEEP_PENALTY = COMMON.getSleepPenalty();

        List<RegistryKey<World>> list = new ArrayList<>();
        COMMON.getDifficultyPenaltyDimensions().forEach((s -> {
            list.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s)));
        }));
        PlayerDifficultyManager.DIMENSION_PENALTY_LIST = list;

        PlayerDifficultyManager.DIMENSION_PENALTY = COMMON.getDimensionPenalty();
        PlayerGroup.USE_AVERAGE_DIFFICULTY = COMMON.getAverageGroupDifficulty();

        RainDamageTickHelper.RAIN_TICK_RATE = COMMON.getRainTickRate();
        RainDamageTickHelper.RAIN_DAMAGE = COMMON.getRainDamage();

        MobDifficultyHandler.MOBS_ONLY = COMMON.getMobsOnly();

        refreshList(COMMON.getDestroyerProofBlocks(), DestroyerExplosionContext.DESTROYER_PROOF_BLOCKS, ForgeRegistries.BLOCKS);
        refreshList(COMMON.getHealthBlacklist(), MobDifficultyHandler.HEALTH_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getSpeedBlacklist(), MobDifficultyHandler.SPEED_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getDamageBlacklist(), MobDifficultyHandler.DAMAGE_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getKnockbackResBlacklist(), MobDifficultyHandler.KNOCKBACK_BLACKLIST, ForgeRegistries.ENTITIES);

        MobDifficultyHandler.HEALTH_TIME_SPAN = COMMON.getHealthTimeSpan();
        MobDifficultyHandler.HEALTH_FLAT_BONUS = COMMON.getHealthFlatBonus();
        MobDifficultyHandler.HEALTH_MULT_BONUS = COMMON.getHealthMultBonus();
        MobDifficultyHandler.HEALTH_FLAT_BONUS_MAX = COMMON.getHealthFlatBonusMax();
        MobDifficultyHandler.HEALTH_MULT_BONUS_MAX = COMMON.getHealthMultBonusMax();
        MobDifficultyHandler.HEALTH_LUNAR_FLAT_BONUS = COMMON.getHealthLunarFlatBonus();
        MobDifficultyHandler.HEALTH_LUNAR_MULT_BONUS = COMMON.getHealthLunarMultBonus();

        MobDifficultyHandler.SPEED_TIME_SPAN = COMMON.getSpeedTimeSpan();
        MobDifficultyHandler.SPEED_MULT_BONUS = COMMON.getSpeedMultBonus();
        MobDifficultyHandler.SPEED_MULT_BONUS_MAX = COMMON.getSpeedMultBonusMax();
        MobDifficultyHandler.SPEED_LUNAR_MULT_BONUS = COMMON.getSpeedLunarMultBonus();

        MobDifficultyHandler.DAMAGE_TIME_SPAN = COMMON.getDamageTimeSpan();
        MobDifficultyHandler.DAMAGE_FLAT_BONUS = COMMON.getDamageFlatBonus();
        MobDifficultyHandler.DAMAGE_MULT_BONUS = COMMON.getDamageMultBonus();
        MobDifficultyHandler.DAMAGE_FLAT_BONUS_MAX = COMMON.getDamageFlatBonusMax();
        MobDifficultyHandler.DAMAGE_MULT_BONUS_MAX = COMMON.getDamageMultBonusMax();
        MobDifficultyHandler.DAMAGE_LUNAR_FLAT_BONUS = COMMON.getDamageLunarFlatBonus();
        MobDifficultyHandler.DAMAGE_LUNAR_MULT_BONUS = COMMON.getDamageLunarMultBonus();

        MobDifficultyHandler.KNOCKBACK_RES_TIME_SPAN = COMMON.getKnockbackResTimeSpan();
        MobDifficultyHandler.KNOCKBACK_RES_FLAT_BONUS = COMMON.getKnockbackResFlatBonus();
        MobDifficultyHandler.KNOCKBACK_RES_FLAT_BONUS_MAX = COMMON.getKnockbackResFlatBonusMax();
        MobDifficultyHandler.KNOCKBACK_RES_LUNAR_FLAT_BONUS = COMMON.getKnockbackResLunarFlatBonus();

        FullMoonEvent.GHOST_SPAWN_WEIGHT = COMMON.getFullMoonMobSpawnWeight(GhostEntity.class);
        FullMoonEvent.BREECHER_SPAWN_WEIGHT = COMMON.getFullMoonMobSpawnWeight(BreecherEntity.class);
        FullMoonEvent.GRUMP_SPAWN_WEIGHT = COMMON.getFullMoonMobSpawnWeight(GrumpEntity.class);
        FullMoonEvent.SEEKER_SPAWN_WEIGHT = COMMON.getFullMoonMobSpawnWeight(SeekerEntity.class);
        FullMoonEvent.DESTROYER_SPAWN_WEIGHT = COMMON.getFullMoonMobSpawnWeight(DestroyerEntity.class);

        FullMoonEvent.MOB_COUNT_TIME_SPAN = COMMON.getDifficultyUntilNextIncrease();

        FullMoonEvent.GHOST_MIN_COUNT = COMMON.getMoonMobMinCount(GhostEntity.class);
        FullMoonEvent.BREECHER_MIN_COUNT = COMMON.getMoonMobMinCount(BreecherEntity.class);
        FullMoonEvent.GRUMP_MIN_COUNT = COMMON.getMoonMobMinCount(GrumpEntity.class);
        FullMoonEvent.SEEKER_MIN_COUNT = COMMON.getMoonMobMinCount(SeekerEntity.class);
        FullMoonEvent.DESTROYER_MIN_COUNT = COMMON.getMoonMobMinCount(DestroyerEntity.class);

        FullMoonEvent.GHOST_ADDITIONAL_COUNT = COMMON.getMoonMobAdditionalCount(GhostEntity.class);
        FullMoonEvent.BREECHER_ADDITIONAL_COUNT = COMMON.getMoonMobAdditionalCount(BreecherEntity.class);
        FullMoonEvent.GRUMP_ADDITIONAL_COUNT = COMMON.getMoonMobAdditionalCount(GrumpEntity.class);
        FullMoonEvent.SEEKER_ADDITIONAL_COUNT = COMMON.getMoonMobAdditionalCount(SeekerEntity.class);
        FullMoonEvent.DESTROYER_ADDITIONAL_COUNT = COMMON.getMoonMobAdditionalCount(DestroyerEntity.class);

        FullMoonEvent.GHOST_START = COMMON.getMoonMobStartDifficulty(GhostEntity.class);
        FullMoonEvent.BREECHER_START = COMMON.getMoonMobStartDifficulty(BreecherEntity.class);
        FullMoonEvent.GRUMP_START = COMMON.getMoonMobStartDifficulty(GrumpEntity.class);
        FullMoonEvent.SEEKER_START = COMMON.getMoonMobStartDifficulty(SeekerEntity.class);
        FullMoonEvent.DESTROYER_START = COMMON.getMoonMobStartDifficulty(DestroyerEntity.class);
    }

    /** Tiny helper method for refreshing config lists */
    private static <T extends IForgeRegistryEntry<T>> void refreshList(List<? extends String> configList, List<T> list, IForgeRegistry<T> registry) {
        list.clear();

        for (String entry : configList) {
            ResourceLocation location = ResourceLocation.tryParse(entry);

            if (location == null) {
                Apocalypse.LOGGER.warn("Invalid registry name found in one of the config lists containing types of " + registry.getRegistryName() + ". Check your Apocalypse common config! Problematic ResourceLocation: " + "\"" + entry + "\"");
            }

            if (registry.containsKey(location)) {
                list.add(registry.getValue(location));
            }
        }
    }
}
