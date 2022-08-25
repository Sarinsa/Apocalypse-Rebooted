package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.event.ClientEvents;
import com.toast.apocalypse.client.event.DifficultyRenderHandler;
import com.toast.apocalypse.client.screen.GrumpInventoryScreen;
import com.toast.apocalypse.client.screen.MobWikiScreen;
import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.inventory.container.GrumpInventoryContainer;
import com.toast.apocalypse.common.network.message.*;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Referencing client only code here should cause no trouble
 * as long as this class isn't loaded by anything else
 * than the client itself (which should be the case).
 */
public class ClientWork {

    @SuppressWarnings("all")
    private static <T> T getCapability(ClientPlayerEntity player, Capability<T> capability) {
        return player.getCapability(capability).orElse(capability.getDefaultInstance());
    }

    public static void handleDifficultyUpdate(S2CUpdatePlayerDifficulty message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            getCapability(player, ApocalypseCapabilities.DIFFICULTY_CAPABILITY).setDifficulty(message.difficulty);
        }
    }

    public static void handleDifficultyRateUpdate(S2CUpdatePlayerDifficultyRate message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            getCapability(player, ApocalypseCapabilities.DIFFICULTY_CAPABILITY).setDifficultyMult(message.multiplier);
        }
    }

    public static void handleMaxDifficultyUpdate(S2CUpdatePlayerMaxDifficulty message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            long maxDifficulty = message.maxDifficulty;
            getCapability(player, ApocalypseCapabilities.DIFFICULTY_CAPABILITY).setMaxDifficulty(maxDifficulty);
            DifficultyRenderHandler.COLOR_CHANGE = maxDifficulty > -1 ? maxDifficulty : References.DEFAULT_COLOR_CHANGE;
        }
    }

    public static void handleEntityVelocityUpdate(S2CUpdateEntityVelocity message) {
        World world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(message.entityId);

            if (entity != null) {
                entity.setDeltaMovement(message.xMotion, message.yMotion, message.zMotion);
            }
        }
    }

    public static void handleMoonPhaseUpdate(S2CUpdateMoonPhase message) {
        ClientUtil.OVERWORLD_MOON_PHASE = message.moonPhase;
    }

    public static void handleMobWikiIndexUpdate(S2CUpdateMobWikiIndexes message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            int[] unlockedIndexes = message.indexes;
            getCapability(player, ApocalypseCapabilities.MOB_WIKI_CAPABILITY).setEntries(unlockedIndexes);
        }
    }

    public static void handleOpenMobWikiScreen(S2COpenMobWikiScreen message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null && player.getUUID().equals(message.uuid))
            return;

        Minecraft.getInstance().setScreen(new MobWikiScreen());
    }

    public static void handleOpenGrumpInventory(S2COpenGrumpInventory message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null || !(player.getUUID().equals(message.uuid)))
            return;

        ClientWorld world = Minecraft.getInstance().level;

        if (world == null)
            return;

        Entity entity = world.getEntity(message.entityID);

        if (!(entity instanceof GrumpEntity))
            return;

        GrumpEntity grump = (GrumpEntity) entity;
        PlayerInventory playerInventory = Minecraft.getInstance().player.inventory;

        GrumpInventoryContainer container = new GrumpInventoryContainer(message.containerId, playerInventory, grump.getInventory(), grump);
        Minecraft.getInstance().player.containerMenu = container;
        Minecraft.getInstance().setScreen(new GrumpInventoryScreen(container, playerInventory, grump));
    }
}
