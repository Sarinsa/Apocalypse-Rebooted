package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public class FullMoonEvent extends AbstractEvent {

    /** Time until mobs can start spawning. */
    private int gracePeriod, baseGracePeriod;

    public FullMoonEvent(int id) {
        super(id);
    }

    @Override
    public String getEventStartMessage() {
        return References.FULL_MOON;
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
    public void write(JsonObject data) throws JsonIOException {
        data.addProperty("eventId", this.getId());
        data.addProperty("gracePeriod", this.gracePeriod);
        data.addProperty("baseGracePeriod", this.baseGracePeriod);
    }

    @Override
    public void read(JsonObject data) throws JsonIOException {
        this.gracePeriod = data.get("gracePeriod").getAsInt();
        this.baseGracePeriod = data.get("baseGracePeriod").getAsInt();
    }
}
