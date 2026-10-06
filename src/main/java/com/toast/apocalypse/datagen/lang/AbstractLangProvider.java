package com.toast.apocalypse.datagen.lang;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractLangProvider implements DataProvider {
    
    private static final String defaultLocale = "en_us";
    
    /** A map of deferred registries to auto-generate English translations for. */
    private final Map<DeferredRegister<?>, Function<RegistryObject<Object>, String>> autoGenRegistries = new HashMap<>();
    /** A map of registry key lists to auto-generate English translations for. */
    private final Map<List<ResourceKey<?>>, Function<ResourceKey<?>, String>> autoGenRegistryKeys = new HashMap<>();
    
    /** A map containing all translation keys and their translation strings. */
    private final Map<String, String> translations = new HashMap<>();
    /** The pack output this provider uses when saving. */
    protected final PackOutput output;
    /** The ID of the mod to run this provider for. */
    protected final String modId;
    
    
    public AbstractLangProvider( PackOutput output, String modId ) {
        this.output = output;
        this.modId = modId;
        TranslationKey.initializeClasses( modId );
    }
    
    /** Called first when adding language entries, before {@link #autoGenForRegistries()} and {@link #autoGenForRegistryKeys()}. */
    protected abstract void addTranslations();
    
    /** Runs this data provider. */
    @Override // DataProvider
    public CompletableFuture<?> run( CachedOutput cache ) {
        return CompletableFuture.supplyAsync( () -> {
            addTranslations();
            autoGenForRegistries();
            autoGenForRegistryKeys();
            
            if( !translations.isEmpty() )
                return save( cache, output.getOutputFolder( PackOutput.Target.RESOURCE_PACK )
                        .resolve( modId ).resolve( "lang" ).resolve( defaultLocale + ".json" ) );
            return CompletableFuture.allOf();
        } );
    }
    
    /** @return A string that identifies this provider. */
    @Override // DataProvider
    public String getName() {
        return "Language: " + modId;
    }
    
    /** Writes the contents of {@link #translations} to JSON. */
    private CompletableFuture<?> save( CachedOutput cache, Path target ) {
        JsonObject json = new JsonObject();
        translations.forEach( json::addProperty );
        
        return DataProvider.saveStable( cache, json, target );
    }
    
    //---------------------------------------------------------------------------------
    //                                ADD TYPE SPECIFIC
    //---------------------------------------------------------------------------------
    
    public void addBlock( Supplier<? extends Block> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( Block key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    public void addItem( Supplier<? extends Item> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( Item key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    public void addItemStack( Supplier<ItemStack> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( ItemStack key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    public void addEnchantment( Supplier<? extends Enchantment> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( Enchantment key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    public void addEffect( Supplier<? extends MobEffect> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( MobEffect key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    public void addEntityType( Supplier<? extends EntityType<?>> key, String name ) {
        add( key.get(), name );
    }
    
    public void add( EntityType<?> key, String name ) {
        add( key.getDescriptionId(), name );
    }
    
    /**
     * Adds death message translations for a damage type.
     *
     * @param damageTypeId The registry ID of the damage type.
     * @param normalMsg    The death message to display if the death was "normal".
     * @param assistedMsg  The death message to display if an attacker was involved, but they did not deal the killing blow.
     * @param itemMsg      The death message to display if the attacker was using a named item to deal the killing blow.
     */
    public void addDamageType( ResourceKey<DamageType> damageTypeId, @Nullable String normalMsg, @Nullable String assistedMsg, @Nullable String itemMsg ) {
        final String name = damageTypeId.location().getPath();
        
        if( normalMsg != null ) {
            String key = "death.attack." + modId + "." + name;
            add( key, normalMsg );
        }
        if( assistedMsg != null ) {
            String key = "death.attack." + modId + "." + name + ".player";
            add( key, assistedMsg );
        }
        if( itemMsg != null ) {
            String key = "death.attack." + modId + "." + name + ".item";
            add( key, itemMsg );
        }
    }
    
    /**
     * Adds the given translation key and translation string pair to the translations map.
     *
     * @throws IllegalStateException If the key already exists in the map.
     */
    public void add( String key, String value ) {
        if( translations.put( key, value ) != null )
            throw new IllegalStateException( "Duplicate translation key: " + key );
    }
    
    
    //---------------------------------------------------------------------------------
    //                                  AUTO GEN
    //---------------------------------------------------------------------------------
    
    /** Adds a deferred register to the map of registries to auto-generate English translations for. */
    protected <T> void addRegistry( DeferredRegister<T> register, Function<RegistryObject<T>, String> keyMapper ) {
        // noinspection unchecked
        autoGenRegistries.put( register, ( regObj ) -> keyMapper.apply( (RegistryObject<T>) regObj ) );
    }
    
    /** Adds a list of registry keys to the map of resource keys to auto-generate English translations for. */
    protected void addRegistryKeys( List<ResourceKey<?>> registryKeys, Function<ResourceKey<?>, String> keyMapper ) {
        autoGenRegistryKeys.put( registryKeys, keyMapper );
    }
    
    /**
     * Loops through all registry objects contained in each registry in the {@link #autoGenRegistries} map
     * and generates an English translation for each registry object's registry ID.
     */
    @SuppressWarnings( "unchecked" )
    private void autoGenForRegistries() {
        autoGenRegistries.forEach( ( register, func ) -> {
            for( RegistryObject<?> regObj : register.getEntries() ) {
                String key = func.apply( (RegistryObject<Object>) regObj );
                if( translations.containsKey( key ) ) continue;
                assert regObj.getId() != null;
                add( key, fromIdToEnglish( regObj.getId() ) );
            }
        } );
    }
    
    /**
     * Loops through all registry keys contained in each registry key list in the {@link #autoGenRegistryKeys} map
     * and generates an English translation based on each registry key.
     */
    private void autoGenForRegistryKeys() {
        autoGenRegistryKeys.forEach( ( resourceKeys, func ) -> {
            for( ResourceKey<?> regKey : resourceKeys ) {
                String key = func.apply( regKey );
                if( translations.containsKey( key ) ) continue;
                add( func.apply( regKey ), fromIdToEnglish( regKey.location() ) );
            }
        } );
    }
    
    /**
     * Grabs the path string of the given resource location and processes it into standard format Minecraft English,
     * assuming the registry names are composed of English words.
     */
    @SuppressWarnings( "deprecation" )
    private String fromIdToEnglish( ResourceLocation id ) {
        String translation = id.getPath().replaceAll( "_", " " );
        translation = WordUtils.capitalizeFully( translation );
        return translation;
    }
}
