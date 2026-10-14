package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.client.ClientRegister;
import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.screen.misc.ApocalypseWCTab;
import com.toast.apocalypse.common.core.Apocalypse;
import fathertoast.crust.api.lib.CrustMath;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;

public class ClientMixinHooks {
    
    private static final ResourceLocation ACID_RAIN_TEXTURE = Apocalypse.rl( "textures/environment/acid_rain.png" );
    private static final ResourceLocation ACID_SNOW_TEXTURE = Apocalypse.rl( "textures/environment/acid_snow.png" );
    
    
    public static ResourceLocation getRenderSnowAndRainTexture( ResourceLocation originalTexture, Biome.Precipitation precipitation ) {
        if( !ClientUtil.isRainingAcid() || !ClientRegister.CLIENT_CONFIG.MISC.renderAcidRain.get() )
            return originalTexture;
        
        if( precipitation == Biome.Precipitation.RAIN ) return ACID_RAIN_TEXTURE;
        else if( precipitation == Biome.Precipitation.SNOW ) {
            return ClientUtil.acidSnowEnabled() ? ACID_SNOW_TEXTURE : originalTexture;
        }
        return originalTexture;
    }
    
    public static float getAcidRainColor( float originalValue, int index, Biome.Precipitation precipitation ) {
        if( !ClientUtil.isRainingAcid() || !ClientRegister.CLIENT_CONFIG.MISC.renderAcidRain.get() )
            return originalValue;
        if( precipitation == Biome.Precipitation.SNOW && !ClientUtil.acidSnowEnabled() )
            return originalValue;
        
        final int color = ClientUtil.getAcidRainColor();
        
        return switch( index ) {
            case 0 -> CrustMath.getRed( color );
            case 1 -> CrustMath.getGreen( color );
            case 2 -> CrustMath.getBlue( color );
            default -> originalValue;
        };
    }
    
    public static ParticleOptions getRainTickParticle( ClientLevel level, ParticleOptions originalParticle, FluidState fluidState ) {
        if( !ClientUtil.isRainingAcid() || !ClientRegister.CLIENT_CONFIG.MISC.renderAcidRain.get() )
            return originalParticle;
        
        return fluidState.is( FluidTags.WATER ) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
    }
    
    public static Tab[] createWorldScreenModifyTabs( Tab[] originalTabs ) {
        Tab apocalypseTab = new ApocalypseWCTab();
        
        Tab[] newTabs = new Tab[originalTabs.length + 1];
        
        System.arraycopy( originalTabs, 0, newTabs, 0, originalTabs.length );
        newTabs[newTabs.length - 1] = apocalypseTab;
        
        return newTabs;
    }
}
