package com.toast.apocalypse.client.screen.widget.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class InfoPoint extends Button {

    private static final MutableComponent GAMERE = Component.literal("?");

    private final MutableComponent tooltipMessage;

    public InfoPoint(int x, int y, Button.OnTooltip tooltip, MutableComponent tooltipMessage) {
        super(x, y, 20, 20, GAMERE, (button) -> {}, tooltip);
        this.tooltipMessage = tooltipMessage;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void onPress() {

    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return false;
    }

    // TODO - Check out how the narration stuff works
    /*
    @Override
    protected void defaultButtonNarrationText(NarrationElementOutput p_168803_) {
        p_168803_.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                p_168803_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
            } else {
                p_168803_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
            }
        }

    }

     */
}
