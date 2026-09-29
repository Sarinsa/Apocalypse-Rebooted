package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.datagen.GatherDataListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ApocalypseDamageTypes {
    
    /** The rain damage source. */
    public static final ResourceKey<DamageType> ACID_RAIN = ResourceKey.create( Registries.DAMAGE_TYPE, Apocalypse.rl( "acid_rain" ) );
    public static final ResourceKey<DamageType> LIGHT_INTOLERANCE = ResourceKey.create( Registries.DAMAGE_TYPE, Apocalypse.rl( "light_intolerance" ) );
    
    
    /** Helper method for creating a damage source instance */
    public static DamageSource of( Level level, ResourceKey<DamageType> key ) {
        return new DamageSource( level.registryAccess().registryOrThrow( Registries.DAMAGE_TYPE ).getHolderOrThrow( key ) );
    }
    
    
    //-------------------------------------------------------------------------
    //                           DATA GENERATION
    //-------------------------------------------------------------------------
    
    /** Called from {@link GatherDataListener} to generate our damage types. */
    public static void bootstrap( BootstapContext<DamageType> context ) {
        registerSimple( context, ACID_RAIN );
        registerSimple( context, LIGHT_INTOLERANCE );
    }
    
    /** Data-gen helper method for generating a simple damage type. */
    protected static void registerSimple( BootstapContext<DamageType> context, ResourceKey<DamageType> damageTypeKey ) {
        register( context, damageTypeKey, new DamageType( msgId( damageTypeKey.location().getPath() ), 0.0F ) );
    }
    
    /** Data-gen helper method for generating a damage type. */
    protected static void register( BootstapContext<DamageType> context, ResourceKey<DamageType> damageTypeKey, DamageType damageType ) {
        context.register( damageTypeKey, damageType );
    }
    
    /** @return A damage type message ID from Apocalypse's namespace and the given name string. */
    private static String msgId( String name ) {
        return Apocalypse.MOD_ID + "." + name;
    }
}
