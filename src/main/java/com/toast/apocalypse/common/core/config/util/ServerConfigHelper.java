package com.toast.apocalypse.common.core.config.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.EnumMap;

/**
 * Weird and hacky helper for modifying server/world config
 * before world creation on clients.
 */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Apocalypse.MOD_ID, value = Dist.DEDICATED_SERVER )
public class ServerConfigHelper {
    
    /** Updated on client only as these values are only required for integrated servers. */
    public static double DESIRED_DEFAULT_MAX_DIFFICULTY;
    public static double DESIRED_DEFAULT_GRACE_PERIOD;
    
    static {
        resetValues();
    }
    
    private static void resetValues() {
        DESIRED_DEFAULT_MAX_DIFFICULTY = 150.0D;
        DESIRED_DEFAULT_GRACE_PERIOD = 1.0D;
    }
    
    /**
     * Writes the current Apocalypse world settings
     * to the integrated server's mod server config.
     */
    @SuppressWarnings( "unchecked" )
    public static void updateModServerConfig() {
        final String configName = ConfigTracker.INSTANCE.getConfigFileName( Apocalypse.MOD_ID, ModConfig.Type.SERVER );
        
        if( configName != null && !configName.isEmpty() ) {
            final ModContainer modContainer = ModList.get().getModContainerById( Apocalypse.MOD_ID ).orElseThrow(
                    () -> new IllegalStateException( "Failed to fetch Apocalypse's mod container. This is impossible..?" ) );
            final Field field = ObfuscationReflectionHelper.findField( ModContainer.class, "configs" );
            
            try {
                final EnumMap<ModConfig.Type, ModConfig> configMap = (EnumMap<ModConfig.Type, ModConfig>) field.get( modContainer );
                final ModConfig config = configMap.getOrDefault( ModConfig.Type.SERVER, null );
                
                // Manually overwrite config values and save
                if( config != null ) {
                    final CommentedConfig commentedConfig = config.getConfigData();
                    commentedConfig.set( "difficulty.defaultPlayerMaxDifficulty", DESIRED_DEFAULT_MAX_DIFFICULTY );
                    commentedConfig.set( "difficulty.defaultPlayerGracePeriod", DESIRED_DEFAULT_GRACE_PERIOD );
                    config.save();
                }
            }
            catch( IllegalAccessException e ) {
                // noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
        resetValues();
    }
    
    /** Called from {@link com.toast.apocalypse.client.screen.misc.ApocalypseWCTab}. */
    public static void updateModServerConfigValues( double maxDifficulty, double gracePeriod ) {
        DESIRED_DEFAULT_MAX_DIFFICULTY = maxDifficulty;
        DESIRED_DEFAULT_GRACE_PERIOD = gracePeriod;
    }
}
