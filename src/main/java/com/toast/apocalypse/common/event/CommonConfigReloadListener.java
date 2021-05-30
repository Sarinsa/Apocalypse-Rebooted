package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.MobDifficultyHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerGroup;
import com.toast.apocalypse.common.core.difficulty.WorldDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.misc.DestroyerExplosionContext;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import net.minecraft.block.Block;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

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
        WorldDifficultyManager.MULTIPLAYER_DIFFICULTY_SCALING = ApocalypseCommonConfig.COMMON.multiplayerDifficultyScaling();
        WorldDifficultyManager.DIFFICULTY_MULTIPLIER = ApocalypseCommonConfig.COMMON.getDifficultyRateMultiplier();
        WorldDifficultyManager.SLEEP_PENALTY = ApocalypseCommonConfig.COMMON.getSleepPenalty();

        List<RegistryKey<World>> list = new ArrayList<>();
        ApocalypseCommonConfig.COMMON.getDifficultyPenaltyDimensions().forEach((s -> {
            list.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s)));
        }));
        WorldDifficultyManager.DIMENSION_PENALTY_LIST = list;

        WorldDifficultyManager.DIMENSION_PENALTY = ApocalypseCommonConfig.COMMON.getDimensionPenalty();
        PlayerGroup.USE_AVERAGE_DIFFICULTY = ApocalypseCommonConfig.COMMON.getAverageGroupDifficulty();

        RainDamageTickHelper.RAIN_TICK_RATE = ApocalypseCommonConfig.COMMON.getRainTickRate();
        RainDamageTickHelper.RAIN_DAMAGE = ApocalypseCommonConfig.COMMON.getRainDamage();

        refreshList(ApocalypseCommonConfig.COMMON.getDestroyerProofBlocks(), DestroyerExplosionContext.DESTROYER_PROOF_BLOCKS, ForgeRegistries.BLOCKS);
        refreshList(ApocalypseCommonConfig.COMMON.getHealthBlacklist(), MobDifficultyHandler.HEALTH_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(ApocalypseCommonConfig.COMMON.getSpeedBlacklist(), MobDifficultyHandler.SPEED_BLACKLIST, ForgeRegistries.ENTITIES);
        refreshList(ApocalypseCommonConfig.COMMON.getDamageBlacklist(), MobDifficultyHandler.DAMAGE_BLACKLIST, ForgeRegistries.ENTITIES);

        MobDifficultyHandler.HEALTH_TIME_SPAN = ApocalypseCommonConfig.COMMON.getHealthTimeSpan();
        MobDifficultyHandler.HEALTH_FLAT_BONUS = ApocalypseCommonConfig.COMMON.getHealthFlatBonus();
        MobDifficultyHandler.HEALTH_MULT_BONUS = ApocalypseCommonConfig.COMMON.getHealthMultBonus();
        MobDifficultyHandler.HEALTH_FLAT_BONUS_MAX = ApocalypseCommonConfig.COMMON.getHealthFlatBonusMax();
        MobDifficultyHandler.HEALTH_MULT_BONUS_MAX = ApocalypseCommonConfig.COMMON.getHealthMultBonusMax();
        MobDifficultyHandler.HEALTH_LUNAR_FLAT_BONUS = ApocalypseCommonConfig.COMMON.getHealthLunarFlatBonus();
        MobDifficultyHandler.HEALTH_LUNAR_MULT_BONUS = ApocalypseCommonConfig.COMMON.getHealthLunarMultBonus();

        MobDifficultyHandler.SPEED_TIME_SPAN = ApocalypseCommonConfig.COMMON.getSpeedTimeSpan();
        MobDifficultyHandler.SPEED_MULT_BONUS = ApocalypseCommonConfig.COMMON.getSpeedMultBonus();
        MobDifficultyHandler.SPEED_MULT_BONUS_MAX = ApocalypseCommonConfig.COMMON.getSpeedMultBonusMax();
        MobDifficultyHandler.SPEED_LUNAR_MULT_BONUS = ApocalypseCommonConfig.COMMON.getSpeedLunarMultBonus();

        MobDifficultyHandler.DAMAGE_TIME_SPAN = ApocalypseCommonConfig.COMMON.getDamageTimeSpan();
        MobDifficultyHandler.DAMAGE_FLAT_BONUS = ApocalypseCommonConfig.COMMON.getDamageFlatBonus();
        MobDifficultyHandler.DAMAGE_MULT_BONUS = ApocalypseCommonConfig.COMMON.getDamageMultBonus();
        MobDifficultyHandler.DAMAGE_FLAT_BONUS_MAX = ApocalypseCommonConfig.COMMON.getDamageFlatBonusMax();
        MobDifficultyHandler.DAMAGE_MULT_BONUS_MAX = ApocalypseCommonConfig.COMMON.getDamageMultBonusMax();
        MobDifficultyHandler.DAMAGE_LUNAR_FLAT_BONUS = ApocalypseCommonConfig.COMMON.getDamageLunarFlatBonus();
        MobDifficultyHandler.DAMAGE_LUNAR_MULT_BONUS = ApocalypseCommonConfig.COMMON.getDamageLunarMultBonus();
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
