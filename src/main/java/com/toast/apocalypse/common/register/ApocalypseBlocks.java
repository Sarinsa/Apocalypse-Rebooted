package com.toast.apocalypse.common.register;

import com.toast.apocalypse.common.block.BouncyBlock;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Apocalypse.MODID);

    public static final RegistryObject<Block> BOUNCY_BLOCK = registerBlock("bouncy_block", BouncyBlock::new);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<T> blockRegistryObject = BLOCKS.register(name, blockSupplier);
        ApocalypseItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()));
        return blockRegistryObject;
    }
}
