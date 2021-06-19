package com.toast.apocalypse.client.screen;

import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class ApocalypseWorldCreateConfigScreen extends Screen {

    /**
     * The screen that was previously "active"
     * This should only ever be the world creation screen.
     */
    private final Screen parent;
    private final Minecraft minecraft = Minecraft.getInstance();

    public ApocalypseWorldCreateConfigScreen(Screen parent) {
        super(new TranslationTextComponent(References.APOCALYPSE_WORLD_CREATE_CONFIG_TITLE));
        this.parent = parent;
    }

    @Override
    public void init() {
        this.addButton(new Button(this.width / 2, this.height / 2, 25, 10, new TranslationTextComponent("No, go away :("), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));
    }
}
