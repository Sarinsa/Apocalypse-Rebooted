package com.toast.apocalypse.common.compat.top;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class TOPEntityInfoProvider implements IProbeInfoEntityProvider, Function<ITheOneProbe, Void> {

    private static final String ID = Apocalypse.MODID + "_player_difficulty";

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private IStyleManager styleManager;

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerEntityProvider(this);
        this.styleManager = probe.getStyleManager();
        return null;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof ServerPlayer) {
            if (ApocalypseCommonConfig.COMMON.requireExtendedProbe() && probeMode != ProbeMode.EXTENDED)
                return;

            long difficulty = CapabilityHelper.getPlayerDifficulty((ServerPlayer) entity);
            iProbeInfo.text(CompoundText.createLabelInfo(Component.translatable(References.DIFFICULTY).getString(), formatDifficulty(difficulty)));
        }
    }

    private static String formatDifficulty(long difficulty) {
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % 24000L / 2400);
        difficulty /= 24000L;

        return difficulty + "." + partialDifficulty;
    }
}
