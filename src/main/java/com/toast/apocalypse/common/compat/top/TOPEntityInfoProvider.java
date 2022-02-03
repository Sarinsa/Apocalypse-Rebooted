package com.toast.apocalypse.common.compat.top;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.function.Function;

public class TOPEntityInfoProvider implements IProbeInfoEntityProvider, Function<ITheOneProbe, Void> {

    private static final String ID = Apocalypse.MODID + "_player_difficulty";

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
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof ServerPlayerEntity) {
            if (ApocalypseCommonConfig.COMMON.requireExtendedProbe() && probeMode != ProbeMode.EXTENDED)
                return;

            long difficulty = CapabilityHelper.getPlayerDifficulty((ServerPlayerEntity) entity);
            iProbeInfo.text(CompoundText.createLabelInfo(new TranslationTextComponent(References.DIFFICULTY).getString(), formatDifficulty(difficulty)));
        }
    }

    private static String formatDifficulty(long difficulty) {
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % 24000L / 2400);
        difficulty /= 24000L;

        return difficulty + "." + partialDifficulty;
    }
}
