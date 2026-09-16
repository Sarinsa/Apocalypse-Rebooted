package com.toast.apocalypse.datagen.lang;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

// TODO
public class ApocalypseLangProvider extends LanguageProvider {
    
    public ApocalypseLangProvider( DataGenerator gen ) {
        super( gen.getPackOutput(), Apocalypse.MOD_ID, "en_us" );
    }
    
    @Override
    protected void addTranslations() {
    }
}
