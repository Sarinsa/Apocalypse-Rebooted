package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBiomeModifiers;
import com.toast.apocalypse.common.core.register.ApocalypseDamageTypes;
import com.toast.apocalypse.datagen.advancement.ApocalypseAdvancementProvider;
import com.toast.apocalypse.datagen.lang.ApocalypseLangProvider;
import com.toast.apocalypse.datagen.loot.ApocalypseLootModProvider;
import com.toast.apocalypse.datagen.loot.ApocalypseLootTableProvider;
import com.toast.apocalypse.datagen.model.ApocalypseBlockModelProvider;
import com.toast.apocalypse.datagen.model.ApocalypseItemModelProvider;
import com.toast.apocalypse.datagen.particle.ApocalypseParticleProvider;
import com.toast.apocalypse.datagen.recipe.ApocalypseRecipeProvider;
import com.toast.apocalypse.datagen.sound.ApocalypseSoundProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseBlockTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseDamageTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseEntityTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber( modid = Apocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class GatherDataListener {
    
    private static final RegistrySetBuilder REGISTRY_BUILDER = new RegistrySetBuilder()
            .add( Registries.DAMAGE_TYPE, ApocalypseDamageTypes::bootstrap )
            .add( ForgeRegistries.Keys.BIOME_MODIFIERS, ApocalypseBiomeModifiers::bootstrap );
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        DataGenerator dataGen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        if( event.includeServer() ) {
            dataGen.addProvider( true, new DatapackBuiltinEntriesProvider( dataGen.getPackOutput(), lookupProvider, REGISTRY_BUILDER, Set.of( Apocalypse.MOD_ID ) ) );
            dataGen.addProvider( true, new ApocalypseRecipeProvider( dataGen ) );
            dataGen.addProvider( true, new ApocalypseLootTableProvider( dataGen ) );
            dataGen.addProvider( true, new ApocalypseAdvancementProvider( dataGen, lookupProvider, fileHelper ) );
            BlockTagsProvider blockTagProvider = dataGen.addProvider( true, new ApocalypseBlockTagProvider( dataGen, lookupProvider, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseItemTagProvider( dataGen, lookupProvider, blockTagProvider.contentsGetter(), fileHelper ) );
            dataGen.addProvider( true, new ApocalypseEntityTagProvider( dataGen, lookupProvider, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseDamageTagProvider( dataGen, lookupProvider, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseLootModProvider( dataGen ) );
        }
        if( event.includeClient() ) {
            dataGen.addProvider( true, new ApocalypseBlockModelProvider( dataGen, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseItemModelProvider( dataGen, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseSoundProvider( dataGen, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseLangProvider( dataGen ) );
            dataGen.addProvider( true, new ApocalypseParticleProvider( dataGen, fileHelper ) );
        }
    }
    
    /** @return The given resource location, with the prefix and suffix of the given resource type merged onto it. */
    public static String toFilePath( ResourceLocation rl, ExistingFileHelper.IResourceType resourceType ) {
        return rl.getNamespace() + ":" + resourceType.getPrefix() + "/" + rl.getPath() + resourceType.getSuffix();
    }
    
    /**
     * Checks if the given resource location actually points to an existing file that
     * matches the provided resource type.
     *
     * @throws IllegalArgumentException if the file does not exist.
     */
    public static void assertFileExists( ResourceLocation texture, ExistingFileHelper fileHelper, ExistingFileHelper.IResourceType resourceType ) {
        if( !fileHelper.exists( texture, resourceType ) ) {
            final String filePath = toFilePath( texture, resourceType );
            throw new IllegalStateException( "Sound file at " + filePath + " does not exist" );
        }
    }
}
