package com.toast.apocalypse.datagen.lang;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.*;
import net.minecraft.data.DataGenerator;

public class ApocalypseLangProvider extends AbstractLangProvider {
    
    
    public ApocalypseLangProvider( DataGenerator gen ) {
        super( gen.getPackOutput(), Apocalypse.MOD_ID );
        addRegistry( ApocalypseItems.REGISTRY, regObj -> regObj.get().getDescriptionId() );
        addRegistry( ApocalypseBlocks.REGISTRY, regObj -> regObj.get().getDescriptionId() );
        addRegistry( ApocalypseEntities.REGISTRY, regObj -> regObj.get().getDescriptionId() );
        addRegistry( ApocalypseTrapTypes.REGISTRY, regObj -> regObj.get().getTranslationKey() );
        
        addDamageType( ApocalypseDamageTypes.ACID_RAIN,
                "%s was sprinkled to death in the acidic rain",
                "%s melted in acid rain whilst trying to escape %s", null );
        addDamageType( ApocalypseDamageTypes.LIGHT_INTOLERANCE,
                "%s perished in the light",
                "%s was obliterated by photons whilst trying to escape %s", null );
    }
    
    @Override
    protected void addTranslations() {
        // Custom translation keys
        TranslationKey.forAllKnownKeys( modId, this::add );
    }
}
