package com.toast.apocalypse.common.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.item.BucketHelmetItem;
import com.toast.apocalypse.common.item.FatherlyToastItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Apocalypse.MODID);


    public static final RegistryObject<Item> SOUL_FRAGMENT = registerSimpleItem("soul_fragment", ItemGroup.TAB_MATERIALS);
    public static final RegistryObject<Item> LUNARIUM_INGOT = registerSimpleItem("lunarium_ingot", ItemGroup.TAB_MATERIALS);
    public static final RegistryObject<Item> FATHERLY_TOAST = registerItem("fatherly_toast", FatherlyToastItem::new);
    public static final RegistryObject<Item> BUCKET_HELM = registerItem("bucket_helm", BucketHelmetItem::new);
    public static final RegistryObject<Item> LUNAR_CLOCK = registerSimpleItem("lunar_clock", ItemGroup.TAB_TOOLS);
    public static final RegistryObject<SpawnEggItem> GHOST_SPAWN_EGG = registerSpawnEgg("ghost", ApocalypseEntities.GHOST_TYPE, 0xBCBCBC, 0x708899);
    public static final RegistryObject<SpawnEggItem> DESTROYER_SPAWN_EGG = registerSpawnEgg("destroyer", ApocalypseEntities.DESTROYER_TYPE, 0x7D7D7D, 0xA80E0E);
    public static final RegistryObject<SpawnEggItem> SEEKER_SPAWN_EGG = registerSpawnEgg("seeker", ApocalypseEntities.SEEKER_TYPE, 0xF9F9F9, 0xA80E0E);
    public static final RegistryObject<SpawnEggItem> GRUMP_SPAWN_EGG = registerSpawnEgg("grump", ApocalypseEntities.GRUMP_TYPE, 0xF9F9F9, 0x2D41F4);
    public static final RegistryObject<SpawnEggItem> BREECHER_SPAWN_EGG = registerSpawnEgg("breecher", ApocalypseEntities.BREECHER_TYPE, 0x0DA70B, 0xF9F9F9);

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

    private static RegistryObject<Item> registerSimpleItem(String name, ItemGroup itemGroup) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().tab(itemGroup)));
    }

    private static <E extends LivingEntity> RegistryObject<SpawnEggItem> registerSpawnEgg(String name, EntityType<E> entityType, int primaryColor, int secondaryColor) {
        return ITEMS.register(name + "_spawn_egg", () -> new SpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    }
}
