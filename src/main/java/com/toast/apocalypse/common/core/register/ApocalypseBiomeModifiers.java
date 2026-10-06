package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.datagen.GatherDataListener;
import com.toast.apocalypse.datagen.biomemodifier.SpawnerDataBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ApocalypseBiomeModifiers {
    
    /** Called from {@link GatherDataListener} to generate our biome modifiers types. */
    public static void bootstrap( BootstapContext<BiomeModifier> context ) {
        HolderGetter<Biome> biomeLookup = context.lookup( Registries.BIOME );
        
        register( context, "fearwolf_added_spawns", new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow( BiomeTags.IS_OVERWORLD ),
                new SpawnerDataBuilder().add( ApocalypseObjects.EntityTypes.FEARWOLF, 20, 1, 4 ).build() ) );
    }
    
    /** Convenience method for registering a biome modifier. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( BootstapContext<BiomeModifier> context, String name, BiomeModifier biomeModifier ) {
        context.register( ResourceKey.create( ForgeRegistries.Keys.BIOME_MODIFIERS, Apocalypse.rl( name ) ), biomeModifier );
    }
}
