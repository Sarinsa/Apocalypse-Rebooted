package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.toast.apocalypse.client.ApocalypseModelLayers;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GhostRenderer<T extends Ghost> extends MobRenderer<T, GhostModel<T>> {
    
    protected static final ResourceLocation GHOST_TEXTURE = Apocalypse.rl( "textures/entity/ghost/ghost.png" );
    
    public GhostRenderer( EntityRendererProvider.Context context ) {
        super( context, new GhostModel<>( context.bakeLayer( ApocalypseModelLayers.GHOST ) ), 0.0F );
        addLayer( new GhostEyesLayer<>( this ) );
        addLayer( new GhostFrozenLayer<>( this ) );
    }
    
    /** @return The texture to use when rendering this renderer's model. */
    @Override
    public ResourceLocation getTextureLocation( T ghost ) {
        return GHOST_TEXTURE;
    }
}
