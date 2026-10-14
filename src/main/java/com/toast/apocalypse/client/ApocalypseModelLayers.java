package com.toast.apocalypse.client;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class ApocalypseModelLayers {
    
    // Entities
    public static final ModelLayerLocation GHOST = create( "ghost" );
    public static final ModelLayerLocation DESTROYER = create( "destroyer" );
    public static final ModelLayerLocation SEEKER = create( "seeker" );
    public static final ModelLayerLocation GRUMP = create( "grump" );
    public static final ModelLayerLocation BREECHER = create( "breecher" );
    public static final ModelLayerLocation FEARWOLF = create( "fearwolf" );
    public static final ModelLayerLocation SHADEFIEND = create( "shadefiend" );
    
    // Armor & equipment
    public static final ModelLayerLocation BUCKET_HELMET = create( "bucket_helmet" );
    public static final ModelLayerLocation GRUMP_BUCKET_HELMET = create( "grump_bucket_helmet" );
    
    
    /** @return A new model layer location named "main" with the given path. */
    private static ModelLayerLocation create( String path ) {
        return create( path, "main" );
    }
    
    /** @return A new model layer location of the given path and layer name. */
    @SuppressWarnings( "SameParameterValue" )
    private static ModelLayerLocation create( String path, String layerName ) {
        return new ModelLayerLocation( Apocalypse.rl( path ), layerName );
    }
    
    // No instantiation
    private ApocalypseModelLayers() { }
}
