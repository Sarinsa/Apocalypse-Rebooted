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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.toast.apocalypse.common.item.ModArmorMaterials.MIDNIGHT_STEEL;

public class LunarArmorItem extends ArmorItem {
    
    public static final String KEY_MOD_DATA = "ApocalypseModData";
    public static final String KEY_LUNAR_INDEX = "LunarArmorIndex";
    
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
        
        if( stack.getTag() != null && stack.getTag().contains( KEY_MOD_DATA, Tag.TAG_COMPOUND ) ) {
            CompoundTag modData = stack.getTag().getCompound( KEY_MOD_DATA );
            
            if( modData.contains( KEY_LUNAR_INDEX, Tag.TAG_INT ) ) {
                index = Mth.clamp( modData.getInt( KEY_LUNAR_INDEX ), MIN_INDEX, MAX_INDEX );
            }
        }
        return getModifiersForIndex( index );
    }
    
    /** @return A map of attribute modifiers associated with the specified index. */
    public Multimap<Attribute, AttributeModifier> getModifiersForIndex( int index ) {
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
    public void appendHoverText( ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag ) {
        tooltip.add( Component.translatable( References.LUNAR_ARMOR_DESC ).withStyle( ChatFormatting.GRAY ) );
    }
    
    @Override
    public void onInventoryTick( ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex ) {
        super.onInventoryTick( stack, level, player, slotIndex, selectedIndex );
        writeIndexToNBT( stack, level );
    }
    
    /**
     * Builds the sporadic full moon attribute modifiers.<br><br>
     * Returned in {@link #getModifiersForIndex(int)} when index is
     * greater than 0.
     */
    private void createFullMoonModifiers( ArmorItem.Type type ) {
        for( int index = MIN_SPORADIC_INDEX; index < MAX_INDEX + 1; index++ ) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get( type );
            
            double defense = Math.max( 0.5, (double) (MIDNIGHT_STEEL.getDefenseForType( type ) / 2) * (double) (index / 2) );
            double toughness = MIDNIGHT_STEEL.getToughness();
            double knockbackRes = MIDNIGHT_STEEL.getKnockbackResistance();
            
            builder.put( Attributes.ARMOR, new AttributeModifier( uuid, "Armor modifier", defense, AttributeModifier.Operation.ADDITION ) );
            builder.put( Attributes.ARMOR_TOUGHNESS, new AttributeModifier( uuid, "Armor toughness", toughness, AttributeModifier.Operation.ADDITION ) );
            
            if( MIDNIGHT_STEEL.getKnockbackResistance() > 0 ) {
                builder.put( Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier( uuid, "Armor knockback resistance", knockbackRes, AttributeModifier.Operation.ADDITION ) );
            }
            FULL_MOON_MODIFIERS.put( index, builder.build() );
        }
    }
    
    /**
     * Writes the current "lunar modifier index" to the specified item stack.
     * The modifier index is calculated periodically by Apocalypse's difficulty manager on server tick.
     *
     * @see com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager#calculateLunarArmorIndex
     */
    public static void writeIndexToNBT( ItemStack itemStack, Level level ) {
        if( level.isClientSide ) return;
        
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        CompoundTag modData = new CompoundTag();
        modData.putInt( LunarArmorItem.KEY_LUNAR_INDEX, Apocalypse.INSTANCE.getDifficultyManager().getLunarArmorModIndex() );
        compoundTag.put( LunarArmorItem.KEY_MOD_DATA, modData );
    }
}
