package com.toast.apocalypse.common.util;

import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

/**
 * Class containing various NBT-related helper methods.
 */
public class NBTUtil {
    
    /** The NBT key used for "main" compound tags where Apocalypse stores its data. */
    private static final String TAG_MOD_DATA = "ApocalypseRebootedData";
    /**
     * The NBT key used for the "is entity processed" flag.
     *
     * @see com.toast.apocalypse.common.event.GameEventListener#onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn)
     */
    private static final String TAG_PROCESSED = "Processed";
    
    
    /** @return True if the given entity has been handled by Apocalypse. */
    public static boolean isEntityProcessed( LivingEntity entity ) {
        if( entity == null ) return false;
        
        if( NBTHelper.containsCompound( entity.getPersistentData(), TAG_MOD_DATA ) ) {
            final CompoundTag modData = entity.getPersistentData().getCompound( TAG_MOD_DATA );
            
            if( NBTHelper.containsNumber( modData, TAG_PROCESSED ) ) {
                return modData.getByte( TAG_PROCESSED ) > (byte) 0;
            }
        }
        return false;
    }
    
    /** Helper method for marking an entity as processed by Apocalypse. */
    public static void markEntityProcessed( LivingEntity entity ) {
        if( entity == null ) return;
        final CompoundTag modTag = NBTHelper.getOrCreateCompound( entity.getPersistentData(), TAG_MOD_DATA );
        modTag.putBoolean( TAG_PROCESSED, true );
    }
}
