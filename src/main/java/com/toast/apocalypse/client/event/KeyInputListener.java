package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.ApocalypseKeyBindings;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.misc.PlayerKeyBindInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyInputListener {

    private final Minecraft mc;


    public KeyInputListener(Minecraft minecraft) {
        this.mc = minecraft;
    }

    /** Misc keybinding handling */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        // Check if the player has no open GUIs
        if (mc.player != null && mc.screen == null) {
            if (keyPressed(event, mc.options.keyInventory)) {
                handleInventoryPress();
            }
            else if (keyPressed(event, ApocalypseKeyBindings.GRUMP_DESCENT)) {
                handleGrumpDescent(false);
            }
            else if (keyReleased(event, ApocalypseKeyBindings.GRUMP_DESCENT)) {
                handleGrumpDescent(true);
            }
            else if (keyPressed(event, ApocalypseKeyBindings.GRUMP_INTERACTION)) {
                handleGrumpInteract();
            }
        }
    }

    private void handleInventoryPress() {
        if (mc.player != null) {
            ClientPlayerEntity player = mc.player;

            if (player.getVehicle() instanceof GrumpEntity) {
                NetworkHelper.requestOpenGrumpInventory(player.getUUID());
            }
        }
    }

    private void handleGrumpDescent(boolean keyReleased) {
        if (mc.player != null) {
            ClientPlayerEntity player = mc.player;

            PlayerKeyBindInfo.getInfo(player.getUUID()).grumpDescent.setValue(keyReleased);

            if (player.getVehicle() instanceof GrumpEntity) {
                NetworkHelper.requestGrumpDescentUpdate(player.getUUID(), keyReleased);
            }
        }
    }

    private void handleGrumpInteract() {

    }

    /** Checks if the given KeyBinding has been pressed */
    private boolean keyPressed(InputEvent.KeyInputEvent event, KeyBinding checkedKey) {
        return event.getKey() == checkedKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS;
    }

    private boolean keyReleased(InputEvent.KeyInputEvent event, KeyBinding checkedKey) {
        return event.getKey() == checkedKey.getKey().getValue() && event.getAction() == GLFW.GLFW_RELEASE;
    }

    private boolean keyRepeatOrPress(InputEvent.KeyInputEvent event, KeyBinding checkedKey) {
        return event.getKey() == checkedKey.getKey().getValue() && (event.getAction() == GLFW.GLFW_REPEAT || event.getAction() == GLFW.GLFW_PRESS);
    }
}
