package com.toast.apocalypse.client.screen.widget.config;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class InfoPoint extends Button {

    private static final ITextComponent GAMERE = new StringTextComponent("?");

    private final ITextComponent tooltipMessage;

    public InfoPoint(int x, int y, Button.ITooltip tooltip, ITextComponent tooltipMessage) {
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

    @Override
    protected IFormattableTextComponent createNarrationMessage() {
        return new TranslationTextComponent("gui.narrate.button", this.tooltipMessage);
    }
}
