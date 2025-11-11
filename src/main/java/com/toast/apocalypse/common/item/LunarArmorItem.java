package com.toast.apocalypse.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.toast.apocalypse.common.item.ApocalypseArmorMaterials.MIDNIGHT_STEEL;

public class LunarArmorItem extends ArmorItem {
    
    public static final String MOD_DATA_KEY = "ApocalypseModData";
    public static final String LUNAR_INDEX_KEY = "LunarArmorIndex";
    
    public static final int MIN_INDEX = -1;
    public static final int MIN_SPORADIC_INDEX = 1;
    public static final int MAX_INDEX = 10;
    
    private final Map<Integer, Multimap<Attribute, AttributeModifier>> FULL_MOON_MODIFIERS = new HashMap<>();
    private final Multimap<Attribute, AttributeModifier> NEW_MOON_MODIFIERS;
    
    
    public LunarArmorItem( ArmorItem.Type type ) {
        super( MIDNIGHT_STEEL, type, new Item.Properties() );
        
        // New moon modifiers
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get( type );
        builder.put( Attributes.ARMOR, new AttributeModifier( uuid, "Armor modifier", (double) MIDNIGHT_STEEL.getDefenseForType( type ) / 2, AttributeModifier.Operation.ADDITION ) );
        builder.put( Attributes.ARMOR_TOUGHNESS, new AttributeModifier( uuid, "Armor toughness", MIDNIGHT_STEEL.getToughness(), AttributeModifier.Operation.ADDITION ) );
        
        if( knockbackResistance > 0 ) {
            builder.put( Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier( uuid, "Armor knockback resistance", knockbackResistance / 2, AttributeModifier.Operation.ADDITION ) );
        }
        NEW_MOON_MODIFIERS = builder.build();
        
        createFullMoonModifiers( type );
    }
    
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers( EquipmentSlot slot, ItemStack stack ) {
        if( getEquipmentSlot() != slot ) return ImmutableMultimap.of();
        
        int index = 0;
        
        if( stack.getTag() != null && stack.getTag().contains( MOD_DATA_KEY, Tag.TAG_COMPOUND ) ) {
            CompoundTag modData = stack.getTag().getCompound( MOD_DATA_KEY );
            
            if( modData.contains( LUNAR_INDEX_KEY, Tag.TAG_INT ) ) {
                index = Mth.clamp( modData.getInt( LUNAR_INDEX_KEY ), MIN_INDEX, MAX_INDEX );
            }
        }
        return getAttributeModifiers( index );
    }
    
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers( int index ) {
        return switch( index ) {
            case -1 -> NEW_MOON_MODIFIERS;
            case 0 -> defaultModifiers;
            default -> FULL_MOON_MODIFIERS.getOrDefault( index, defaultModifiers );
        };
    }
    
    @Override
    public void onCraftedBy( ItemStack itemStack, Level level, Player player ) {
        writeIndexToNBT( itemStack, level );
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag ) {
        tooltip.add( Component.translatable( References.LUNAR_ARMOR_DESC ).withStyle( ChatFormatting.GRAY ) );
        tooltip.add( Component.literal( "" ) );
    }
    
    @Override
    public void onInventoryTick( ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex ) {
        writeIndexToNBT( stack, level );
    }
    
    /**
     * Builds the sporadic full moon attribute modifiers.<br><br>
     * Returned in {@link #getAttributeModifiers(int)} when index is
     * greater than 0.
     */
    private void createFullMoonModifiers( ArmorItem.Type type ) {
        for( int i = MIN_SPORADIC_INDEX; i < MAX_INDEX + 1; i++ ) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get( type );
            
            double defense = Math.max( 0.5, (double) (MIDNIGHT_STEEL.getDefenseForType( type ) / 2) * (double) (i / 2) );
            double toughness = MIDNIGHT_STEEL.getToughness();
            double knockbackRes = MIDNIGHT_STEEL.getKnockbackResistance();
            
            builder.put( Attributes.ARMOR,
                    new AttributeModifier( uuid, "Armor modifier", defense, AttributeModifier.Operation.ADDITION ) );
            builder.put( Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier( uuid, "Armor toughness", toughness, AttributeModifier.Operation.ADDITION ) );
            
            if( MIDNIGHT_STEEL.getKnockbackResistance() > 0 ) {
                builder.put( Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier( uuid, "Armor knockback resistance", knockbackRes, AttributeModifier.Operation.ADDITION ) );
            }
            FULL_MOON_MODIFIERS.put( i, builder.build() );
        }
    }
    
    public static void writeIndexToNBT( ItemStack itemStack, Level level ) {
        if( !level.isClientSide ) {
            CompoundTag compoundTag = itemStack.getOrCreateTag();
            CompoundTag modData = new CompoundTag();
            modData.putInt( LunarArmorItem.LUNAR_INDEX_KEY, Apocalypse.INSTANCE.getDifficultyManager().getLunarArmorModIndex() );
            compoundTag.put( LunarArmorItem.MOD_DATA_KEY, modData );
        }
    }
}
