package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.Arrays;

public class ApocalypseLootModProvider extends GlobalLootModifierProvider {
    
    public ApocalypseLootModProvider( DataGenerator gen ) {
        super( gen.getPackOutput(), Apocalypse.MOD_ID );
    }
    
    @Override
    protected void start() {
        add( "fatherly_toast", new SimpleAddLootModifier(
                new LootItemCondition[] {},
                ApocalypseObjects.Items.FATHERLY_TOAST.get(),
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
