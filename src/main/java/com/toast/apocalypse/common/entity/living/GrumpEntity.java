package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.IFullMoonMob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.world.World;

public class GrumpEntity extends GhastEntity implements IFullMoonMob {

    public GrumpEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }
}
