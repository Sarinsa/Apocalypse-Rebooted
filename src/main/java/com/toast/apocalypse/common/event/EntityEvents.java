package com.toast.apocalypse.common.event;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.difficulty.MobPotionHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EntityEvents {

    /** Whether attribute bonuses should only be applied to mob entities. */
    public static boolean MOBS_ONLY;

    /**
     * A Map containing all the difficulty-limited EntityTypes and their
     * difficulty level needed to start spawning.
     */
    public static Map<EntityType<?>, Double> MOB_DIFFICULTIES = new HashMap<>();


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

    /**
     * Denies mob spawns of mobs that requires the nearest player
     * to have passed a certain difficulty to spawn.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        SpawnReason spawnReason = event.getSpawnReason();

        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.MOB_SUMMONED || spawnReason == SpawnReason.STRUCTURE)
            return;

        EntityType<?> entityType = event.getEntityLiving().getType();

        if (MOB_DIFFICULTIES.containsKey(entityType)) {
            final double neededDifficulty = MOB_DIFFICULTIES.get(entityType);
            final long nearestDifficulty = (PlayerDifficultyManager.getNearestPlayerDifficulty(event.getWorld(), event.getEntityLiving())) / References.DAY_LENGTH;

            if (nearestDifficulty < neededDifficulty)
                event.setResult(Event.Result.DENY);
        }
    }

    /**
     * Handles equipment and potion effects for mobs.
     */
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
        Random random = world.getRandom();
        final long difficulty = PlayerDifficultyManager.getNearestPlayerDifficulty(world, livingEntity);
        final boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();

        // Don't do anything if the player is still on grace period
        if (difficulty <= 0L)
            return;

        if (!(livingEntity instanceof IMob) && MOBS_ONLY)
            return;

        MobAttributeHandler.handleAttributes(livingEntity, difficulty, fullMoon);
        MobPotionHandler.handlePotions(livingEntity, difficulty, fullMoon, random);
        MobEquipmentHandler.handleMobEquipment(livingEntity, difficulty, fullMoon, random);

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
     * Toast!!!!!!!!
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) event.getEntity();
            Item item = itemEntity.getItem().getItem();

            if (item == Items.BREAD) {
                World world = event.getEntity().getCommandSenderWorld();
                int itemCount = itemEntity.getItem().getCount();
                ItemStack stack = new ItemStack(ApocalypseItems.FATHERLY_TOAST.get(), itemCount);
                // Toast level, nice
                stack.getOrCreateTag().putInt("ToastLevel", event.getEntity().level.random.nextInt(99) + 1);
                world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack));
                itemEntity.remove();
            }
            else if (item == ApocalypseItems.FATHERLY_TOAST.get())
                event.setCanceled(true);
        }
    }

    public static void refreshMobDifficulties() {
        MOB_DIFFICULTIES.clear();
        CommentedConfig config = ApocalypseCommonConfig.COMMON.getMobDifficulties();

        for (CommentedConfig.Entry entry : config.entrySet()) {
            String key = entry.getKey();

            if (!StringUtils.isNumeric(key)) {
                logError("Invalid mob difficulty entry \"{}\" found. A mob difficulty entry's key must be a number representing the target difficulty level");
                continue;
            }
            ResourceLocation entityId = ResourceLocation.tryParse(entry.getValue());
            double difficulty = Double.parseDouble(key);

            if (difficulty <= 0) {
                logError("Invalid mob difficulty entry \"{}\" found. The mob difficulty entry key must be a positive number representing the target difficulty level.");
                continue;
            }
            if (entityId == null) {
                logError("Invalid mob difficulty for entry \"{}\" found. Entry name must be an entity ID.", key);
                continue;
            }
            EntityType<?> entityType;

            if (ForgeRegistries.ENTITIES.containsKey(entityId)) {
                entityType = ForgeRegistries.ENTITIES.getValue(entityId);
            }
            else {
                logError("Found mob difficulty entry with a entity ID that does not exist in the Forge registry: {}. This mob difficulty entry will not be loaded.", entityId);
                continue;
            }
            MOB_DIFFICULTIES.put(entityType, difficulty);
        }
    }

    private static void logError(String message, Object... args) {
        Apocalypse.LOGGER.error("[Apocalypse Config] " + message, args);
    }
}
