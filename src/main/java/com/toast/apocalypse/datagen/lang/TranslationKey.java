package com.toast.apocalypse.datagen.lang;

import fathertoast.crust.api.config.common.ConfigUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.ModFileScanData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * Holds a translation key and provides some methods for easily
 * creating translation components from it.
 */
// TODO - If we end up moving this to Crust, we might want to
//        make it so translations for multiple languages can be appended to a key object,
//        in a builder style maybe.
public class TranslationKey {
    
    /** A map of mod IDs linked to a map of all known keys and their translation strings. */
    private static final Map<String, Map<String, String>> ALL_KEYS = new HashMap<>();
    
    
    /**
     * Attempts to initialize all classes that belong to the specified namespace / mod ID
     * to ensure all static instances of this class are initialized before language data gen starts.
     */
    public static void initializeClasses( String modId ) {
        // Iterate through all scanned classes
        for( ModFileScanData scanData : ModList.get().getAllScanData() ) {
            if( !modId.equals( scanData.getTargets().keySet().stream().findFirst().orElse( null ) ) )
                continue;
            
            for( ModFileScanData.ClassData classData : scanData.getClasses() ) {
                final String className = classData.clazz().getClassName();
                try {
                    Class.forName( className );
                }
                catch( ClassNotFoundException e ) {
                    ConfigUtil.LOG.error( "Failed to initialize class '{}'!", className );
                    if( !FMLEnvironment.production ) {
                        // noinspection CallToPrintStackTrace
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Creates a new instance from the given mod ID, key and translation string
     * and adds it to the internal map of all known keys.
     *
     * @param modId       The ID of the mod that is adding a translation key.
     * @param key         The actual translation key string.
     * @param translation The English translation of the key.
     * @return The created translation key instance.
     */
    public static TranslationKey of( String modId, String key, String translation ) {
        final TranslationKey translationKey = new TranslationKey( key );
        if( !ALL_KEYS.containsKey( modId ) ) {
            ALL_KEYS.put( modId, new Object2ObjectOpenHashMap<>() );
        }
        ALL_KEYS.get( modId ).put( key, translation );
        return translationKey;
    }
    
    /**
     * Performs the specified action for every key-translation map inside the {@link #ALL_KEYS} map.
     * This is primarily used for data generation.
     *
     * @param modId  The ID of the mod whose key-translation map should be iterated through. If this is null,
     *               every map will be iterated through.
     * @param action The action to perform for each entry in the key-translation map(s).
     */
    @SuppressWarnings( "unused" )
    public static void forAllKnownKeys( @Nullable String modId, BiConsumer<String, String> action ) {
        if( modId != null && ALL_KEYS.containsKey( modId ) ) {
            ALL_KEYS.get( modId ).forEach( action );
        }
        else {
            for( Map<String, String> map : ALL_KEYS.values() ) {
                map.forEach( action );
            }
        }
    }
    
    /** The actual translation key string. */
    private final String key;
    
    
    /** Internal constructor, use {@link TranslationKey#of(String, String, String)} instead. */
    private TranslationKey( String key ) {
        this.key = key;
    }
    
    /** @return A new translatable component from this translation key and the given arguments. */
    public MutableComponent withArgs( Object... args ) {
        return Component.translatable( key, args );
    }
    
    /** @return A new translatable component from this translation key, with the given style. */
    public MutableComponent withStyle( Style style ) {
        return get().withStyle( style );
    }
    
    /** @return A new translatable component from this translation key, with the given chat formatting. */
    public MutableComponent withStyle( ChatFormatting chatFormatting ) {
        return get().withStyle( chatFormatting );
    }
    
    /** @return A new translatable component from this translation key, with the given chat formatting(s). */
    public MutableComponent withStyle( ChatFormatting... chatFormatting ) {
        return get().withStyle( chatFormatting );
    }
    
    /** @return A new translatable component from this translation key, with the resulting style of the given operator. */
    public MutableComponent withStyle( UnaryOperator<Style> styleOperator ) {
        return get().withStyle( styleOperator );
    }
    
    /** @return A new translatable component from this translation key. */
    public MutableComponent get() {
        return Component.translatable( key );
    }
    
    /** @return This instance's translation key. */
    public String getKey() {
        return key;
    }
}
