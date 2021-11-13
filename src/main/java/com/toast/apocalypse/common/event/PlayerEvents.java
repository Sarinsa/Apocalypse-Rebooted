package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // No point doing further checks if rain damage is disabled
        if (!ApocalypseCommonConfig.COMMON.rainDamageEnabled())
            return;

        // Tick rain damage
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            RainDamageTickHelper.checkAndPerformRainDamageTick(event.player);
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
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
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
     * Make sure difficulty capability data persist on death or when leaving The End.
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
