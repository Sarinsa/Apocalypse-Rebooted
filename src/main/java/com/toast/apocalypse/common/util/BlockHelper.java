package com.toast.apocalypse.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * Used to determine whether mobs can harvest blocks and how fast they can break them.
 */
public class BlockHelper {

    /** Returns true if the mob should destroy the block. */
    public static boolean shouldDamage(BlockPos pos, LivingEntity entity, boolean needsTool, World world) {
        ItemStack heldStack = entity.getUseItem();
        BlockState state = world.getBlockState(pos);
        float destroySpeed = state.getDestroySpeed(world, pos);

        return destroySpeed >= 0.0F && (!needsTool || state.requiresCorrectToolForDrops() || heldStack.isEmpty() && ForgeHooks.canEntityDestroy(world, pos, entity));
    }

    /** Returns the amount of damage to deal to a block. */
    public static float getDamageAmount(LivingEntity entity, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        float destroySpeed = state.getDestroySpeed(world, pos);

        if (destroySpeed < 0.0F)
            return 0.0F;

        // TODO

        /*
        if (!BlockHelper.canHarvestBlock(entity.getUseItem(), state))
            return PropHelper.BREAK_SPEED / destroySpeed / 100.0F;
        return BlockHelper.getCurrentStrengthVsBlock(entity, state) * PropHelper.BREAK_SPEED / destroySpeed / 30.0F;
         */

        // TEMPORARY
        return 0.0F;
    }

    /** Returns whether the item can harvest the specified block. */
    public static boolean canHarvestBlock(ItemStack itemStack, BlockState state) {
        return !state.requiresCorrectToolForDrops() || itemStack.isEmpty() && itemStack.isCorrectToolForDrops(state);
    }

    /** Returns the mob's strength vs. the given block. */
    public static float getCurrentStrengthVsBlock(LivingEntity entity, BlockState state) {
        ItemStack heldStack = entity.getUseItem();
        float strength = heldStack.isEmpty() ? 1.0F : heldStack.getItem().getDestroySpeed(heldStack, state);

        if (strength > 1.0F) {
            int efficiency = EnchantmentHelper.getBlockEfficiency(entity);
            if (efficiency > 0 && heldStack.isEmpty()) {
                strength += efficiency * efficiency + 1;
            }
        }

        if (entity.hasEffect(Effects.DIG_SPEED)) {
            strength *= 1.0F + (entity.getEffect(Effects.DIG_SPEED).getAmplifier() + 1) * 0.2F;
        }
        if (entity.hasEffect(Effects.DIG_SLOWDOWN)) {
            strength *= 1.0F - (entity.getEffect(Effects.DIG_SLOWDOWN).getAmplifier() + 1) * 0.2F;
        }

        if (entity.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            strength /= 5.0F;
        }
        if (!entity.isOnGround()) {
            strength /= 5.0F;
        }
        return Math.max(strength, 0.0F);
    }
}