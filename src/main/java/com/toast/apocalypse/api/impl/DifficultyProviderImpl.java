package com.toast.apocalypse.api.impl;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.api.plugin.DifficultyProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class DifficultyProviderImpl implements DifficultyProvider {

    private static final List<Integer> EMPTY_ID_LIST = ImmutableList.of();

    @Override
    public double getDifficultyRate(Player player) {
        return CapabilityHelper.getPlayerDifficultyMult(player);
    }

    @Override
    public long getPlayerDifficulty(Player player) {
        return CapabilityHelper.getPlayerDifficulty(player);
    }

    @Override
    public long getMaxPlayerDifficulty(Player player) {
        return CapabilityHelper.getMaxPlayerDifficulty(player);
    }

    @Override
    public List<Integer> getEventIds(ServerPlayer player) {
        Iterable<EventType<?>> eventTypes = Apocalypse.INSTANCE.getDifficultyManager().getEventTypes(player);

        if (eventTypes == null) return EMPTY_ID_LIST;

        List<Integer> eventIds = new ArrayList<>();

        for (EventType<?> eventType : eventTypes) {
            eventIds.add(eventType.getId());
        }
        return ImmutableList.copyOf(eventIds);
    }
}
