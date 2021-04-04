package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.toast.apocalypse.common.util.TranslationReferences;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class FullMoonEvent extends AbstractEvent {

    public FullMoonEvent(ResourceLocation id) {
        super(id);
    }

    @Override
    public String getEventStartMessage() {
        return TranslationReferences.FULL_MOON;
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
    public JsonObject save(JsonObject saveData) throws JsonIOException {
        saveData.addProperty("players", 3);
        return saveData;
    }

    @Override
    public void load(JsonObject loadData) throws JsonIOException {

    }
}
