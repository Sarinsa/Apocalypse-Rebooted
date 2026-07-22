package com.toast.apocalypse.common.item;

import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class FatherlyToastItem extends Item {
    
    public static final String KEY_TOAST_LEVEL = "ToastLevel";
    
    
    public FatherlyToastItem() {
        super( new Item.Properties()
                .fireResistant()
                .food( ApocalypseFoods.FATHERLY_TOAST )
        );
    }
    
    @Override
    public ItemStack finishUsingItem( ItemStack itemStack, Level world, LivingEntity livingEntity ) {
        if( isEdible() ) {
            if( livingEntity instanceof Player player && !player.getAbilities().instabuild ) {
                // Setting creative players on fire just makes the fire
                // extinguish instantly, which is weird to look at.
                livingEntity.setSecondsOnFire( 1_000 );
            }
            return livingEntity.eat( world, itemStack );
        }
        return itemStack;
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag ) {
        tooltip.add( Component.translatable( References.FATHERLY_TOAST_DESC ).withStyle( ChatFormatting.GRAY ) );
        
        if( itemStack.hasTag() ) {
            tooltip.add( Component.literal( "" ) );
            
            final CompoundTag tag = itemStack.getTag();
            
            if( NBTHelper.containsNumber( tag, KEY_TOAST_LEVEL ) ) {
                tooltip.add( Component.translatable( References.FATHERLY_TOAST_LEVEL, tag.getInt( KEY_TOAST_LEVEL ) ).withStyle( ChatFormatting.GRAY ) );
            }
        }
    }
}
