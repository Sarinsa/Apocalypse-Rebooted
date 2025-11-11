package com.toast.apocalypse.common.trap_actions;

import com.toast.apocalypse.api.BaseTrapAction;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.List;

public class GhostTrap extends BaseTrapAction {
    
    private static final String DESCRIPTION_KEY = "apocalypse.trap_type.apocalypse.ghost_freeze.description";
    private static final ResourceLocation ICON = Apocalypse.rl( "textures/trap_icons/ghost_freeze.png" );
    
    
    public GhostTrap() {
    }
    
    @Override
    public void execute( Level level, BlockPos pos, Direction facing, AABB areaOfEffect ) {
        if( !level.isClientSide ) {
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    (pos.getX() + 0.5D) + ((facing.getStepX() / 2.0) * 1.1),
                    (pos.getY() + 0.5D) + ((facing.getStepY() / 2.0) * 1.1),
                    (pos.getZ() + 0.5D) + ((facing.getStepZ() / 2.0) * 1.1),
                    15,
                    Mth.randomBetween( level.random, -1.0F, 1.0F ) * 0.08F,
                    Mth.randomBetween( level.random, -1.0F, 1.0F ) * 0.08F,
                    Mth.randomBetween( level.random, -1.0F, 1.0F ) * 0.08F,
                    0.1D
            );
        }
        List<Ghost> nearbyGhosts = level.getEntitiesOfClass( Ghost.class, areaOfEffect );
        
        if( !nearbyGhosts.isEmpty() ) {
            for( Ghost ghost : nearbyGhosts ) {
                if( !ghost.isFrozen() )
                    ghost.freeze( 200 );
            }
        }
    }
    
    @Override
    public int getEffectRadius() {
        return ApocalypseConfig.MISC.TRAP_PROPERTIES.ghostFreezeRange.get();
    }
    
    @Override
    @Nonnull
    public ResourceLocation iconLocation() {
        return ICON;
    }
    
    @Override
    public String getDescriptionKey() {
        return DESCRIPTION_KEY;
    }
}
