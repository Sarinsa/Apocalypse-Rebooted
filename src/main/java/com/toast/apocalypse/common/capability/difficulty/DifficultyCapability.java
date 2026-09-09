package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;

public class DifficultyCapability implements IDifficultyCapability {
    
    public static final String KEY_DIFFICULTY = "Difficulty";
    public static final String KEY_MAX_DIFFICULTY = "MaxDifficulty";
    public static final String KEY_MULTIPLIER = "DifficultyMul";
    public static final String KEY_PARTIAL_DIFF = "DifficultyPart";
    
    private long difficulty;
    private long maxDifficulty;
    private double multiplier;
    private double partialDifficulty;
    
    
    public DifficultyCapability() {
        difficulty = -CapabilityHelper.mulByDayLength( (long) ApocalypseServerConfig.SERVER.getPlayerGracePeriod() );
        maxDifficulty = CapabilityHelper.mulByDayLength( (long) ApocalypseServerConfig.SERVER.getDefaultPlayerMaxDifficulty() );
        multiplier = 1.0;
        partialDifficulty = 0.0;
    }
    
    
    @Override
    public void setDifficulty( long difficulty ) {
        this.difficulty = difficulty;
    }
    
    @Override
    public long getDifficulty() {
        return difficulty;
    }
    
    @Override
    public void setMaxDifficulty( long maxDifficulty ) {
        this.maxDifficulty = maxDifficulty;
    }
    
    @Override
    public long getMaxDifficulty() {
        return maxDifficulty;
    }
    
    @Override
    public void setMultiplier( double multiplier ) {
        this.multiplier = multiplier;
    }
    
    @Override
    public double getMultiplier() {
        return multiplier;
    }
    
    @Override
    public void setPartialDifficulty( double partialDifficulty ) {
        this.partialDifficulty = partialDifficulty;
    }
    
    @Override
    public double getPartialDifficulty() {
        return partialDifficulty;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        
        tag.putLong( KEY_DIFFICULTY, difficulty );
        tag.putLong( KEY_MAX_DIFFICULTY, maxDifficulty );
        tag.putDouble( KEY_MULTIPLIER, multiplier );
        tag.putDouble( KEY_MULTIPLIER, partialDifficulty );
        
        return tag;
    }
    
    @Override
    public void deserializeNBT( CompoundTag compoundTag ) {
        if( NBTHelper.containsNumber( compoundTag, KEY_DIFFICULTY ) )
            difficulty = compoundTag.getLong( KEY_DIFFICULTY );
        
        if( NBTHelper.containsNumber( compoundTag, KEY_MAX_DIFFICULTY ) )
            maxDifficulty = compoundTag.getLong( KEY_MAX_DIFFICULTY );
        
        if( NBTHelper.containsNumber( compoundTag, KEY_MULTIPLIER ) )
            multiplier = compoundTag.getDouble( KEY_MULTIPLIER );
        
        if( NBTHelper.containsNumber( compoundTag, KEY_MULTIPLIER ) )
            partialDifficulty = compoundTag.getDouble( KEY_MULTIPLIER );
    }
}