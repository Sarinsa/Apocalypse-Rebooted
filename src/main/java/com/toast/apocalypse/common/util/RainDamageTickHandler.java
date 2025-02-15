package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
     *
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

                    if (EnchantmentHelper.hasAquaAffinity(player) || !level.isRainingAt(player.blockPosition().offset(0, (int) player.getEyeHeight(), 0)))
                        continue;

                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);

                    if (!headStack.isEmpty()) {
                        if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                            continue;
                        }
                        headStack.hurtAndBreak(player.getRandom().nextInt(2), player, (playerEntity) -> player.broadcastBreakEvent(EquipmentSlot.HEAD));
                    }
                    else {
                        player.hurt(ApocalypseDamageSources.of(level, ApocalypseDamageSources.ACID_RAIN), ACID_RAIN.GENERAL.rainDamage.get());
                    }
                }
            }
            resetTimer();
        }
    }

    public void resetTimer() {
        timeRainDmgCheck = 0;
    }
}
