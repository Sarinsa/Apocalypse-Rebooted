package com.toast.apocalypse.common.item;

import com.toast.apocalypse.client.ClientRegister;
import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.renderers.model.armor.BucketHelmetModel;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

    public static final String TEXTURE = Apocalypse.resourceLoc("textures/models/armor/bucket_helm.png").toString();

    public BucketHelmetItem() {
        super(ArmorMaterial.IRON, EquipmentSlotType.HEAD, new Item.Properties().tab(ItemGroup.TAB_COMBAT).defaultDurability(0));
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return TEXTURE;
    }

    // This is the largest number of annotations I have ever seen on one method
    @OnlyIn(Dist.CLIENT)
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A defaultModel) {
        return armorSlot == EquipmentSlotType.HEAD ? (A) ClientRegister.BUCKET_HELMET_MODEL : defaultModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks) {
        ClientUtil.renderBucketHelmOverlay(width, height);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(References.BUCKET_HELM_DESC).withStyle(TextFormatting.GRAY));
    }
}
