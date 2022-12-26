package com.toast.apocalypse.common.core.register;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.item.BucketHelmetItem;
import com.toast.apocalypse.common.item.FatherlyToastItem;
import com.toast.apocalypse.common.item.MobWikiBookItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Apocalypse.MODID);


    public static final RegistryObject<Item> FRAGMENTED_SOUL = registerSimpleItem("fragmented_soul", ItemGroup.TAB_MATERIALS);
    public static final RegistryObject<Item> MIDNIGHT_STEEL_INGOT = registerSimpleItem("midnight_steel_ingot", ItemGroup.TAB_MATERIALS);
    public static final RegistryObject<Item> FATHERLY_TOAST = registerItem("fatherly_toast", FatherlyToastItem::new);
    public static final RegistryObject<Item> BUCKET_HELM = registerItem("bucket_helm", BucketHelmetItem::new);
    public static final RegistryObject<Item> LUNAR_CLOCK = registerItem("lunar_clock", () -> new Item(new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)));
    public static final RegistryObject<Item> APOCALYPSE_COMPENDIUM = registerItem("apocalypse_compendium", MobWikiBookItem::new);
    public static final RegistryObject<ForgeSpawnEggItem> GHOST_SPAWN_EGG = registerSpawnEgg("ghost", ApocalypseEntities.GHOST, 0xBCBCBC, 0x708899);
    public static final RegistryObject<ForgeSpawnEggItem> DESTROYER_SPAWN_EGG = registerSpawnEgg("destroyer", ApocalypseEntities.DESTROYER, 0x877B6F, 0x912820);
    public static final RegistryObject<ForgeSpawnEggItem> SEEKER_SPAWN_EGG = registerSpawnEgg("seeker", ApocalypseEntities.SEEKER, 0x766F87, 0x912820);
    public static final RegistryObject<ForgeSpawnEggItem> GRUMP_SPAWN_EGG = registerSpawnEgg("grump", ApocalypseEntities.GRUMP, 0xF9F9F9, 0x2D41F4);
    public static final RegistryObject<ForgeSpawnEggItem> BREECHER_SPAWN_EGG = registerSpawnEgg("breecher", ApocalypseEntities.BREECHER, 0x0DA70B, 0xF9F9F9);
    public static final RegistryObject<ForgeSpawnEggItem> FEARWOLF_SPAWN_EGG = registerSpawnEgg("fearwolf", ApocalypseEntities.FEARWOLF, 0x222127, 0x912820);

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

    private static RegistryObject<Item> registerSimpleItem(String name, ItemGroup itemGroup) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().tab(itemGroup)));
    }

    private static <E extends LivingEntity> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(String name, Supplier<EntityType<E>> entityTypeSupplier, int backgroundColor, int highlightColor) {
        return ITEMS.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityTypeSupplier, backgroundColor, highlightColor, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    }

    public static void onMissingMappings(RegistryEvent.MissingMappings<Item> event) {
        ImmutableList<RegistryEvent.MissingMappings.Mapping<Item>> mappings = event.getMappings(Apocalypse.MODID);


        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : mappings) {
            if (mapping.key.getPath().equals("lunarium_ingot")) {
                mapping.remap(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get());
            }
            else if (mapping.key.getPath().equals("soul_fragment")) {
                mapping.remap(ApocalypseItems.FRAGMENTED_SOUL.get());
            }
        }
    }
}
