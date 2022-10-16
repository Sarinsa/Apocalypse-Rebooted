package com.toast.apocalypse.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.mobwiki.MobEntries;
import com.toast.apocalypse.client.mobwiki.MobEntry;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public class MobWikiScreen extends Screen {

    private static final ResourceLocation ADDITIONAL_PAGE = Apocalypse.resourceLoc("textures/gui/mobwiki/text_page.png");
    private static final ResourceLocation FIRST_PAGE = Apocalypse.resourceLoc("textures/gui/mobwiki/illustration_page.png");

    private MobEntry currentEntry = MobEntries.EMPTY;
    private int currentPage = 0;

    private MobEntry[] unlockedEntries;

    private ChangePageButton forwardButton;
    private ChangePageButton backButton;

    public MobWikiScreen() {
        super(new TranslationTextComponent(References.FULL_MOON_MOB_BOOK));
    }

    @Override
    protected void init() {
        this.setFocused(null);

        this.createPages();

        int i = (this.width - 192) / 2;
        this.forwardButton = this.addButton(new ChangePageButton(i + 116, 159, true, (button) -> {
            this.nextPage();
        }, true));

        this.backButton = this.addButton(new ChangePageButton(i + 43, 159, false, (button) -> {
            this.previousPage();
        }, true));
    }

    private void createPages() {
        int size = ClientUtil.UNLOCKED_INDEXES.length;

        if (size == 0) {
            this.unlockedEntries = new MobEntry[] {};
            return;
        }

        this.unlockedEntries = new MobEntry[size];

        for (int i = 0; i < size; i++) {
            if (MobEntries.ENTRIES.containsKey(i)) {
                this.unlockedEntries[i] = MobEntries.ENTRIES.get(i);
            }
        }
    }

    private void nextPage() {
        if ((this.currentPage + 1) >= this.unlockedEntries.length) {
            return;
        }
        else {
            ++this.currentPage;
            this.currentEntry = this.unlockedEntries[this.currentPage];
        }
    }

    private void previousPage() {
        if ((this.currentPage - 1) < 0) {
            return;
        }
        else {
            --this.currentPage;
            this.currentEntry = this.unlockedEntries[this.currentPage];
        }
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (super.keyPressed(key, scancode, mods)) {
            return true;
        }
        else {
            switch(key) {
                case 266:
                    this.backButton.onPress();
                    return true;
                case 267:
                    this.forwardButton.onPress();
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float wut) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.currentEntry == MobEntries.EMPTY) {
            this.minecraft.textureManager.bind(ADDITIONAL_PAGE);
            int i = (this.width - 280) / 2;
            // MatrixStack, x, y, uOffset, vOffset, uWidth, vHeight
            this.blit(matrixStack, i, 25, 0, 0, 280, 280);
        }
        else {
            this.renderPageContent(matrixStack, this.currentEntry);
        }
    }

    public void renderBackground(MatrixStack matrixStack) {
        if (this.minecraft.level != null) {
            // Gray transparent background
            this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this, matrixStack));
        }
        else {
            // No idea how this could ever happen
            this.renderDirtBackground(0);
        }
    }

    private void renderPageContent(MatrixStack matrixStack, MobEntry mobEntry) {
        this.minecraft.textureManager.bind(FIRST_PAGE);
        int i = (this.width - 192) / 2;
        this.blit(matrixStack, i, 2, 0, 0, 192, 192);

        this.minecraft.textureManager.bind(mobEntry.getMobTexture());
        this.blit(matrixStack, i, 2, 0, 0, 192, 192);

        drawCenteredString(matrixStack, this.font, mobEntry.getMobName(), this.width / 2, 25, -1);
    }
}
