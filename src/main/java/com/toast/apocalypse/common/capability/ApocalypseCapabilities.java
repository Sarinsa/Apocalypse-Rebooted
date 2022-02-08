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
import com.toast.apocalypse.common.capability.mobwiki.DefaultMobWikiCapability;
import com.toast.apocalypse.common.capability.mobwiki.IMobWikiCapability;
import com.toast.apocalypse.common.capability.mobwiki.MobWikiCapabilityStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ApocalypseCapabilities {

    @CapabilityInject(value = IDifficultyCapability.class)
    public static final Capability<IDifficultyCapability> DIFFICULTY_CAPABILITY = null;

    @CapabilityInject(value = IEventDataCapability.class)
    public static final Capability<IEventDataCapability> EVENT_DATA_CAPABILITY = null;

    @CapabilityInject(value = IEntityMarkerCapability.class)
    public static final Capability<IEntityMarkerCapability> ENTITY_MARKER_CAPABILITY = null;

    @CapabilityInject(value = IMobWikiCapability.class)
    public static final Capability<IMobWikiCapability> MOB_WIKI_CAPABILITY = null;


    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IDifficultyCapability.class, new DifficultyCapabilityStorage(), DefaultDifficultyCapability::new);
        CapabilityManager.INSTANCE.register(IEventDataCapability.class, new EventDataCapabilityStorage(), DefaultEventDataCapability::new);
        CapabilityManager.INSTANCE.register(IEntityMarkerCapability.class, new EntityMarkerCapabilityStorage(), DefaultEntityMarkerCapability::new);
        CapabilityManager.INSTANCE.register(IMobWikiCapability.class, new MobWikiCapabilityStorage(), DefaultMobWikiCapability::new);
    }
}
