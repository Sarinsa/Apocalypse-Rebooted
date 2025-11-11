package com.toast.apocalypse.common.util;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

/**
 * Helper class containing various methods for manipulating NBT.
 */
public class NBTUtil {
    
    private static final String TAG_MOD_DATA = "ApocalypseRebootedData";
    private static final String TAG_PROCESSED = "Processed";
    
    
    /** @return True if the given entity has been handled by Apocalypse. */
    public static boolean isEntityProcessed( LivingEntity livingEntity ) {
        if( livingEntity == null ) return false;
        
        if( livingEntity.getPersistentData().contains( TAG_MOD_DATA, Tag.TAG_COMPOUND ) ) {
            CompoundTag modData = livingEntity.getPersistentData().getCompound( TAG_MOD_DATA );
            
            if( modData.contains( TAG_PROCESSED, Tag.TAG_BYTE ) ) {
                return modData.getByte( TAG_PROCESSED ) > (byte) 0;
            }
        }
        return false;
    }
    
    /** Helper method for marking an entity as processed by Apocalypse. */
    public static void markEntityProcessed( LivingEntity livingEntity ) {
        if( livingEntity == null ) return;
        
        CompoundTag modTag = livingEntity.getPersistentData().getCompound( TAG_MOD_DATA );
        modTag.put( TAG_PROCESSED, ByteTag.valueOf( true ) );
        livingEntity.getPersistentData().put( TAG_MOD_DATA, modTag );
    }
}
