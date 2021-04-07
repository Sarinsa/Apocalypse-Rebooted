package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.JsonIOException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

public class ThunderStormEvent extends AbstractEvent {

    public ThunderStormEvent(int id) {
        super(id);
    }

    @Override
    public String getEventStartMessage() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void update() {

    }

    @Override
    public void update(ServerWorld world) {

    }

    @Override
    public void update(PlayerEntity player) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        return null;
    }

    @Override
    public void read(CompoundNBT data) throws JsonIOException {

    }
}
