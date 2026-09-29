package com.toast.apocalypse.datagen.particle;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ApocalypseParticleProvider extends AbstractParticleProvider {
    
    public ApocalypseParticleProvider( DataGenerator dataGen, ExistingFileHelper fileHelper ) {
        super( dataGen.getPackOutput(), Apocalypse.MOD_ID, fileHelper );
    }
    
    @Override
    protected void addParticles() {
        add( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE, spritesRanged( mcLoc( "generic_" ), 0, 7 ) );
    }
}
