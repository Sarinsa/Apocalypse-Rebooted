package com.toast.apocalypse.common.misc;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerKeyBindInfo {

    private static final Map<UUID, KeyBindInfo> KEYBIND_INFO = new HashMap<>();


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            getInfo(event.player.getUUID()).resetPressables((ServerPlayerEntity) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            createInfo(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            clearInfo(event.getPlayer().getUUID());
        }
    }

    private static void createInfo(UUID playerUUID) {
        if (!KEYBIND_INFO.containsKey(playerUUID)) {
            KEYBIND_INFO.put(playerUUID, new KeyBindInfo());
        }
        else {
            Apocalypse.LOGGER.warn("Attempted to create KeyBindInfo for player UUID {} with already existing KeyBindInfo.", playerUUID);
        }
    }

    private static void clearInfo(UUID playerUUID) {
        KEYBIND_INFO.remove(playerUUID);
    }

    @Nonnull
    public static KeyBindInfo getInfo(UUID playerUUID) {
        if (KEYBIND_INFO.containsKey(playerUUID)) {
            return KEYBIND_INFO.get(playerUUID);
        }
        else {
            createInfo(playerUUID);
            return getInfo(playerUUID);
        }
    }


    public static class KeyBindInfo {

        public KeyInfo grumpDescent = new KeyInfo((player) -> !(player.getVehicle() instanceof GrumpEntity));
        public KeyInfo grumpInteract = new KeyInfo((player -> true));

        private void resetPressables(ServerPlayerEntity player) {
            grumpDescent.checkReset(player);
            grumpInteract.checkReset(player);
        }
    }

    public static class KeyInfo {

        private boolean value;
        private final Predicate<ServerPlayerEntity> resetPredicate;

        private KeyInfo(Predicate<ServerPlayerEntity> resetPredicate) {
            this.resetPredicate = resetPredicate;
        }

        public void checkReset(ServerPlayerEntity player) {
            if (resetPredicate.test(player))
                value = false;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }
    }

    // Non-instantiatable
    private PlayerKeyBindInfo() {}

    public static void init() {}
}
