package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class RainDamageTickHelper {

    /**
     * Checks if it is time to apply rain tick damage,
     * and applies the damage to the player if so.
     *
     * @see com.toast.apocalypse.common.event.EntityEvents#onPlayerTick(TickEvent.PlayerTickEvent)
     */
    public static void checkAndPerformRainDamageTick(PlayerEntity playerEntity) {
        Apocalypse.LOGGER.info("Rain tick: " + CapabilityHelper.getRainTicks(playerEntity));
        if (CapabilityHelper.getRainTicks(playerEntity) >= ApocalypseCommonConfig.COMMON.getRainTickRate()) {
            float damage = ApocalypseCommonConfig.COMMON.getRainDamage();
            ItemStack headStack = playerEntity.getItemBySlot(EquipmentSlotType.HEAD);

            if (!headStack.isEmpty() && headStack.getItem() == ApocalypseItems.BUCKET_HELM.get()) {
                headStack.hurtAndBreak(playerEntity.getRandom().nextInt(2), playerEntity, (player) -> player.broadcastBreakEvent(EquipmentSlotType.HEAD));
            }
            else {
                playerEntity.hurt(ApocalypseDamageSources.RAIN_DAMAGE, damage);
            }
            CapabilityHelper.clearRainTicks(playerEntity);
        }
        else {
            CapabilityHelper.addRainTick(playerEntity);
        }
    }
}
