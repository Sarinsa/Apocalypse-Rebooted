package com.toast.apocalypse.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.toast.apocalypse.common.core.Apocalypse;
import fathertoast.crust.api.client.SortedKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.text.WordUtils;

@Mod.EventBusSubscriber( modid = Apocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT )
public class ApocalypseKeyBindings {
    
    public static final SortedKeyMapping TOGGLE_DIFFICULTY = create( 0, "toggle_difficulty", InputConstants.KEY_C,
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM );
    public static final SortedKeyMapping GRUMP_INTERACTION = create( 1, "grump_launch_hook", InputConstants.KEY_V,
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM );
    public static final SortedKeyMapping GRUMP_DESCENT = create( 2, "grump_descent", InputConstants.KEY_LCONTROL,
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM );
    
    
    @SubscribeEvent
    public static void registerKeyBindings( RegisterKeyMappingsEvent event ) {
        event.register( TOGGLE_DIFFICULTY );
        event.register( GRUMP_INTERACTION );
        event.register( GRUMP_DESCENT );
    }
    
    /**
     * Helper method for creating a sorted key mapping.
     *
     * @param index           The sorting index of this key mapping.
     * @param name            The name of the key mapping.
     * @param conflictContext The conflict context type to use for this key mapping.
     * @param type            The input type
     * @return The created key mapping.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static SortedKeyMapping create( int index, String name, int keyCode, IKeyConflictContext conflictContext, InputConstants.Type type ) {
        String id = "key." + Apocalypse.MOD_ID + "." + name;
        // noinspection deprecation
        Apocalypse.tk( id, WordUtils.capitalizeFully( name ).replaceAll( "_", " " ) );
        return new SortedKeyMapping( index, id, Apocalypse.MOD_NAME, type, keyCode ).withConflictContext( conflictContext );
    }
}
