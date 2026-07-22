package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.common.capability.difficulty.IDifficultyCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ApocalypseCapabilities {
    
    public static final Capability<IDifficultyCapability> DIFFICULTY_CAPABILITY = CapabilityManager.get( new CapabilityToken<>() { } );
}
