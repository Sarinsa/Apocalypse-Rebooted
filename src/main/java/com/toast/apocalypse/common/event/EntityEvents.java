package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.difficulty.MobPotionHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEvents {

    /** Whether attribute bonuses should only be applied to mob entities. */
    public static boolean MOBS_ONLY;

    /**
     * Cancel full moon monsters despawning during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDespawnCheck(LivingSpawnEvent.AllowDespawn event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getEntityLiving() instanceof IFullMoonMob && Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide)
            return;

        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof PlayerEntity)
            return;

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if (CapabilityHelper.isEntityMarked(livingEntity))
            return;

        World world = livingEntity.getCommandSenderWorld();
        final long difficulty = PlayerDifficultyManager.getNearestPlayerDifficulty(world, livingEntity);
        final boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();

        // Don't do anything if the player is still on grace period
        if (difficulty <= 0L)
            return;

        if (!(livingEntity instanceof IMob) && MOBS_ONLY)
            return;

        MobAttributeHandler.handleAttributes(livingEntity, difficulty, fullMoon);
        MobPotionHandler.handlePotions(livingEntity, difficulty, fullMoon);
        MobEquipmentHandler.handleMobEquipment(livingEntity, difficulty, fullMoon);

        // Arright, the deed is done! Now lets just mark
        // the entity as "processed" so that we don't do
        // all of this again for the same entity the next
        // time it is loaded into the world.
        CapabilityHelper.markEntity(livingEntity);
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
                int itemCount = itemEntity.getItem().getCount();
                world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(ApocalypseItems.FATHERLY_TOAST.get(), itemCount)));
                itemEntity.remove();
            }
            else if (item == ApocalypseItems.FATHERLY_TOAST.get())
                event.setCanceled(true);
        }
    }
}
