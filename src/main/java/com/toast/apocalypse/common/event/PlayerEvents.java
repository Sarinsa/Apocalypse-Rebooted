package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.common.util.VersionCheckHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEvents {

    /**
     * Notify the player of an available
     * mod update on login, if there is one.
     */
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ApocalypseCommonConfig.COMMON.getSendUpdateMessage())
            return;

        String updateMessage = VersionCheckHelper.getUpdateMessage();

        if (updateMessage != null) {
            event.getPlayer().sendMessage(new StringTextComponent(updateMessage), Util.NIL_UUID);
        }
    }

    /**
     * Prevent players from being able
     * to sleep during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerTrySleep(PlayerSleepInBedEvent event) {
        PlayerEntity player = event.getPlayer();

        if (player.isSleeping() || !player.isAlive())
            return;

        if (!player.getCommandSenderWorld().isClientSide && Apocalypse.INSTANCE.getDifficultyManager().isFullMoon()) {
            event.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
            player.displayClientMessage(new TranslationTextComponent(References.TRY_SLEEP_FULL_MOON), true);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();

            NetworkHelper.sendUpdatePlayerDifficulty(serverPlayer);
            NetworkHelper.sendMobWikiIndexUpdate(serverPlayer);
        }
    }

    /**
     * Makes sure necessary capability data from Apocalypse persists on player cloning.<br>
     * (death or leaving The End).
     */
    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity newPlayer = (ServerPlayerEntity) event.getPlayer();
            ServerPlayerEntity originalPlayer = (ServerPlayerEntity) event.getOriginal();

            long difficulty = CapabilityHelper.getPlayerDifficulty(originalPlayer);
            long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(originalPlayer);
            CompoundNBT eventData = CapabilityHelper.getEventData(originalPlayer);
            int[] mobWikiIndexes = CapabilityHelper.getMobWikiIndexes(originalPlayer);

            CapabilityHelper.setPlayerDifficulty(newPlayer, difficulty);
            CapabilityHelper.setMaxPlayerDifficulty(newPlayer, maxDifficulty);
            CapabilityHelper.setEventData(newPlayer, eventData);
            CapabilityHelper.setMobWikiIndexes(newPlayer, mobWikiIndexes);
        }
    }
}
