package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VillagerTradeEvents {

    @SubscribeEvent
    public void onTrade(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CLERIC) {
            event.getTrades().get(2).add(new VillagerTrades.EmeraldForItems(ApocalypseItems.FRAGMENTED_SOUL.get(), 2, 10, 10));
            event.getTrades().get(5).add(new VillagerTrades.ItemsForEmeralds(ApocalypseItems.LUNAR_CLOCK.get(), 34, 1, 30));
        }
    }
}
