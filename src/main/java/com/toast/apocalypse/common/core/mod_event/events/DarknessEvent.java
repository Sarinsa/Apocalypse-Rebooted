package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.compat.ryaomic.RyoamicCompat;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Shadefiend;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Objects;

public final class DarknessEvent extends AbstractEvent {
    // NBT tag names
    private static final String TAG_STATE = "Stage";
    private static final String TAG_TIME_NEXT_STATE = "TimeNextState";
    
    /** The amount of ticks until the event proceeds to the next {@link Stage}. */
    private int timer = 150;
    /** The current event stage. */
    private Stage stage = Stage.STARTING;
    
    
    public DarknessEvent( EventType<?> type ) {
        super( type );
    }
    
    
    /** Called when this event starts. */
    @Override
    public void onStart( MinecraftServer server, ServerPlayer player ) { }
    
    /**
     * Called every 5 ticks on the server to tick this event.
     *
     * @param player The player to update this event for.
     */
    @Override
    public void update( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        if( timer > 0 ) timer -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        
        if( timer <= 0 && stage != Stage.RESET ) {
            timer = stage.getDuration();
            stage = Stage.cycle( stage );
            
            switch( stage ) {
                // These states function as breaks between other states
                case STARTING, RESET -> { }
                case FIRST_WARN -> warn( level, player, References.CALL_OF_THE_SHADOWS_0, false );
                case SECOND_WARN -> warn( level, player, References.CALL_OF_THE_SHADOWS_1, true );
                case SPAWN -> spawnMonster( level, player );
            }
        }
    }
    
    /** Displays a warning text component to the player, and optionally plays a spooky sound. */
    private void warn( ServerLevel level, Player player, String warning, boolean spookySound ) {
        if( spookySound ) {
            BlockPos soundPos = player.blockPosition().offset(
                    (int) player.getRandom().nextGaussian() * 10,
                    (int) player.getRandom().nextGaussian() * 10,
                    (int) player.getRandom().nextGaussian() * 10
            );
            level.playSound( null, soundPos, SoundEvents.AMBIENT_CAVE.get(), SoundSource.AMBIENT, 0.6F, 1.0F );
        }
        player.displayClientMessage( Component.translatable( warning ), true );
    }
    
    // TODO Maybe make a configurable weighted list of mobs that can spawn
    
    /** Spawns a monster at the given player's location. */
    private void spawnMonster( ServerLevel level, Player player ) {
        player.displayClientMessage( Component.translatable( References.CALL_OF_THE_SHADOWS_2 ), true );
        
        final Shadefiend mob = ApocalypseEntities.SHADEFIEND.get().create( level );
        
        if( mob == null ) return;
        
        ForgeEventFactory.onFinalizeSpawn( mob, level, level.getCurrentDifficultyAt( player.blockPosition() ), MobSpawnType.EVENT, null, null );
        mob.setPos( player.getX(), player.getY(), player.getZ() );
        level.tryAddFreshEntityWithPassengers( mob );
        
        // TODO Maybe make this configurable as well, or at the very least the duration
        player.addEffect( new MobEffectInstance( MobEffects.BLINDNESS, 50 ) );
    }
    
    /** Called before each update to check if this event should keep running. */
    @Override
    public boolean shouldContinueRunning( ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
        if( stage == Stage.RESET || player.isCreative() || player.isSpectator() ) return false;
        return isLowBrightnessAt( level, player );
    }
    
    /** Called when the event ends naturally. */
    @Override
    public void onEnd( MinecraftServer server, ServerPlayer player ) { }
    
    /** Called when the player disconnects before the event can end naturally. */
    @Override
    public void stop( ServerLevel level, ServerPlayer player ) { }
    
    /**
     * Saves this event's data to NBT.
     *
     * @param data The tag to write to.
     */
    @Override
    public void writeAdditional( CompoundTag data ) {
        data.putInt( TAG_STATE, stage.ordinal() );
        data.putInt( TAG_TIME_NEXT_STATE, timer );
    }
    
    /**
     * Loads this event's data from the given NBT.
     *
     * @param data the tag to read from.
     */
    public void read( CompoundTag data, ServerPlayer player, ServerLevel level ) {
        if( NBTHelper.containsNumber( data, TAG_STATE ) ) {
            stage = Stage.getFromOrdinal( data.getInt( TAG_STATE ) );
        }
        if( NBTHelper.containsNumber( data, TAG_TIME_NEXT_STATE ) ) {
            timer = data.getInt( TAG_TIME_NEXT_STATE );
        }
    }
    
    /**
     * @return True if the skylight and block light at the given player's eye position
     * is low enough to trigger this event.
     */
    public static boolean isLowBrightnessAt( Level level, Player player ) {
        final BlockPos eyePos = player.blockPosition().atY( (int) Math.floor( player.getEyeY() ) );
        final int skylight = level.getBrightness( LightLayer.SKY, eyePos );
        final int blockLight = RyoamicCompat.getBlockOrDynamicLightAt( level, eyePos );
        
        return skylight <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.skyLightLevel.get()
                && blockLight <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.blockLightLevel.get();
    }
    
    enum Stage {
        STARTING( 150 ),
        FIRST_WARN( 150 ),
        SECOND_WARN( 150 ),
        SPAWN( 400 ),
        RESET( 0 );
        
        /** The duration of this stage, in ticks. */
        private final int duration;
        
        /** @param duration How long this stage lasts before being switched with the next one */
        Stage( int duration ) { this.duration = duration; }
        
        /** @return The duration of this stage, in ticks. */
        public int getDuration() {
            return duration;
        }
        
        /**
         * @return The stage that corresponds to the given ordinal.
         * Returns {@link Stage#STARTING} if ordinal is out of bounds.
         */
        static Stage getFromOrdinal( int ordinal ) {
            for( Stage stage : Stage.values() ) {
                if( stage.ordinal() == ordinal ) return stage;
            }
            return STARTING;
        }
        
        /**
         * @return The next enum constant in the order after the given stage.
         * If we have reached the last enum constant, the first one is returned.
         */
        static Stage cycle( Stage stage ) {
            Objects.requireNonNull( stage );
            if( stage.ordinal() == values().length - 1 )
                return STARTING;
            return values()[stage.ordinal() + 1];
        }
    }
}
