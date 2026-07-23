package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.item.BucketHelmetItem;
import com.toast.apocalypse.common.item.FatherlyToastItem;
import com.toast.apocalypse.common.item.LunarArmorItem;
import com.toast.apocalypse.common.item.MobWikiBookItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public final class ApocalypseItems {
    
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, Apocalypse.MOD_ID );
    
    public static final Map<ResourceKey<CreativeModeTab>, List<RegistryObject<? extends Item>>> TAB_ITEMS = new HashMap<>();
    
    static {
        registerSimple( ApocalypseObjects.Items.FRAGMENTED_SOUL, CreativeModeTabs.INGREDIENTS );
        registerSimple( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT, CreativeModeTabs.INGREDIENTS );
        register( ApocalypseObjects.Items.FATHERLY_TOAST, FatherlyToastItem::new, CreativeModeTabs.FOOD_AND_DRINKS );
        register( ApocalypseObjects.Items.BUCKET_HELM, BucketHelmetItem::new, CreativeModeTabs.COMBAT );
        register( ApocalypseObjects.Items.MIDNIGHT_STEEL_HELMET, () -> new LunarArmorItem( ArmorItem.Type.HELMET ), CreativeModeTabs.COMBAT );
        register( ApocalypseObjects.Items.MIDNIGHT_STEEL_CHESTPLTAE, () -> new LunarArmorItem( ArmorItem.Type.CHESTPLATE ), CreativeModeTabs.COMBAT );
        register( ApocalypseObjects.Items.MIDNIGHT_STEEL_LEGGINGS, () -> new LunarArmorItem( ArmorItem.Type.LEGGINGS ), CreativeModeTabs.COMBAT );
        register( ApocalypseObjects.Items.MIDNIGHT_STEEL_BOOTS, () -> new LunarArmorItem( ArmorItem.Type.BOOTS ), CreativeModeTabs.COMBAT );
        register( ApocalypseObjects.Items.LUNAR_CLOCK, () -> new Item( new Item.Properties().stacksTo( 1 ) ), CreativeModeTabs.TOOLS_AND_UTILITIES );
        register( ApocalypseObjects.Items.APOCALYPSE_COMPENDIUM, MobWikiBookItem::new, CreativeModeTabs.TOOLS_AND_UTILITIES );
        register( ApocalypseObjects.Items.WET_TORCH, () -> new StandingAndWallBlockItem( ApocalypseObjects.Blocks.WET_TORCH.get(), ApocalypseObjects.Blocks.WET_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN ) );
        
        registerSpawnEgg( ApocalypseObjects.Items.GHOST_SPAWN_EGG, ApocalypseEntities.GHOST, 0xBCBCBC, 0x708899 );
        registerSpawnEgg( ApocalypseObjects.Items.DESTROYER_SPAWN_EGG, ApocalypseEntities.DESTROYER, 0x877B6F, 0x912820 );
        registerSpawnEgg( ApocalypseObjects.Items.SEEKER_SPAWN_EGG, ApocalypseEntities.SEEKER, 0x766F87, 0x912820 );
        registerSpawnEgg( ApocalypseObjects.Items.GRUMP_SPAWN_EGG, ApocalypseEntities.GRUMP, 0xF9F9F9, 0x2D41F4 );
        registerSpawnEgg( ApocalypseObjects.Items.BREECHER_SPAWN_EGG, ApocalypseEntities.BREECHER, 0x0DA70B, 0xF9F9F9 );
        registerSpawnEgg( ApocalypseObjects.Items.FEARWOLF_SPAWN_EGG, ApocalypseEntities.FEARWOLF, 0x222127, 0x912820 );
        registerSpawnEgg( ApocalypseObjects.Items.SHADEFIEND_SPAWN_EGG, ApocalypseEntities.SHADEFIEND, 0x1D1D1D, 0x292929 );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an item to the deferred register. */
    @SafeVarargs
    private static void register( RegistryObject<Item> regObj, Supplier<Item> supplier, ResourceKey<CreativeModeTab>... creativeTabs ) {
        queueForCreativeTabs( REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier ), creativeTabs );
    }
    
    /** Registers a simple block item for the given block to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    @SafeVarargs
    static void registerBlockItem( RegistryObject<Block> regObj, ResourceKey<CreativeModeTab>... creativeTabs ) {
        RegistryObject<Item> itemRegObj = REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> new BlockItem( regObj.get(), new Item.Properties() ) );
        queueForCreativeTabs( itemRegObj, creativeTabs );
    }
    
    /** Registers a simple item to the deferred register. */
    @SafeVarargs
    private static void registerSimple( RegistryObject<Item> regObj, ResourceKey<CreativeModeTab>... creativeTabs ) {
        queueForCreativeTabs( REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> new Item( new Item.Properties() ) ), creativeTabs );
    }
    
    /** Registers a spawn egg item for the given entity type. */
    private static <T extends Mob> void registerSpawnEgg( RegistryObject<Item> regObj, RegistryObject<EntityType<T>> entityTypeSupplier, int backgroundColor, int highlightColor ) {
        try {
            REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(),
                    () -> new ForgeSpawnEggItem( entityTypeSupplier, backgroundColor, highlightColor, new Item.Properties() ) );
            queueForCreativeTabs( regObj, CreativeModeTabs.SPAWN_EGGS );
        }
        catch( ClassCastException e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    /** Enqueues the specified item for being added to X creative mode tabs. */
    @SafeVarargs
    private static void queueForCreativeTabs( RegistryObject<? extends Item> item, ResourceKey<CreativeModeTab>... creativeTabs ) {
        for( ResourceKey<CreativeModeTab> tab : creativeTabs ) {
            if( !TAB_ITEMS.containsKey( tab ) ) {
                List<RegistryObject<? extends Item>> list = new ArrayList<>();
                list.add( item );
                TAB_ITEMS.put( tab, list );
            }
            else {
                TAB_ITEMS.get( tab ).add( item );
            }
        }
    }
    
    /** Called when creative mode tabs are populated. */
    public static void onCreativeTabPopulate( BuildCreativeModeTabContentsEvent event ) {
        if( TAB_ITEMS.containsKey( event.getTabKey() ) ) {
            List<RegistryObject<? extends Item>> items = TAB_ITEMS.get( event.getTabKey() );
            items.forEach( ( regObj ) -> event.accept( regObj.get() ) );
        }
    }
    
    /** Called when missing mappings are found. */
    public static void onMissingMappings( MissingMappingsEvent event ) {
        List<MissingMappingsEvent.Mapping<Item>> mappings = event.getMappings( ForgeRegistries.ITEMS.getRegistryKey(), Apocalypse.MOD_ID );
        
        for( MissingMappingsEvent.Mapping<Item> mapping : mappings ) {
            if( mapping.getKey().getPath().equals( "lunarium_ingot" ) ) {
                mapping.remap( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT.get() );
            }
            else if( mapping.getKey().getPath().equals( "soul_fragment" ) ) {
                mapping.remap( ApocalypseObjects.Items.FRAGMENTED_SOUL.get() );
            }
        }
    }
    
    
    private ApocalypseItems() { }
}
