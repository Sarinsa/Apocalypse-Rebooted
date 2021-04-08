package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.impl.FullMoonMobInfo;
import com.toast.apocalypse.api.impl.RegistryHelper;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.register.ApocalypseItems;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class EntityEvents {

    /**
     * Cancel full moon monsters despawning during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDespawnCheck(LivingSpawnEvent.AllowDespawn event) {
        if (RegistryHelper.FULL_MOON_MOB_INFO.containsKey(event.getEntityLiving().getType())) {
            FullMoonMobInfo mobInfo = RegistryHelper.FULL_MOON_MOB_INFO.get(event.getEntityLiving().getType());

            if (WorldDifficultyManager.isFullMoon(event.getWorld()) && mobInfo.isPersistent()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Tick rain damage
        if (event.side == LogicalSide.SERVER) {
            // No point doing further checks if rain damage is disabled
            if (!ApocalypseCommonConfig.COMMON.rainDamageEnabled())
                return;

            World world = event.player.getCommandSenderWorld();

            if (EnchantmentHelper.hasAquaAffinity(event.player) || !world.canSeeSky(event.player.blockPosition()))
                return;

            if (world.isRainingAt(event.player.blockPosition()))
                RainDamageTickHelper.checkAndPerformRainDamageTick(event.player);
        }
    }

    /**
     * Prevent players from being able
     * to sleep during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerTrySleep(PlayerSleepInBedEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();

        if (player.isSleeping() || !player.isAlive())
            return;

        if (world.isNight() && WorldDifficultyManager.isFullMoon(world)) {
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
            player.displayClientMessage(new TranslationTextComponent(References.TRY_SLEEP_FULL_MOON), true);
        }
    }

    /**
     * Toast
     */
    @SubscribeEvent
    public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) event.getEntity();

            if (itemEntity.getItem().getItem() == Items.BREAD) {
                World world = event.getEntity().getCommandSenderWorld();
                world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(ApocalypseItems.FATHERLY_TOAST.get())));
                itemEntity.remove();
            }
        }
    }
}
