package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.Arrays;

public class ApocalypseLootModProvider extends GlobalLootModifierProvider {
    
    public ApocalypseLootModProvider( DataGenerator gen ) {
        super( gen.getPackOutput(), Apocalypse.MODID );
    }
    
    @Override
    protected void start() {
        add( "fatherly_toast", new SimpleAddLootModifier(
                new LootItemCondition[] {},
                ApocalypseItems.FATHERLY_TOAST.get(),
                0.3D,
                1,
                6,
                Arrays.asList(
                        ResourceLocation.withDefaultNamespace( "chests/simple_dungeon" ),
                        ResourceLocation.withDefaultNamespace( "chests/desert_pyramid" ),
                        ResourceLocation.withDefaultNamespace( "chests/jungle_temple" ),
                        ResourceLocation.withDefaultNamespace( "chests/abandoned_mineshaft" )
                ) )
        );
    }
}
