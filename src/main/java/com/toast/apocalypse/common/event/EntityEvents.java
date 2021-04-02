package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.IFullMoonMob;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import com.toast.apocalypse.common.util.TranslationReferences;
import com.toast.apocalypse.common.util.WorldDifficultyManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class EntityEvents {

    /**
     * Cancel full moon monsters despawning during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDespawnCheck(LivingSpawnEvent.AllowDespawn event) {
        if (event.getEntityLiving() instanceof IFullMoonMob) {
            IFullMoonMob fullMoonMob = (IFullMoonMob) event.getEntityLiving();
            if (WorldDifficultyManager.isFullMoon(event.getWorld()) && fullMoonMob.persistentDuringFullMoon()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Tick rain damage
        if (event.side == LogicalSide.SERVER) {
            World world = event.player.getCommandSenderWorld();

            if (EnchantmentHelper.hasAquaAffinity(event.player) || !world.canSeeSky(event.player.blockPosition()))
                return;

            if (world.isRainingAt(event.player.blockPosition()))
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
        World world = player.getCommandSenderWorld();

        if (player.isSleeping() || !player.isAlive())
            return;

        if (world.isNight() && WorldDifficultyManager.isFullMoon(world)) {
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
            player.displayClientMessage(new TranslationTextComponent(TranslationReferences.TRY_SLEEP_FULL_MOON), true);
        }
    }
}
