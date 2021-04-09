package com.toast.apocalypse.common.item;

import com.toast.apocalypse.common.util.References;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class FatherlyToastItem extends Item {

    public FatherlyToastItem() {
        super(new Item.Properties().fireResistant().food(ApocalypseFoods.FATHERLY_TOAST));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity livingEntity) {
        if (this.isEdible()) {

            if (livingEntity instanceof PlayerEntity) {
                // Avoid the player rendering as on fire for just
                // a split second since they are in creative.
                if (((PlayerEntity)livingEntity).abilities.instabuild)
                    return livingEntity.eat(world, itemStack);
            }
            else {
                livingEntity.setSecondsOnFire(1000);
                return livingEntity.eat(world, itemStack);
            }
        }
        return itemStack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(References.FATHERLY_TOAST_DESC).withStyle(TextFormatting.GRAY));
    }
}
