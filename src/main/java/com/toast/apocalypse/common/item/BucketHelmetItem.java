package com.toast.apocalypse.common.item;

import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BucketHelmetItem extends ArmorItem {

    private static final String HELMET_TEXTURE_1 = Apocalypse.resourceLoc("textures/models/armor/bucket_layer_1.png").toString();
    private static final String HELMET_TEXTURE_2 = Apocalypse.resourceLoc("textures/models/armor/bucket_layer_2.png").toString();

    public BucketHelmetItem() {
        super(ArmorMaterial.IRON, EquipmentSlotType.HEAD, new Item.Properties().tab(ItemGroup.TAB_COMBAT).defaultDurability(0));
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return slot == EquipmentSlotType.CHEST ? HELMET_TEXTURE_2 : HELMET_TEXTURE_1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks) {
        ClientUtil.renderBucketHelmOverlay(width, height);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(References.BUCKET_HELM_DESC).withStyle(TextFormatting.GRAY));
    }
}
