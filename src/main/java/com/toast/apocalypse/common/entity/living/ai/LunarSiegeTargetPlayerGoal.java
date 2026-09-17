package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import com.toast.apocalypse.common.entity.living.AbstractLunarSiegeGhast;
import com.toast.apocalypse.common.entity.living.Grump;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class LunarSiegeTargetPlayerGoal<T extends Mob & ILunarSiegeMob> extends TargetGoal {
    
    private final T moonMob;
    
    public LunarSiegeTargetPlayerGoal( T mobEntity, boolean mustSee ) {
        super( mobEntity, mustSee );
        moonMob = mobEntity;
    }
    
    @Override
    public boolean canUse() {
        UUID playerTargetUUID = moonMob.getPlayerTargetUUID();
        
        if( playerTargetUUID == null )
            return false;
        
        // noinspection resource
        Player player = moonMob.level().getPlayerByUUID( playerTargetUUID );
        
        if( player == null )
            return false;
        
        if( moonMob instanceof Grump grump ) {
            if( grump.hasOwner() )
                return false;
        }
        
        if( mustSee ) {
            if( mob instanceof AbstractLunarSiegeGhast ) {
                if( !((AbstractLunarSiegeGhast) moonMob).canSeeDirectly( player ) )
                    return false;
            }
            else if( !mob.getSensing().hasLineOfSight( player ) )
                return false;
        }
        return player.isAlive() && !player.isCreative() && !player.isSpectator();
    }
    
    @SuppressWarnings( "ConstantConditions" )
    public void start() {
        LivingEntity target = moonMob.getTarget();
        // noinspection resource
        Player playerTarget = moonMob.level().getPlayerByUUID( moonMob.getPlayerTargetUUID() );
        
        moonMob.setTarget( playerTarget != null ? playerTarget : target );
        super.start();
    }
}
