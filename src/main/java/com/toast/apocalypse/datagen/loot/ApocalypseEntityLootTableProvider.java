package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.datagen.loot.LootEntryItemBuilder;
import fathertoast.crust.api.datagen.loot.LootHelper;
import fathertoast.crust.api.datagen.loot.LootPoolBuilder;
import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class ApocalypseEntityLootTableProvider extends EntityLootSubProvider {
    
    private final Set<EntityType<?>> knownEntities = new HashSet<>();
    
    protected ApocalypseEntityLootTableProvider( FeatureFlagSet flagSet ) { super( flagSet ); }
    
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() { return knownEntities.stream(); }
    
    @Override
    protected void add( EntityType<?> type, LootTable.Builder table ) {
        super.add( type, table );
        knownEntities.add( type );
    }
    
    protected <T extends Entity> void add( Supplier<EntityType<T>> type, LootTable.Builder table ) { add( type.get(), table ); }
    
    @Override
    public void generate() {
        // Lunar siege monsters
        
        add( ApocalypseEntities.GHOST, new LootTableBuilder()
                .addSemicommonDrop( "base", Items.EXPERIENCE_BOTTLE )
                .toLootTable() );
        
        add( ApocalypseEntities.GRUMP, new LootTableBuilder()
                .addSemicommonDrop( "base", Items.COOKIE )
                .addRareDrop( "rare", ItemTags.DECORATED_POT_SHERDS )
                .toLootTable() );
        
        add( ApocalypseEntities.SEEKER, new LootTableBuilder()
                .addCommonDrop( "base", Items.GUNPOWDER, 4 )
                .addUncommonDrop( "uncommon", Items.DRAGON_BREATH )
                .addRareDrop( "rare", Items.FLOWER_BANNER_PATTERN, Items.CREEPER_BANNER_PATTERN,
                        Items.SKULL_BANNER_PATTERN, Items.MOJANG_BANNER_PATTERN, Items.GLOBE_BANNER_PATTERN,
                        Items.PIGLIN_BANNER_PATTERN )
                .toLootTable() );
        
        add( ApocalypseEntities.DESTROYER, new LootTableBuilder()
                .addCommonDrop( "base", Items.GUNPOWDER, 6 )
                .addSemicommonDrop( "common", Items.DRAGON_BREATH )
                .addPool( new LootPoolBuilder( "uncommon" )
                        .addConditions( LootHelper.UNCOMMON_CONDITIONS )
                        .addEntry( new LootEntryItemBuilder( Items.BOOK ).enchant( 30, true ).toLootEntry() )
                        .toLootPool() )
                .toLootTable() );
        
        add( ApocalypseEntities.BREECHER, new LootTableBuilder()
                .addLootTable( "base", EntityType.CREEPER.getDefaultLootTable() )
                .addRareDrop( "rare", ItemTags.TRIM_TEMPLATES )
                .toLootTable() );
        
        
        // Other monsters
        
        add( ApocalypseEntities.FEARWOLF, new LootTableBuilder()
                .addCommonDrop( "base", Items.BONE, 2 )
                .addUncommonDrop( "uncommon", Items.EXPERIENCE_BOTTLE )
                .toLootTable() );
        
        add( ApocalypseEntities.SHADEFIEND, new LootTableBuilder()
                .addSemicommonDrop( "base", Items.PHANTOM_MEMBRANE )
                .toLootTable() );
    }
}