package com.toast.apocalypse.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

// TODO - Will this ever be used? Very unlikely :(
public class MobWikiBookItem extends Item {
    
    public MobWikiBookItem() {
        super( new Item.Properties().stacksTo( 1 ).fireResistant().rarity( Rarity.UNCOMMON ) );
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use( Level level, Player player, InteractionHand hand ) {
        return super.use( level, player, hand );
    }
}
