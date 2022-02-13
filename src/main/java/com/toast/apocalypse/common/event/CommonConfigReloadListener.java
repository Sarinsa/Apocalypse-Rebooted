package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.*;
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

import java.util.ArrayList;
import java.util.List;

import static com.toast.apocalypse.common.core.config.ApocalypseCommonConfig.COMMON;

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
     * Updates static references for config values
     * and repopulates various collections and whatnot.
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

        EntityEvents.MOBS_ONLY = COMMON.getMobsOnly();

        refreshList(COMMON.getDestroyerProofBlocks(), DestroyerExplosionContext.DESTROYER_PROOF_BLOCKS, ForgeRegistries.BLOCKS);
        refreshList(COMMON.getHealthBlacklist(), MobAttributeHandler.HEALTH_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getSpeedBlacklist(), MobAttributeHandler.SPEED_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getDamageBlacklist(), MobAttributeHandler.DAMAGE_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getKnockbackResBlacklist(), MobAttributeHandler.KNOCKBACK_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(COMMON.getCanHaveWeapons(), MobEquipmentHandler.CAN_HAVE_WEAPONS, ForgeRegistries.ENTITIES);

        MobAttributeHandler.HEALTH_TIME_SPAN = COMMON.getHealthTimeSpan();
        MobAttributeHandler.HEALTH_FLAT_BONUS = COMMON.getHealthFlatBonus();
        MobAttributeHandler.HEALTH_MULT_BONUS = COMMON.getHealthMultBonus();
        MobAttributeHandler.HEALTH_FLAT_BONUS_MAX = COMMON.getHealthFlatBonusMax();
        MobAttributeHandler.HEALTH_MULT_BONUS_MAX = COMMON.getHealthMultBonusMax();
        MobAttributeHandler.HEALTH_LUNAR_FLAT_BONUS = COMMON.getHealthLunarFlatBonus();
        MobAttributeHandler.HEALTH_LUNAR_MULT_BONUS = COMMON.getHealthLunarMultBonus();

        MobAttributeHandler.SPEED_TIME_SPAN = COMMON.getSpeedTimeSpan();
        MobAttributeHandler.SPEED_MULT_BONUS = COMMON.getSpeedMultBonus();
        MobAttributeHandler.SPEED_MULT_BONUS_MAX = COMMON.getSpeedMultBonusMax();
        MobAttributeHandler.SPEED_LUNAR_MULT_BONUS = COMMON.getSpeedLunarMultBonus();

        MobAttributeHandler.DAMAGE_TIME_SPAN = COMMON.getDamageTimeSpan();
        MobAttributeHandler.DAMAGE_FLAT_BONUS = COMMON.getDamageFlatBonus();
        MobAttributeHandler.DAMAGE_MULT_BONUS = COMMON.getDamageMultBonus();
        MobAttributeHandler.DAMAGE_FLAT_BONUS_MAX = COMMON.getDamageFlatBonusMax();
        MobAttributeHandler.DAMAGE_MULT_BONUS_MAX = COMMON.getDamageMultBonusMax();
        MobAttributeHandler.DAMAGE_LUNAR_FLAT_BONUS = COMMON.getDamageLunarFlatBonus();
        MobAttributeHandler.DAMAGE_LUNAR_MULT_BONUS = COMMON.getDamageLunarMultBonus();

        MobAttributeHandler.KNOCKBACK_RES_TIME_SPAN = COMMON.getKnockbackResTimeSpan();
        MobAttributeHandler.KNOCKBACK_RES_FLAT_BONUS = COMMON.getKnockbackResFlatBonus();
        MobAttributeHandler.KNOCKBACK_RES_FLAT_BONUS_MAX = COMMON.getKnockbackResFlatBonusMax();
        MobAttributeHandler.KNOCKBACK_RES_LUNAR_FLAT_BONUS = COMMON.getKnockbackResLunarFlatBonus();

        MobEquipmentHandler.refreshEquipmentList();
        MobPotionHandler.refreshPotionMap();

        MobEquipmentHandler.WEAPONS_TIME = COMMON.getWeaponsTimeSpan();
        MobEquipmentHandler.WEAPONS_CHANCE = COMMON.getWeaponsChance();
        MobEquipmentHandler.WEAPONS_LUNAR_CHANCE = COMMON.getWeaponsLunarChance();
        MobEquipmentHandler.WEAPONS_CHANCE_MAX = COMMON.getWeaponsMaxChance();
        MobEquipmentHandler.CURRENT_WEAPON_TIER_ONLY = COMMON.getUseCurrentWeaponTierOnly();

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

        FullMoonEvent.GHOST_MAX_COUNT = COMMON.getMoonMobMaxCount(GhostEntity.class);
        FullMoonEvent.BREECHER_MAX_COUNT = COMMON.getMoonMobMaxCount(BreecherEntity.class);
        FullMoonEvent.GRUMP_MAX_COUNT = COMMON.getMoonMobMaxCount(GrumpEntity.class);
        FullMoonEvent.SEEKER_MAX_COUNT = COMMON.getMoonMobMaxCount(SeekerEntity.class);
        FullMoonEvent.DESTROYER_MAX_COUNT = COMMON.getMoonMobMaxCount(DestroyerEntity.class);

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
