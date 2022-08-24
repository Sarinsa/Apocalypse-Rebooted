package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.register.ApocalypseEffects;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.goals.MoonMobPlayerTargetGoal;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import com.toast.apocalypse.common.inventory.container.GrumpInventoryContainer;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * This is a full moon mob that is meant to be a high threat to players that are not in a safe area from them.
 * Grumps fly, have a pulling attack, and have a melee attack that can't be reduced below 2 damage and applies a
 * short gravity effect.<br>
 * Unlike most full moon mobs, this one has no means of breaking through defenses and therefore relies on the
 * player being vulnerable to attack - whether by will or by other mobs breaking through to the player.
 */
public class GrumpEntity extends AbstractFullMoonGhastEntity implements IInventoryChangedListener {

    protected static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.defineId(GrumpEntity.class, DataSerializers.OPTIONAL_UUID);
    protected static final DataParameter<Boolean> STAND_BY = EntityDataManager.defineId(GrumpEntity.class, DataSerializers.BOOLEAN);

    /**The current fishhook entity launched by the grump. */
    private MonsterFishHook fishHook;
    private final MoveHelperController moveHelperController;

    protected final Inventory inventory = new Inventory(1);


    public GrumpEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        moveHelperController = new MoveHelperController(this);
        moveControl = moveHelperController;
        xpReward = 3;
        inventory.addListener(this);
        setHeadItem(inventory.getItem(0));
    }

    public static AttributeModifierMap.MutableAttribute createGrumpAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(ForgeMod.SWIM_SPEED.get(), 1.1D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(STAND_BY, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GrumpEntity.MeleeAttackGoal(this));
        this.goalSelector.addGoal(1, new GrumpEntity.FollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new LookAroundGoal(this));
        this.goalSelector.addGoal(3, new LaunchMonsterHookGoal(this));
        this.goalSelector.addGoal(4, new GrumpEntity.RandomFlyGoal(this));
        this.targetSelector.addGoal(0, new GrumpEntity.OwnersAttackerTargetGoal(this));
        this.targetSelector.addGoal(1, new GrumpMobEntityAttackedByTargetGoal(this, IMob.class));
        this.targetSelector.addGoal(2, new MoonMobPlayerTargetGoal<>(this, true));
        this.targetSelector.addGoal(3, new GrumpNearestAttackableTargetGoal<>(this, PlayerEntity.class));
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 0.60F;
    }

    @Override
    protected float getSoundVolume() {
        // Not nearly as loud as a ghast since it is much smaller.
        return 2.0F;
    }

    @Override
    public boolean isPushedByFluid() {
        return false; // Not pushed by fluids
    }

    public static boolean checkGrumpSpawnRules(EntityType<? extends GrumpEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (damageSource.getDirectEntity() instanceof AbstractArrowEntity) {
            if (this.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ApocalypseItems.BUCKET_HELM.get()) {
                damage = Math.min(damage, 1.0F);
            }
        }
        return super.hurt(damageSource, damage);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                int duration = this.level.getDifficulty() == Difficulty.HARD ? 100 : 60;
                ((PlayerEntity)entity).addEffect(new EffectInstance(ApocalypseEffects.HEAVY.get(), duration));
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void checkDespawn() {
        if (hasOwner())
            return;

        super.checkDespawn();
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == ApocalypseItems.FATHERLY_TOAST.get() && !hasOwner()) {
            if (!level.isClientSide) {
                if (level.getRandom().nextInt(5) == 0) {
                    usePlayerItem(player, itemStack);
                    setOwnerUUID(player.getUUID());
                    ApocalypseTriggers.TAMED_GRUMP.trigger((ServerPlayerEntity)player, this);
                    setTarget(null);
                    setPlayerTargetUUID(null);
                    return ActionResultType.SUCCESS;
                }
                else {
                    level.broadcastEntityEvent(this, (byte)6);
                    return ActionResultType.CONSUME;
                }
            }
            return ActionResultType.CONSUME;
        }
        else if (itemStack.getItem() == Items.COOKIE) {
            if (getHealth() < getMaxHealth()) {
                heal((getMaxHealth() + 1.0F) / 6);
                performEatEffects(2);
                usePlayerItem(player, itemStack);
                return ActionResultType.SUCCESS;
            }
        }
        else {
            if (getOwner() == player) {
                if (player.isShiftKeyDown()) {
                    setStandBy(!shouldStandBy());
                }
                else if (getHeadItem().getItem() == Items.SADDLE) {
                    if (getPassengers().isEmpty()) {
                        player.startRiding(this);
                        return ActionResultType.SUCCESS;
                    }
                }
                else {
                    if (!level.isClientSide)
                    openContainerForPlayer((ServerPlayerEntity) player);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public void openContainerForPlayer(ServerPlayerEntity player) {
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }
        player.nextContainerCounter();
        Container container = new GrumpInventoryContainer(player.containerCounter, player.inventory, this.inventory, this);
        NetworkHelper.openGrumpInventory(player, container.containerId, this);
        player.containerMenu = container;
        container.addSlotListener(player);

        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.containerMenu));
    }

    public IInventory getInventory() {
        return this.inventory;
    }

    public ItemStack getHeadItem() {
        return getItemBySlot(EquipmentSlotType.HEAD);
    }

    public void setHeadItem(@Nonnull ItemStack itemStack) {
        setItemSlot(EquipmentSlotType.HEAD, itemStack);
    }

    protected void usePlayerItem(PlayerEntity player, ItemStack itemStack) {
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
    }

    /**
     * @param type Determines which particles should be displayed when the grump is fed.<br>
     *             <br>
     *             0 = Smoke<br>
     *             1 = Happy (Green star things)<br>
     *             2 = Heart
     */
    protected void performEatEffects(int type) {
        IParticleData particleType;

        switch (type) {
            case 0:
                particleType = ParticleTypes.SMOKE;
                break;
            case 1:
                particleType = ParticleTypes.HAPPY_VILLAGER;
                break;
            default:
                particleType = ParticleTypes.HEART;
                break;
        }
        for(int i = 0; i < 7; ++i) {
            double x = random.nextGaussian() * 0.02D;
            double y = random.nextGaussian() * 0.02D;
            double z = random.nextGaussian() * 0.02D;
            level.addParticle(particleType, getRandomX(1.0D), getRandomY() + 0.5D, getRandomZ(1.0D), x, y, z);
        }
        level.playSound(null, blockPosition(), SoundEvents.HORSE_EAT, SoundCategory.NEUTRAL, 0.8F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte eventId) {
        if (eventId == 7) {
            performEatEffects(1);
        }
        else if (eventId == 6) {
            performEatEffects(0);
        }
        else {
            super.handleEntityEvent(eventId);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.isVehicle();
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void travel(Vector3d vec) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider() && getHeadItem().getItem() == Items.SADDLE) {
                if (getControllingPassenger() != null && getControllingPassenger() instanceof LivingEntity) {
                    LivingEntity rider = (LivingEntity) this.getControllingPassenger();

                    this.yRot = rider.yRot;
                    this.yRotO = this.yRot;
                    this.xRot = rider.xRot * 0.5F;
                    this.setRot(this.yRot, this.xRot);
                    this.yBodyRot = this.yRot;
                    this.yHeadRot = this.yBodyRot;

                    float xSpeed = rider.xxa;
                    float ySpeed = rider.yya * 1.15F;
                    float zSpeed = rider.zza;

                    if (rider instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) rider;

                        if (player.isShiftKeyDown()) {
                            ySpeed = -0.5F;
                        }
                        else if (player.jumping) {
                            ySpeed = 0.5F;
                        }
                    }
                    super.travel(new Vector3d(xSpeed, ySpeed, zSpeed));
                }
            }
            else {
                super.travel(vec);
            }
        }
    }

    public void setOwnerUUID(UUID uuid) {
        entityData.set(OWNER_UUID, Optional.of(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public boolean hasOwner() {
        return getOwnerUUID() != null;
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = getOwnerUUID();
            return uuid == null ? null : level.getPlayerByUUID(uuid);
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldStandBy() {
        return this.entityData.get(STAND_BY);
    }

    public void setStandBy(boolean standBy) {
        this.entityData.set(STAND_BY, standBy);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        compoundNBT.putBoolean("StandBy", this.entityData.get(STAND_BY));

        if (hasOwner()) {
            compoundNBT.putUUID("Owner", getOwnerUUID());
        }
        if (inventory != null && !getHeadItem().isEmpty()) {
            compoundNBT.put("HeadItem", getHeadItem().save(new CompoundNBT()));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        setStandBy(compoundNBT.getBoolean("StandBy"));

        if (compoundNBT.contains("HeadItem", Constants.NBT.TAG_COMPOUND)) {
            setHeadItem(ItemStack.of(compoundNBT.getCompound("HeadItem")));
        }

        UUID uuid;

        if (compoundNBT.hasUUID("Owner")) {
            uuid = compoundNBT.getUUID("Owner");
        }
        else {
            String s = compoundNBT.getString("Owner");
            uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            }
            catch (Throwable ignored) {

            }
        }
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld serverWorld, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT compoundNBT) {
        spawnData = super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, spawnData, compoundNBT);
        this.populateDefaultEquipmentSlots(difficultyInstance);
        return spawnData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        double chance = ApocalypseCommonConfig.COMMON.getGrumpBucketHelmetChance();

        if (chance <= 0)
            return;

        if (this.random.nextDouble() <= chance) {
            this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(ApocalypseItems.BUCKET_HELM.get()));
        }
    }

    @Override
    public void containerChanged(IInventory inventory) {
        ItemStack itemStack = inventory.getItem(0);
        setItemSlot(EquipmentSlotType.HEAD, itemStack);

        if (this.tickCount > 20 && itemStack.getItem() == Items.SADDLE || itemStack.getItem() == ApocalypseItems.BUCKET_HELM.get()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5F, 1.0F);
        }
    }

    /** Copied from ghast */
    static class LookAroundGoal extends Goal {
        private final GrumpEntity grump;

        public LookAroundGoal(GrumpEntity grump) {
            this.grump = grump;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.grump.getTarget() == null) {
                Vector3d vector3d = this.grump.getDeltaMovement();
                this.grump.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
                this.grump.xRot = 0.0F;
            } else {
                LivingEntity target = this.grump.getTarget();

                double x = target.getX() - this.grump.getX();
                double z = target.getZ() - this.grump.getZ();
                this.grump.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
                this.grump.xRot = (float) target.getEyeY() - (float) this.grump.getEyeY();
            }
            this.grump.yBodyRot = this.grump.yRot;
        }
    }

    private static class MeleeAttackGoal extends Goal {

        final GrumpEntity grump;

        public MeleeAttackGoal(GrumpEntity grump) {
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.grump = grump;
        }

        private void setWantedPosition(LivingEntity target) {
            Vector3d vector = target.getEyePosition(1.0F).add(0.0D, -(this.grump.getBbHeight() / 2), 0.0D);
            this.grump.moveControl.setWantedPosition(vector.x, vector.y, vector.z, 1.0D);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.grump.getTarget();
            return target != null && this.grump.canSeeDirectly(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.grump.getTarget();

            if (target != null && target.isAlive()) {
                return this.grump.moveControl.hasWanted() && this.grump.moveHelperController.canReachCurrentWanted();
            }
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = this.grump.getTarget();

            if (target != null) {
                this.setWantedPosition(target);
            }
        }

        @Override
        public void stop() {
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void tick() {
            LivingEntity target = this.grump.getTarget();

            if (this.grump.getBoundingBox().inflate(0.3F).intersects(target.getBoundingBox())) {
                this.grump.doHurtTarget(target);
            }
            else {
                this.setWantedPosition(target);
            }
        }
    }

    private static class LaunchMonsterHookGoal extends Goal {

        private final GrumpEntity grump;
        private int timeHookExisted;
        private int timeNextHookLaunch;

        public LaunchMonsterHookGoal(GrumpEntity grump) {
            this.setFlags(EnumSet.of(Flag.TARGET));
            this.grump = grump;
        }

        @Override
        public boolean canUse() {
            if (this.grump.fluidOnEyes != null) {
                return false;
            }

            if (this.grump.getTarget() != null) {
                LivingEntity target = this.grump.getTarget();
                return this.grump.canSee(target) && this.grump.distanceToSqr(target) < 180.0D;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            // Wait a bit before launching the hook, as the grump's
            // body rotation usually don't get updated before
            // this AI task is started, resulting in the hook
            // being launched where the Grump was looking before
            // targeting a player.
            this.timeNextHookLaunch = 20;
        }

        @Override
        public void stop() {
            if (this.grump.fishHook != null) {
                this.grump.fishHook.remove();
                this.grump.fishHook = null;
            }
            this.timeHookExisted = 0;
            this.timeNextHookLaunch = 0;
        }

        @Override
        public void tick() {
            MonsterFishHook hook = grump.fishHook;

            if (hook == null) {
                if (++timeNextHookLaunch >= 40) {
                    spawnMonsterFishHook(grump.getTarget());
                    timeNextHookLaunch = 0;
                }
            }
            else {
                if (hook.getHookedIn() != null) {
                    // The grump might end up accidentally hooking itself, who knows?
                    if (hook.getHookedIn() != grump) {
                        hook.bringInHookedEntity();
                    }
                    removeMonsterFishHook();
                    return;
                }

                if (++timeHookExisted >= 60) {
                    timeHookExisted = 0;
                    removeMonsterFishHook();
                }
            }
        }

        private void removeMonsterFishHook() {
            grump.fishHook.remove();
            grump.fishHook = null;
        }

        private void spawnMonsterFishHook(@Nullable LivingEntity target) {
            if (target == null)
                return;

            World world = grump.getCommandSenderWorld();
            MonsterFishHook fishHook = new MonsterFishHook(grump, target, world);
            world.addFreshEntity(fishHook);
            grump.fishHook = fishHook;
            world.playSound(null, grump.blockPosition(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.6F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        }
    }

    private static class OwnersAttackerTargetGoal extends TargetGoal {

        private final GrumpEntity grump;
        private LivingEntity target;

        public OwnersAttackerTargetGoal(GrumpEntity grump) {
            super(grump, true, true);
            this.grump = grump;
        }

        @Override
        public boolean canUse() {
            if (grump.isVehicle())
                return false;

            if (grump.getOwner() != null && grump.getOwner().isAlive()) {
                LivingEntity owner = grump.getOwner();

                LivingEntity target = owner.getLastHurtByMob() == null ? owner.getLastHurtMob() : owner.getLastHurtByMob();

                if (target != null && target != grump) {
                    if (target instanceof TameableEntity) {
                        if (!((TameableEntity) target).isOwnedBy(owner)) {
                            this.target = target;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.target);
            super.start();
        }
    }

    private static class GrumpMobEntityAttackedByTargetGoal extends MobEntityAttackedByTargetGoal {

        private final GrumpEntity grump;

        public GrumpMobEntityAttackedByTargetGoal(GrumpEntity grump, Class<?>... toIgnoreDamage) {
            super(grump, toIgnoreDamage);
            this.grump = grump;
        }

        @Override
        public boolean canUse() {
            if (grump.getLastHurtByMob() == grump.getOwner() || grump.isVehicle())
                return false;

            return super.canUse();
        }

        @Override
        protected boolean canAttack(@Nullable LivingEntity target, EntityPredicate predicate) {
            boolean canAttack = super.canAttack(target, predicate);

            return canAttack && target != grump.getOwner();
        }
    }

    private static class GrumpNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        private final GrumpEntity grump;

        public GrumpNearestAttackableTargetGoal(GrumpEntity grump, Class<T> targetClass) {
            super(grump, targetClass, true);
            this.grump = grump;
        }

        /** Friggin' large bounding box */
        @Override
        protected AxisAlignedBB getTargetSearchArea(double followRange) {
            return this.mob.getBoundingBox().inflate(followRange, followRange, followRange);
        }

        @Override
        public boolean canUse() {
            return !grump.hasOwner() && super.canUse();
        }

        @Override
        protected boolean canAttack(@Nullable LivingEntity livingEntity, EntityPredicate predicate) {
            boolean canAttack = super.canAttack(target, predicate);

            if (canAttack) {
                if (grump.hasOwner())
                    return !target.getUUID().equals(grump.getOwnerUUID());
            }
            return canAttack;
        }
    }

    /** Copied from ghast */
    static class RandomFlyGoal extends Goal {

        private final GrumpEntity grump;

        public RandomFlyGoal(GrumpEntity grump) {
            this.grump = grump;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (grump.hasOwner() || grump.isVehicle())
                return false;

            if (this.grump.getTarget() != null) {
                return false;
            }
            MovementController movementcontroller = this.grump.getMoveControl();

            if (!movementcontroller.hasWanted()) {
                return true;
            }
            else {
                double x = movementcontroller.getWantedX() - this.grump.getX();
                double y = movementcontroller.getWantedY() - this.grump.getY();
                double z = movementcontroller.getWantedZ() - this.grump.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public void stop() {
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Random random = this.grump.getRandom();
            double x = this.grump.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            double y = this.grump.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            double z = this.grump.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            this.grump.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }
    }

    /** Mostly copy-pasted from {@link net.minecraft.entity.ai.goal.FollowOwnerGoal} */
    @SuppressWarnings("ConstantConditions")
    static class FollowOwnerGoal extends Goal {

        private final GrumpEntity grump;
        private LivingEntity owner;

        public FollowOwnerGoal(GrumpEntity grump) {
            this.grump = grump;
        }

        private void setWantedToOwner() {
            Vector3d vector = owner.getEyePosition(1.0F).add(0.0D, -(this.grump.getBbHeight() / 2), 0.0D);
            this.grump.moveControl.setWantedPosition(vector.x, vector.y, vector.z, 1.0D);
        }

        private void setWantedPosition(double x, double y, double z) {
            this.grump.moveControl.setWantedPosition(x, y, z, 1.0D);
        }

        @Override
        public boolean canUse() {
            return grump.getOwner() != null && !grump.shouldStandBy() && !grump.isVehicle() && grump.distanceToSqr(grump.getOwner()) > 100.0D;
        }

        @Override
        public boolean canContinueToUse() {
            if (owner != null && owner.isAlive() && !grump.shouldStandBy() && !grump.isVehicle() && grump.distanceToSqr(owner) > 40.0D) {
                return this.grump.moveControl.hasWanted() && this.grump.moveHelperController.canReachCurrentWanted();
            }
            return false;
        }

        @Override
        public void start() {
            this.owner = grump.getOwner();
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            if (grump.distanceToSqr(owner) > 200) {
                teleportToOwner();
            }
            else {
                this.setWantedToOwner();
            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = grump.getOwner().blockPosition();

            for(int i = 0; i < 10; ++i) {
                int x = this.randomIntInclusive(-3, 3);
                int y = this.randomIntInclusive(-1, 1);
                int z = this.randomIntInclusive(-3, 3);

                boolean teleported = this.maybeTeleportTo(blockpos.getX() + x, blockpos.getY() + y, blockpos.getZ() + z);

                if (teleported) {
                    return;
                }
            }
        }

        private boolean maybeTeleportTo(int x, int y, int z) {
            if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                return false;
            }
            else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
                return false;
            }
            else {
                grump.moveTo((double)x + 0.5D, y + 0.4D, (double)z + 0.5D, grump.yRot, grump.xRot);
                setWantedPosition(grump.getX(), grump.getY(), grump.getZ());
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos pos) {
            PathNodeType nodeType = WalkNodeProcessor.getBlockPathTypeStatic(grump.level, pos.mutable());

            if (nodeType != PathNodeType.WALKABLE) {
                return false;
            }
            else {
                BlockPos blockpos = pos.subtract(grump.blockPosition());
                return grump.level.noCollision(grump, grump.getBoundingBox().move(blockpos));
            }
        }

        private int randomIntInclusive(int minBound, int maxBound) {
            return grump.getRandom().nextInt(maxBound - minBound + 1) + minBound;
        }
    }
}
