package com.toast.apocalypse.common.item;

import com.google.common.base.Suppliers;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ApocalypseArmorMaterials implements ArmorMaterial {

    MIDNIGHT_STEEL(
            new int[] {165, 240, 225, 195},
            new int[] {2, 6, 5, 2},
            13,
            () -> SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()),
            name("midnight_steel"),
            0.0F,
            0.0F
    );


    ApocalypseArmorMaterials(int[] durability, int[] defense, int enchantmentValue,
                             Supplier<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
                             String name, float toughness, float knockbackResistance) {

        this.durability = durability;
        this.defense = defense;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
        this.name = name;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }
    private final int[] durability;
    private final int[] defense;
    private final int enchantmentValue;
    private final Supplier<SoundEvent> equipSound;
    private final Supplier<Ingredient> repairIngredient;
    private final String name;
    private final float toughness;
    private final float knockbackResistance;


    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> durability[0];
            case CHESTPLATE -> durability[1];
            case LEGGINGS -> durability[2];
            case BOOTS -> durability[3];
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> defense[0];
            case CHESTPLATE -> defense[1];
            case LEGGINGS -> defense[2];
            case BOOTS -> defense[3];
        };
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound.get();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    private static String name(String name) {
        return Apocalypse.MODID + ":" + name;
    }
}
