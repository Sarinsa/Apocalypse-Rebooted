package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.ACID_RAIN;

public class RainDamageTickHandler {

    private int timeRainDmgCheck;


    public RainDamageTickHandler() {
        resetTimer();
    }


    /**
     * Checks if it is time to apply acid rain tick damage,
     * and applies damage to all exposed players.<br>
     * <br>
     * <p>
     * Called from {@link PlayerDifficultyManager#onServerTick(TickEvent.ServerTickEvent)}<br>
     * <br>
     */
    public void checkAndPerformRainDamageTick(Iterable<ServerLevel> serverLevels, PlayerDifficultyManager difficultyManager) {
        if (ACID_RAIN.GENERAL.rainDamage.get() <= 0) return;

        if (++timeRainDmgCheck >= (ACID_RAIN.GENERAL.damageRate.get() * 20)) {
            for (ServerLevel level : serverLevels) {
                for (ServerPlayer player : level.players()) {
                    if (!difficultyManager.isRainingAcid(level))
                        continue;

                    if (EnchantmentHelper.hasAquaAffinity(player) || !isRainingOrSnowingAt(level, player.blockPosition().offset(0, (int) player.getEyeHeight(), 0)))
                        continue;

                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);

                    if (!headStack.isEmpty()) {
                        if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                            continue;
                        }
                        headStack.hurtAndBreak(player.getRandom().nextInt(2), player, (playerEntity) -> player.broadcastBreakEvent(EquipmentSlot.HEAD));
                    } else {
                        player.hurt(ApocalypseDamageSources.of(level, ApocalypseDamageSources.ACID_RAIN), ACID_RAIN.GENERAL.rainDamage.get());
                    }
                }
            }
            resetTimer();
        }
    }

    private boolean isRainingOrSnowingAt(Level level, BlockPos pos) {
        if (!level.isRaining()) {
            return false;
        }
        else if (!level.canSeeSky(pos)) {
            return false;
        }
        else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        else {
            Biome biome = level.getBiome(pos).value();
            return biome.getPrecipitationAt(pos) == Biome.Precipitation.RAIN || biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW;
        }
    }

    public void resetTimer() {
        timeRainDmgCheck = 0;
    }
}
