package com.toast.apocalypse.client.renderer.entity.living.breecher;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class BreecherRenderer extends CreeperRenderer {
    
    private static final ResourceLocation TEXTURE = Apocalypse.rl( "textures/entity/breecher/breecher.png" );
    
    public BreecherRenderer( EntityRendererProvider.Context context ) {
        super( context );
    }
    
    /** @return The texture to use when rendering this renderer's model. */
    @Override
    public ResourceLocation getTextureLocation( Creeper creeper ) {
        return TEXTURE;
    }
}
