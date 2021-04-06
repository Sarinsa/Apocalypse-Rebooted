package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.common.capability.difficulty.DefaultDifficultyCapability;
import com.toast.apocalypse.common.capability.difficulty.DifficultyCapabilityStorage;
import com.toast.apocalypse.common.capability.difficulty.IDifficultyCapability;
import com.toast.apocalypse.common.capability.rain_tick.DefaultRainTickCapability;
import com.toast.apocalypse.common.capability.rain_tick.IRainTickCapability;
import com.toast.apocalypse.common.capability.rain_tick.RainTickCapabilityStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ApocalypseCapabilities {

    @CapabilityInject(value = IRainTickCapability.class)
    public static final Capability<IRainTickCapability> RAIN_TICK_CAPABILITY = null;

    @CapabilityInject(value = IDifficultyCapability.class)
    public static final Capability<IDifficultyCapability> DIFFICULTY_CAPABILITY = null;

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IRainTickCapability.class, new RainTickCapabilityStorage(), DefaultRainTickCapability::new);
        CapabilityManager.INSTANCE.register(IDifficultyCapability.class, new DifficultyCapabilityStorage(), DefaultDifficultyCapability::new);
    }
}
