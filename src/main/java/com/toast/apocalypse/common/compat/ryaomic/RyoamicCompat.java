package com.toast.apocalypse.common.compat.ryaomic;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.thinkingstudio.ryoamiclights.RyoamicLights;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandler;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;

public class RyoamicCompat {
    
    private static final String RYOAMIC = "ryoamiclights";
    
    
    /** Called from {@link com.toast.apocalypse.client.ClientRegister#onClientSetup(FMLClientSetupEvent)}. */
    public static void init() {
        if( FMLEnvironment.dist != Dist.CLIENT ) return;
        
        if( ModList.get().isLoaded( RYOAMIC ) ) {
            registerHandlers();
        }
    }
    
    /** Here we register our dynamic light handlers. */
    public static void registerHandlers() {
        DynamicLightHandlers.registerDynamicLightHandler( ApocalypseEntities.BREECHER.get(),
                DynamicLightHandler.makeCreeperEntityHandler( null ) );
    }
    
    /**
     * @return Whichever value is greater if RyoamicLights is installed; block light or dynamic light.
     * If the mod is not installed, this just returns the block light at the given position.
     * <br><br>
     * If RyoamicLights is installed, but we fail to look up the dynamic light
     * for whatever reason, we print a warning and return the block light.
     */
    public static int getBlockOrDynamicLightAt( Level level, BlockPos pos ) {
        int blockLight = level.getBrightness( LightLayer.BLOCK, pos );
        
        if( ModList.get().isLoaded( RYOAMIC ) ) {
            try {
                RyoamicLights instance = RyoamicLights.get();
                int dynamicLight = (int) instance.getDynamicLightLevel( pos );
                return Math.max( dynamicLight, blockLight );
            }
            catch( Exception e ) {
                warn( "Failed to look up dynamic light at position: {}", pos.toString() );
                return blockLight;
            }
        }
        return blockLight;
    }
    
    @SuppressWarnings( { "StringConcatenationArgumentToLogCall", "SameParameterValue" } )
    private static void warn( String message, Object... args ) {
        Apocalypse.LOGGER.warn( "[RyoamicLights compat] " + message, args );
    }
}
