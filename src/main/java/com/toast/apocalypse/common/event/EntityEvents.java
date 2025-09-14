package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.difficulty.MobPotionHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.util.NBTUtil;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.EnvironmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.DIFFICULTY;

public class EntityEvents {

    /** A map containing an entity instance per entity type in the registry. */
    private static final Map<EntityType<?>, Entity> ENTITY_FOR_TYPE = new HashMap<>();

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (type == EntityType.PLAYER) continue;

            try {
                Entity entity = type.create(event.getServer().overworld());

                // We only bother with living entities.
                if (entity instanceof LivingEntity) {
                    ENTITY_FOR_TYPE.put(type, entity);
                }
                else if (entity == null) {
                    Apocalypse.LOGGER.error("Failed to create entity instance for type {}! Mob spawn difficulty config list will not work for this type!", type);
                }
            }
            catch (Exception ignored) {
                // If this explodes, no worries (probably isn't a normal living entity anyway)
            }
        }
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        ENTITY_FOR_TYPE.clear();
    }

    /** Cancel full moon monsters despawning during full moons. */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDespawnCheck(MobSpawnEvent.AllowDespawn event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof IFullMoonMob && Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    /**
     * Denies mob spawns of mobs that requires the nearest player
     * to have passed a certain difficulty to spawn.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        MobSpawnType spawnType = event.getSpawnType();

        if (spawnType == MobSpawnType.SPAWNER || spawnType == MobSpawnType.SPAWN_EGG || spawnType == MobSpawnType.COMMAND
                || spawnType == MobSpawnType.MOB_SUMMONED || spawnType == MobSpawnType.STRUCTURE)
            return;

        // Create or get entity instance to check against the list properly
        EntityType<?> entityType = event.getEntityType();
        final Entity entity = ENTITY_FOR_TYPE.getOrDefault(entityType, null);

        // Check if mob can spawn with the difficulty of the closest player
        if (entity != null && DIFFICULTY.GENERAL.mobSpawnDifficulties.contains(entity)) {
            final double neededDifficulty = DIFFICULTY.GENERAL.mobSpawnDifficulties.get().getValue(entity);
            final long nearestDifficulty = (PlayerDifficultyManager.getNearestPlayerDifficulty(event.getLevel(), event.getPos())) / References.DAY_LENGTH;

            if (nearestDifficulty < neededDifficulty) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }

        // Completely ignore spawn placement checks if thunderstorm event is running
        if (event.getLevel() instanceof ServerLevel level) {
            if (ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && level.isThundering() && entity instanceof Enemy) {
                if (!ApocalypseConfig.THUNDERSTORM.GENERAL.spawnsIgnoreLight.get()) {
                    event.setResult(Monster.isDarkEnoughToSpawn(level, event.getPos(), event.getRandom())
                            ? Event.Result.ALLOW
                            : Event.Result.DEFAULT
                    );
                    return;
                }
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public void onSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) return;

        if (event.getLevel() instanceof Level level && ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && level.isThundering()) {
            if (event.getEntity() instanceof Enemy) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    /**
     * Handles equipment and potion effects for mobs.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!EnvironmentHelper.isLoaded(event.getLevel(), BlockPos.containing(event.getX(), event.getY(), event.getZ())))
            return;

        if (NBTUtil.isEntityProcessed(event.getEntity()))
            return;

        Mob mob = event.getEntity();
        ServerLevelAccessor level = event.getLevel();
        RandomSource random = level.getRandom();
        final long difficulty = PlayerDifficultyManager.getNearestPlayerDifficulty(level, mob);
        final boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();

        // Don't do anything if the player is still on grace period
        if (difficulty <= 0L)
            return;

        if (!(mob instanceof Enemy) && ApocalypseConfig.MOB_BUFFING.GENERAL.enemiesOnly.get())
            return;

        MobAttributeHandler.handleAttributes(mob, difficulty, fullMoon);
        MobPotionHandler.handlePotions(mob, difficulty, fullMoon, random);
        MobEquipmentHandler.handleMobEquipment(mob, difficulty, fullMoon, random);

        // Arright, the deed is done! Now lets just mark
        // the entity as "processed" so that we don't do
        // all of this again for the same entity the next
        // time it is loaded into the world.
        NBTUtil.markEntityProcessed(mob);
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
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            Item item = itemEntity.getItem().getItem();

            if (item == Items.BREAD) {
                Level level = event.getEntity().level();
                int itemCount = itemEntity.getItem().getCount();
                ItemStack stack = new ItemStack(ApocalypseItems.FATHERLY_TOAST.get(), itemCount);
                // Toast level, nice
                stack.getOrCreateTag().putInt("ToastLevel", event.getEntity().level().random.nextInt(99) + 1);
                level.addFreshEntity(new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack));
                itemEntity.discard();
            }
            else if (item == ApocalypseItems.FATHERLY_TOAST.get())
                event.setCanceled(true);
        }
    }
}
