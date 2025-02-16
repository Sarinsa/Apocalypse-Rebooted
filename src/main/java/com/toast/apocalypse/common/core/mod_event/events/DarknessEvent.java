package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Shadefiend;
import com.toast.apocalypse.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.ForgeEventFactory;

public final class DarknessEvent extends AbstractEvent {

    private int timeNextStage = 150;
    private Stage stage = Stage.STARTING;

    public DarknessEvent(EventType<?> type) {
        super(type);
    }

    @Override
    public void onStart(MinecraftServer server, ServerPlayer player) {

    }

    @Override
    public void update(ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager) {
        if (timeNextStage > 0) {
            timeNextStage -= 5;
        }
        if (timeNextStage <= 0 && stage != Stage.RESET) {
            timeNextStage = stage == Stage.OH_NO ? 400 : 150;
            stage = Stage.values()[stage.ordinal() + 1];

            if (stage == Stage.FIRST_WARN) {
                player.displayClientMessage(Component.translatable(References.CALL_OF_THE_SHADOWS_0), true);
            }
            else if (stage == Stage.SECOND_WARN) {
                player.displayClientMessage(Component.translatable(References.CALL_OF_THE_SHADOWS_1), true);
                BlockPos soundPos = player.blockPosition().offset(
                        (int) player.getRandom().nextGaussian() * 10,
                        (int) player.getRandom().nextGaussian() * 10,
                        (int) player.getRandom().nextGaussian() * 10
                );
                level.playSound(null, soundPos, SoundEvents.AMBIENT_CAVE.get(), SoundSource.AMBIENT, 0.6F, 1.0F);
            }
            else if (stage == Stage.OH_NO) {
                player.displayClientMessage(Component.translatable(References.CALL_OF_THE_SHADOWS_2), true);

                Shadefiend shadefiend = ApocalypseEntities.SHADEFIEND.get().create(level);

                if (shadefiend == null) return;

                ForgeEventFactory.onFinalizeSpawn(shadefiend, level, level.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                shadefiend.setPos(player.getX(), player.getY(), player.getZ());
                level.tryAddFreshEntityWithPassengers(shadefiend);

                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50));
            }
        }
    }

    @Override
    public boolean shouldContinueRunning(ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager) {
        if (stage == Stage.RESET || player.isCreative() || player.isSpectator()) return false;

        BlockPos pos = player.blockPosition();
        return level.getBrightness(LightLayer.SKY, pos) <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.skyLightLevel.get()
                && level.getBrightness(LightLayer.BLOCK, pos) <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.blockLightLevel.get();
    }

    @Override
    public void onEnd(MinecraftServer server, ServerPlayer player) { }

    @Override
    public void stop(ServerLevel level, ServerPlayer player) { }

    @Override
    public void writeAdditional(CompoundTag data) {
        data.putInt("TimeNextStage", timeNextStage);
        data.putInt("EventStage", stage.ordinal());
    }

    /**
     * Loads this event.
     *
     * @param data the tag to read from.
     */
    public void read(CompoundTag data, ServerPlayer player, ServerLevel level) {
        if (data.contains("TimeNextStage", Tag.TAG_ANY_NUMERIC)) {
            timeNextStage = data.getInt("TimeNextStage");
        }
        if (data.contains("EventStage", Tag.TAG_ANY_NUMERIC)) {
            int ordinal = Mth.clamp(data.getInt("EventStage"), 0, Stage.values().length - 1);
            stage = Stage.values()[ordinal];
        }
    }

    enum Stage {
        STARTING,
        FIRST_WARN,
        SECOND_WARN,
        OH_NO,
        RESET,
    }
}
