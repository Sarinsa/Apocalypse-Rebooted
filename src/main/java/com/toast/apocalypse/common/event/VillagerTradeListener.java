package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VillagerTradeListener {
    
    @SubscribeEvent
    public void onTrade( VillagerTradesEvent event ) {
        if( event.getType() == VillagerProfession.CLERIC ) {
            event.getTrades().get( 2 ).add( new VillagerTrades.EmeraldForItems( ApocalypseObjects.Items.FRAGMENTED_SOUL.get(), 2, 10, 10 ) );
            event.getTrades().get( 5 ).add( new VillagerTrades.ItemsForEmeralds( ApocalypseObjects.Items.LUNAR_CLOCK.get(), 34, 1, 30 ) );
        }
    }
}
