package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

public class RainDamageTickHelper {

    /**
     * Variables for quick access.
     *
     * These are updated when the mod's
     * common config loads/reloads.
     *
     * {@link CommonConfigReloadListener#updateInfo()}
     */
    public static int RAIN_TICK_RATE;
    public static float RAIN_DAMAGE;

    /**
     * Checks if it is time to apply rain tick damage,
     * and applies the damage to the player if so.
     *
     * @see com.toast.apocalypse.common.event.EntityEvents#onPlayerTick(TickEvent.PlayerTickEvent)
     */
    public static void checkAndPerformRainDamageTick(PlayerEntity player) {
        World world = player.getCommandSenderWorld();

        if (EnchantmentHelper.hasAquaAffinity(player) || !world.isRainingAt(player.blockPosition()))
            return;

        if (CapabilityHelper.getRainTicks(player) >= RAIN_TICK_RATE) {
            CapabilityHelper.clearRainTicks(player);
            ItemStack headStack = player.getItemBySlot(EquipmentSlotType.HEAD);

            if (!headStack.isEmpty()) {
                if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                    return;
                }
                headStack.hurtAndBreak(player.getRandom().nextInt(2), player, (playerEntity) -> player.broadcastBreakEvent(EquipmentSlotType.HEAD));
            }
            else {
                player.hurt(ApocalypseDamageSources.RAIN_DAMAGE, RAIN_DAMAGE);
            }
        }
        else {
            CapabilityHelper.addRainTick(player);
        }
    }
}
