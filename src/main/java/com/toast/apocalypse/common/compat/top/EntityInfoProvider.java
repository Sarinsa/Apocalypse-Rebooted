package com.toast.apocalypse.common.compat.top;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.function.Function;

public class EntityInfoProvider implements IProbeInfoEntityProvider, Function<ITheOneProbe, Void> {

    private static final String ID = Apocalypse.resourceLoc("entity_info").toString();

    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerEntityProvider(this);
        return null;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (probeMode == ProbeMode.EXTENDED) {
            if (entity instanceof ServerPlayerEntity) {
                long difficulty = CapabilityHelper.getPlayerDifficulty((ServerPlayerEntity) entity);
                iProbeInfo.text(formatDifficulty(difficulty));
            }
        }
    }

    private static ITextComponent formatDifficulty(long difficulty) {
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % 24000L / 2400);
        difficulty /= 24000L;

        return new TranslationTextComponent(References.DIFFICULTY, ": " + difficulty + "." + partialDifficulty);
    }
}
