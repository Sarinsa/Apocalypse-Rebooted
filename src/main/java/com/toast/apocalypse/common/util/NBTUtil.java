package com.toast.apocalypse.common.util;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

public class NBTUtil {

    private static final String MOD_TAG = "ApocalypseRebootedData";
    private static final String PROCESSED_TAG = "Processed";

    public static boolean isEntityProcessed(LivingEntity livingEntity) {
        if (livingEntity == null)
            return false;

        if (livingEntity.getPersistentData().contains(MOD_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag modData = livingEntity.getPersistentData().getCompound(MOD_TAG);

            if (modData.contains(PROCESSED_TAG, Tag.TAG_BYTE)) {
                return modData.getByte(PROCESSED_TAG) > (byte) 0;
            }
        }
        return false;
    }

    public static void markEntityProcessed(LivingEntity livingEntity) {
        if (livingEntity == null)
            return;

        CompoundTag modTag = livingEntity.getPersistentData().getCompound(MOD_TAG);
        modTag.put(PROCESSED_TAG, ByteTag.valueOf(true));
        livingEntity.getPersistentData().put(MOD_TAG, modTag);
    }
}
