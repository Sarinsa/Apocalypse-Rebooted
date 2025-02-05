package com.toast.apocalypse.common.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class LunarArmorItem extends ArmorItem {

    public LunarArmorItem(ArmorItem.Type type) {
        super(ApocalypseArmorMaterials.MIDNIGHT_STEEL, type, new Item.Properties());
    }
}
