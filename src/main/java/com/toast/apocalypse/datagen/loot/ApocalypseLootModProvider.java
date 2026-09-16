package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import fathertoast.crust.api.datagen.loot.LootHelper;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class ApocalypseLootModProvider extends GlobalLootModifierProvider {
    
    public ApocalypseLootModProvider( DataGenerator dataGen ) {
        super( dataGen.getPackOutput(), Apocalypse.MOD_ID );
    }
    
    @Override
    protected void start() {
        add( "fatherly_toast", new SimpleAddLootModifier(
                new LootItemCondition[] {},
                ApocalypseObjects.Items.FATHERLY_TOAST.get(),
                0.05, 1, 6,
                Arrays.asList(
                        ResourceLocation.withDefaultNamespace( "chests/simple_dungeon" ),
                        ResourceLocation.withDefaultNamespace( "chests/desert_pyramid" ),
                        ResourceLocation.withDefaultNamespace( "chests/jungle_temple" ),
                        ResourceLocation.withDefaultNamespace( "chests/abandoned_mineshaft" )
                )
        ) );
        
        add( "fragmented_soul", new SimpleAddLootModifier(
                makeSiegeMobDropConditions(),
                ApocalypseObjects.Items.FRAGMENTED_SOUL.get(),
                /* Chance handled by conditions */ 1.0, 1, 1
        ) );
    }
    
    private static LootItemCondition[] makeSiegeMobDropConditions() {
        final ArrayList<LootItemCondition> list = new ArrayList<>();
        
        // Makes this a standard "uncommon" drop
        Arrays.asList( LootHelper.UNCOMMON_CONDITIONS ).forEach( condition -> list.add( condition.build() ) );
        
        // Check if the entity was spawned by a lunar siege event
        CompoundTag tag = new CompoundTag();
        NBTHelper.getOrCreateCompound( NBTHelper.getOrCreateCompound( tag, "ForgeData" ), Apocalypse.MOD_ID )
                .putByte( ApocalypseObjects.Items.TAG_CAN_DROP_FRAGMENTED_SOUL, (byte) 1 );
        list.add( LootItemEntityPropertyCondition.hasProperties( LootContext.EntityTarget.THIS,
                EntityPredicate.Builder.entity().nbt( new NbtPredicate( tag ) ) ).build() );
        
        return list.toArray( new LootItemCondition[0] );
    }
}