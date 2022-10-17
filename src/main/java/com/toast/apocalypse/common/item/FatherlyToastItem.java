package com.toast.apocalypse.common.item;

import com.toast.apocalypse.common.util.References;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class FatherlyToastItem extends Item {

    public FatherlyToastItem() {
        super(new Item.Properties()
                .fireResistant()
                .food(ApocalypseFoods.FATHERLY_TOAST)
                .tab(ItemGroup.TAB_FOOD)
        );
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity livingEntity) {
        if (this.isEdible()) {
            if (livingEntity instanceof PlayerEntity && !((PlayerEntity) livingEntity).abilities.instabuild) {
                // Setting creative players on fire just makes the fire
                // extinguish instantly, which is weird to look at.
                livingEntity.setSecondsOnFire(1000);
            }
            return livingEntity.eat(world, itemStack);
        }
        return itemStack;
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("ConstantConditions")
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(References.FATHERLY_TOAST_DESC).withStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent(""));

        if (itemStack.hasTag() && itemStack.getTag().contains("ToastLevel", Constants.NBT.TAG_INT)) {
            tooltip.add(new TranslationTextComponent(References.FATHERLY_TOAST_LEVEL, itemStack.getTag().getInt("ToastLevel")).withStyle(TextFormatting.GRAY));
        }
    }
}
