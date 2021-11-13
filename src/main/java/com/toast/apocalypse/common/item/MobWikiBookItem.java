package com.toast.apocalypse.common.item;

import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MobWikiBookItem extends Item {

    public MobWikiBookItem() {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON).tab(ItemGroup.TAB_MISC));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (player instanceof ServerPlayerEntity) {
            NetworkHelper.openMobWikiScreen((ServerPlayerEntity) player);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }
}
