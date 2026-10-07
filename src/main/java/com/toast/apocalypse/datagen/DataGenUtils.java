package com.toast.apocalypse.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/** Utility class containing miscellaneous helper methods related to data generation. */
public final class DataGenUtils {
    
    /** A logger instance named after this class. */
    private static final Logger LOGGER = LogManager.getLogger( DataGenUtils.class );
    
    
    /** @return The given resource location, with the prefix and suffix of the given resource type merged onto it. */
    public static String toFilePath( ResourceLocation rl, ExistingFileHelper.IResourceType resourceType ) {
        return rl.getNamespace() + ":" + resourceType.getPrefix() + "/" + rl.getPath() + resourceType.getSuffix();
    }
    
    /**
     * Checks if the given resource location actually points to an existing file that
     * matches the provided resource type and throws an exception if no file is found.
     *
     * @param resourceLocation The resource location to check.
     * @param fileHelper       The {@link ExistingFileHelper} instance to use to verify the file's existence.
     * @param resourceType     A resource type describing the type of file to look for, and in which data/resource base directory to look in.
     * @throws IllegalArgumentException if the file does not exist.
     */
    public static void assertFileExists( ResourceLocation resourceLocation, ExistingFileHelper fileHelper,
                                         ExistingFileHelper.IResourceType resourceType ) {
        if( !fileHelper.exists( resourceLocation, resourceType ) ) {
            final String filePath = toFilePath( resourceLocation, resourceType );
            throw new IllegalStateException( "File at " + filePath + " does not exist" );
        }
    }
    
    /**
     * Attempts to initialize all classes that belong to the specified namespace / mod ID.
     *
     * @param modId      The ID of the mod whose classes should be initialized.
     * @param exceptions A list of full class names of classes that should not be initialized. This can be empty, but not null.
     * @throws NullPointerException if {@code modId} or {@code exceptions} is null.
     */
    public static void initializeClasses( String modId, List<String> exceptions ) {
        Objects.requireNonNull( modId );
        Objects.requireNonNull( exceptions );
        // Iterate through all scanned classes
        for( ModFileScanData scanData : ModList.get().getAllScanData() ) {
            if( !modId.equals( scanData.getTargets().keySet().stream().findFirst().orElse( null ) ) )
                continue;
            
            for( ModFileScanData.ClassData classData : scanData.getClasses() ) {
                final String className = classData.clazz().getClassName();
                if( exceptions.contains( className ) ) continue;
                try {
                    Class.forName( className );
                }
                catch( ClassNotFoundException e ) {
                    LOGGER.error( "Encountered unknown class name while initializing classes for namespace '{}': {}", modId, className );
                }
            }
        }
    }
    
    // Utility class
    private DataGenUtils() { }
}
