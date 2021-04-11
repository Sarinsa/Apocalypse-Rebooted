package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.impl.FullMoonMobInfo;
import com.toast.apocalypse.api.impl.RegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
    public void onLivingTick(LivingEvent.LivingUpdateEvent event) {

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Tick rain damage
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            // No point doing further checks if rain damage is disabled
            if (!ApocalypseCommonConfig.COMMON.rainDamageEnabled())
                return;

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
     * Modifying final damage dealt to entities by
     * the mobs we have that have a minimum
     * amount of damage they should inflict.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingEntityDamaged(LivingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker != null) {
            float damage = event.getAmount();

            if (attacker.getType() == ApocalypseEntities.GHOST.get()) {
                event.setAmount(Math.max(1.0F, damage));
            }
            else if (attacker.getType() == ApocalypseEntities.GRUMP.get()) {
                event.setAmount(Math.max(2.0F, damage));
            }
        }
    }

    /**
     * Toast
     */
    @SubscribeEvent
    public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) event.getEntity();
            Item item = itemEntity.getItem().getItem();

            if (item == Items.BREAD) {
                World world = event.getEntity().getCommandSenderWorld();
                world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(ApocalypseItems.FATHERLY_TOAST.get())));
                itemEntity.remove();
            }
            else if (item == ApocalypseItems.FATHERLY_TOAST.get())
                event.setCanceled(true);
        }
    }
}
