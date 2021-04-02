package com.toast.apocalypse.common.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;

public class DestroyerFireballEntity extends FireballEntity {

    public DestroyerFireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
        super(entityType, world);
    }
}
