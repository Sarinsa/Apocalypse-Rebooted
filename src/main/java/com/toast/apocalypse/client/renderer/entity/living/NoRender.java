package com.toast.apocalypse.client.renderer.entity.living;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

/** Used for entities that should not render or for testing out entities that does not have a model yet. */
@SuppressWarnings( "unused" )
public class NoRender<T extends Entity> extends EntityRenderer<T> {
    
    public NoRender( EntityRendererProvider.Context context ) {
        super( context );
    }
    
    @Override
    @Nonnull
    public ResourceLocation getTextureLocation( @Nonnull T entity ) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
