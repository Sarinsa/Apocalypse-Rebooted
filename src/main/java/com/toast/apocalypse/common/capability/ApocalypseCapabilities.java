package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.common.capability.difficulty.IDifficultyCapability;
import com.toast.apocalypse.common.capability.event_data.IEventDataCapability;
import com.toast.apocalypse.common.capability.mobwiki.IMobWikiCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ApocalypseCapabilities {

    public static final Capability<IDifficultyCapability> DIFFICULTY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IEventDataCapability> EVENT_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IMobWikiCapability> MOB_WIKI_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
}
