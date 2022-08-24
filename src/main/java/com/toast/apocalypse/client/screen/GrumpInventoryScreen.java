package com.toast.apocalypse.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.inventory.container.GrumpInventoryContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GrumpInventoryScreen extends ContainerScreen<GrumpInventoryContainer> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/gui/container/grump.png");
    private final GrumpEntity grump;
    private float xMouse;
    private float yMouse;

    public GrumpInventoryScreen(GrumpInventoryContainer container, PlayerInventory playerInventory, GrumpEntity grump) {
        super(container, playerInventory, grump.getDisplayName());
        this.grump = grump;
        this.passEvents = false;
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTick, int xMouse, int yMouse) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(TEXTURE);
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;

        blit(matrixStack, i, j, 0, 0, imageWidth, imageHeight);
        blit(matrixStack, i + 7, j + 35 - 18, 18, imageHeight + 54, 18, 18);
        blit(matrixStack, i + 7, j + 35, 0, imageHeight + 54, 18, 18);

        InventoryScreen.renderEntityInInventory(i + 51, j + 50, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, grump);
    }

    @Override
    public void render(MatrixStack matrixStack, int xMouse, int yMouse, float partialTick) {
        renderBackground(matrixStack);
        this.xMouse = (float)xMouse;
        this.yMouse = (float)yMouse;
        super.render(matrixStack, xMouse, yMouse, partialTick);
        renderTooltip(matrixStack, xMouse, yMouse);
    }
}
