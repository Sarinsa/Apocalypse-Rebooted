package com.toast.apocalypse.datagen;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.List;
import java.util.Objects;

/** Utility class containing miscellaneous helper methods related to data generation. */
public final class DataGenUtils {
    
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
    
    /**
     * Attempts to initialize all classes that belong to the specified namespace / mod ID.
     *
     * @param modId      The ID of the mod whose classes should be initialized.
     * @param exceptions A list of full class names of classes that should not be initialized. This can be empty, but not null.
     * @throws NullPointerException if {@code modId} is null.
     * @throws ReportedException    if something else goes wrong, such as looking up a class that does not exist.
     */
    public static void initializeClasses( String modId, List<String> exceptions ) {
        Objects.requireNonNull( modId );
        // Iterate through all scanned classes
        for( ModFileScanData scanData : ModList.get().getAllScanData() ) {
            if( !modId.equals( scanData.getTargets().keySet().stream().findFirst().orElse( null ) ) )
                continue;
            
            for( ModFileScanData.ClassData classData : scanData.getClasses() ) {
                final String className = classData.clazz().getClassName();
                try {
                    Class.forName( className );
                }
                catch( Throwable throwable ) {
                    CrashReport report = CrashReport.forThrowable( throwable, "Exception while trying to initialize classes!" );
                    CrashReportCategory category = report.addCategory( "Initialization details" );
                    category.setDetail( "Name of unfound class", className );
                    category.setDetail( "Mod ID associated with class", modId );
                    category.setDetail( "Avoided classes", exceptions );
                    throw new ReportedException( report );
                }
            }
        }
    }
    
    // Utility class
    private DataGenUtils() { }
}
