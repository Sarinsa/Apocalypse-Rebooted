package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public class FullMoonEvent extends AbstractEvent {

    /** Time until mobs can start spawning. */
    private int gracePeriod, baseGracePeriod;

    /**
     * Checks if the event should be postponed.
     * The full moon sieges should for example not
     * be skippable on servers, so to prevent players
     * from just logging out during full moons and wait
     * out the siege event, this is used to pause
     * the event as long as the server's player count is 0.
     */
    private boolean waiting;

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
    public CompoundNBT write(CompoundNBT data) {
        data.putInt("EventId", this.getId());
        data.putInt("GracePeriod", this.gracePeriod);
        data.putInt("BaseGracePeriod", this.baseGracePeriod);

        return data;
    }

    @Override
    public void read(CompoundNBT data) {
        this.gracePeriod = data.getInt("GracePeriod");
        this.baseGracePeriod = data.getInt("BaseGracePeriod");
    }
}
