package com.toast.apocalypse.common.misc;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for storing info about mod key binds both
 * client and server side (has one of our key binds been pressed/released?)
 */
@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerKeyBindInfo {

    private static final Map<UUID, KeyBindInfo> KEYBIND_INFO = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            createInfo(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            clearInfo(event.getEntity().getUUID());
        }
    }

    private static void createInfo(UUID playerUUID) {
        if (!KEYBIND_INFO.containsKey(playerUUID)) {
            KEYBIND_INFO.put(playerUUID, new KeyBindInfo());
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

    /** Functions as a server-side cache for mod key binding states */
    public static class KeyBindInfo {

        public KeyInfo grumpDescent = new KeyInfo();
        public KeyInfo grumpInteract = new KeyInfo();
    }

    /** Represents the "value" of a key binding (has it been pressed?) */
    public static class KeyInfo {

        private boolean value;

        private KeyInfo() {
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
