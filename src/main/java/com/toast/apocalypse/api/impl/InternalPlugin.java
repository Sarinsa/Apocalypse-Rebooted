package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.ApocalypseApi;
import com.toast.apocalypse.api.plugin.ApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IRegistryHelper;
import net.minecraft.entity.monster.SkeletonEntity;

@ApocalypsePlugin
public class InternalPlugin implements IApocalypsePlugin {

    @Override
    public void load(ApocalypseApi api) {
        IRegistryHelper registryHelper = api.getRegistryHelper();

        // Not setting target since the skeleton
        // starts shooting immediately and more often
        // than not ends up hitting other monsters,
        // starting a whole freakin' gang fight.
        registryHelper.registerSeekerAlertable(SkeletonEntity.class, (skeleton, target, seeker) -> {
            skeleton.setLastHurtByMob(null);
            skeleton.getNavigation().moveTo(target, 1.0D);
        });
    }

    @Override
    public String getPluginId() {
        return "Apocalypse-Builtin";
    }
}
