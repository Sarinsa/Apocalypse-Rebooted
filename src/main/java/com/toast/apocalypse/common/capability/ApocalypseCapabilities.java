package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.common.capability.difficulty.DefaultDifficultyCapability;
import com.toast.apocalypse.common.capability.difficulty.DifficultyCapabilityStorage;
import com.toast.apocalypse.common.capability.difficulty.IDifficultyCapability;
import com.toast.apocalypse.common.capability.entity_marker.DefaultEntityMarkerCapability;
import com.toast.apocalypse.common.capability.entity_marker.EntityMarkerCapabilityStorage;
import com.toast.apocalypse.common.capability.entity_marker.IEntityMarkerCapability;
import com.toast.apocalypse.common.capability.event_data.DefaultEventDataCapability;
import com.toast.apocalypse.common.capability.event_data.EventDataCapabilityStorage;
import com.toast.apocalypse.common.capability.event_data.IEventDataCapability;
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

    @CapabilityInject(value = IEventDataCapability.class)
    public static final Capability<IEventDataCapability> EVENT_DATA_CAPABILITY = null;

    @CapabilityInject(value = IEntityMarkerCapability.class)
    public static final Capability<IEntityMarkerCapability> ENTITY_MARKER_CAPABILITY = null;


    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IRainTickCapability.class, new RainTickCapabilityStorage(), DefaultRainTickCapability::new);
        CapabilityManager.INSTANCE.register(IDifficultyCapability.class, new DifficultyCapabilityStorage(), DefaultDifficultyCapability::new);
        CapabilityManager.INSTANCE.register(IEventDataCapability.class, new EventDataCapabilityStorage(), DefaultEventDataCapability::new);
        CapabilityManager.INSTANCE.register(IEntityMarkerCapability.class, new EntityMarkerCapabilityStorage(), DefaultEntityMarkerCapability::new);
    }
}
