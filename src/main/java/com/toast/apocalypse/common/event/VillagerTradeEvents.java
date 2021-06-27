package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VillagerTradeEvents {

    @SubscribeEvent
    public void onTrade(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CLERIC) {
            event.getTrades().get(5).add(new VillagerTrades.ItemsForEmeraldsTrade(ApocalypseItems.LUNAR_CLOCK.get(), 34, 1, 30));
        }
    }
}
