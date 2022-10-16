package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.config.CommonConfigReloadListener;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;

public class RainDamageTickHelper {

    /**
     * Variables for quick access.<br>
     * <br>
     *
     * These are updated when the mod's
     * common config loads/reloads.<br
     * <br>
     *
     * {@link CommonConfigReloadListener#updateInfo()}
     */
    public static int RAIN_TICK_RATE;
    public static float RAIN_DAMAGE;

    private int timeRainDmgCheck;


    public RainDamageTickHelper() {
        this.resetTimer();
    }

    public void resetTimer() {
        this.timeRainDmgCheck = 0;
    }

    /**
     * Checks if it is time to apply rain tick damage,
     * and applies the damage to the player if so.<br>
     * <br>
     *
     * Called from {@link com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager#onServerTick(TickEvent.ServerTickEvent)}<br>
     * <br>
     */
    public void checkAndPerformRainDamageTick(Iterable<ServerWorld> serverLevels, PlayerDifficultyManager difficultyManager) {
        if (!ApocalypseCommonConfig.COMMON.rainDamageEnabled())
            return;

        if (++this.timeRainDmgCheck >= RAIN_TICK_RATE) {
            for (ServerWorld level : serverLevels) {
                for (ServerPlayerEntity player : level.players()) {
                    if (!difficultyManager.isRainingAcid(level))
                        continue;

                    if (EnchantmentHelper.hasAquaAffinity(player) || !level.isRainingAt(player.blockPosition().offset(0.0D, player.getEyeHeight(), 0.0D)))
                        continue;

                    ItemStack headStack = player.getItemBySlot(EquipmentSlotType.HEAD);

                    if (!headStack.isEmpty()) {
                        if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                            continue;
                        }
                        headStack.hurtAndBreak(player.getRandom().nextInt(2), player, (playerEntity) -> player.broadcastBreakEvent(EquipmentSlotType.HEAD));
                    }
                    else {
                        player.hurt(ApocalypseDamageSources.RAIN_DAMAGE, RAIN_DAMAGE);
                    }
                }
            }
            this.resetTimer();
        }
    }
}
