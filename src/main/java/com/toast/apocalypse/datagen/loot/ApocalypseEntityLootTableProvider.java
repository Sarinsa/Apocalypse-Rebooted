package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

// TODO - Utilize crust's loot gen API, cause this hurts to look at
public class ApocalypseEntityLootTableProvider extends EntityLootSubProvider {
    
    private final Set<EntityType<?>> knownEntities = new HashSet<>();
    
    protected ApocalypseEntityLootTableProvider( FeatureFlagSet flagSet ) {
        super( flagSet );
    }
    
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return knownEntities.stream();
    }
    
    @Override
    protected void add( EntityType<?> type, LootTable.Builder table ) {
        super.add( type, table );
        knownEntities.add( type );
    }
    
    @Override
    public void generate() {
        add( ApocalypseEntities.GHOST.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1.0F ) )
                        .add( LootItem.lootTableItem( Items.EXPERIENCE_BOTTLE )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 1.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) )
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 1.0F ) ) ) ) ) );
        
        
        add( ApocalypseEntities.GRUMP.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( Items.COOKIE )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 3.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) )
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 1.0F ) ) ) ) ) );
        
        
        add( ApocalypseEntities.SEEKER.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( Items.GUNPOWDER )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 3.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) )
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 1.0F ) ) ) ) ) );
        
        
        add( ApocalypseEntities.DESTROYER.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( Items.GUNPOWDER )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 1.0F, 5.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) )
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 1.0F ) ) ) ) ) );
        
        
        add( ApocalypseEntities.BREECHER.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( Items.GUNPOWDER )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 1.0F, 4.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) )
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 0.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 1.0F ) ) ) ) ) );
        
        
        add( ApocalypseEntities.FEARWOLF.get(), LootTable.lootTable()
                .withPool( LootPool.lootPool()
                        .setRolls( ConstantValue.exactly( 1 ) )
                        .add( LootItem.lootTableItem( Items.BONE )
                                .apply( SetItemCountFunction.setCount( UniformGenerator.between( 1.0F, 2.0F ) ) )
                                .apply( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( 0.0F, 2.0F ) ) ) ) ) );
    }
}
