package com.toast.apocalypse.common.compat.ryaomic;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thinkingstudio.ryoamiclights.RyoamicLights;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandler;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;

public class ApocalypseRyoamicCompat {
    
    /** RyoamicLight's mod ID */
    private static final String RYOAMIC = "ryoamiclights";
    /** A logger instance with this class' name. */
    private static final Logger LOG = LogManager.getLogger();
    
    
    /** Called from {@link com.toast.apocalypse.client.ClientRegister#onClientSetup(FMLClientSetupEvent)}. */
    public static void init() {
        if( FMLEnvironment.dist != Dist.CLIENT ) return;
        if( isModLoaded() ) registerHandlers();
    }
    
    /** @return True if the RyoamicLights mod is installed and loaded. */
    private static boolean isModLoaded() {
        return ModList.get().isLoaded( RYOAMIC );
    }
    
    /** Registers Apocalypse's dynamic light handlers. */
    public static void registerHandlers() {
        DynamicLightHandlers.registerDynamicLightHandler( ApocalypseEntities.BREECHER.get(),
                DynamicLightHandler.makeCreeperEntityHandler( null ) );
    }
    
    /**
     * @return Whichever value is greater if RyoamicLights is installed; block light or dynamic light.
     * If the mod is not installed, this just returns the block light at the given block position.
     * <br><br>
     * If RyoamicLights is installed, but we fail to look up the dynamic light
     * for whatever reason, we print a warning and return the block light.
     */
    public static int getBlockOrDynamicLightAt( Level level, BlockPos pos ) {
        int blockLight = level.getBrightness( LightLayer.BLOCK, pos );
        
        if( isModLoaded() ) {
            try {
                RyoamicLights instance = RyoamicLights.get();
                int dynamicLight = (int) instance.getDynamicLightLevel( pos );
                return Math.max( dynamicLight, blockLight );
            }
            catch( Exception e ) {
                LOG.warn( "Failed to look up dynamic light at position: {}", pos );
                return blockLight;
            }
        }
        return blockLight;
    }
}
