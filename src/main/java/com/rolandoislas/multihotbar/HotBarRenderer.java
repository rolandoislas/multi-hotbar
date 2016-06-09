package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Rolando on 6/6/2016.
 */
public class HotBarRenderer extends Gui {
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int SELECTOR_SIZE = 24;
    private final ResourceLocation WIDGETS;
    private final Minecraft minecraft;
    private int[][] hotbarPos = new int[4][2];

    public HotBarRenderer() {
        super();
        this.minecraft = Minecraft.getMinecraft();
        this.WIDGETS = new ResourceLocation("minecraft", "textures/gui/widgets.png");
    }

    public void render() {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        ScaledResolution scaledResolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        if (Config.numberOfHotbars == 1)
            drawSingle(scaledResolution, 0);
        else if (Config.numberOfHotbars == 2)
            drawDouble(scaledResolution, 0);
        else if (Config.numberOfHotbars == 3) {
            drawDouble(scaledResolution, 0);
            drawSingle(scaledResolution, 2);
        } else if (Config.numberOfHotbars == 4) {
            drawDouble(scaledResolution, 0);
            drawDouble(scaledResolution, 2);
        }
        drawSelection();
        drawItems();
    }

    private void drawItems() {
        for (int i = 0; i < Config.numberOfHotbars; i++) {
            int x = hotbarPos[i][0];
            int y = hotbarPos[i][1];
            drawItems(x, y, i);
        }
    }

    private void drawSelection() {
        // Draw selection indicator
        int slot = minecraft.thePlayer.inventory.currentItem;
        int index = (int) Math.floor(slot / 9);
        slot -= index * 9;
        int x = hotbarPos[index][0];
        int y = hotbarPos[index][1];
        minecraft.getTextureManager().bindTexture(WIDGETS);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        zLevel += 100;
        minecraft.ingameGUI.drawTexturedModalRect(-1 + x + 20 * slot, y - 1, 0, HOTBAR_HEIGHT, SELECTOR_SIZE,
                SELECTOR_SIZE);
    }

    private void drawSingle(ScaledResolution scaledResolution, int index) {
        int x = scaledResolution.getScaledWidth() / 2 - HOTBAR_WIDTH / 2;
        int y = scaledResolution.getScaledHeight() - HOTBAR_HEIGHT * (index + 1);
        drawHotbar(x, y, index);
    }

    private void drawDouble(ScaledResolution scaledResolution, int index) {
        int x = scaledResolution.getScaledWidth() / 2 - HOTBAR_WIDTH;
        int y = scaledResolution.getScaledHeight() - HOTBAR_HEIGHT * (index == 0 ? 1 : index);
        for (int i = index; i < index + 2; i++) {
            drawHotbar(x, y, i);
            x += HOTBAR_WIDTH;
        }
    }

    private void drawHotbar(int x, int y, int index) {
        hotbarPos[index][0] = x;
        hotbarPos[index][1] = y;
        // Draw hotbar
        minecraft.getTextureManager().bindTexture(WIDGETS);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        minecraft.ingameGUI.drawTexturedModalRect(x, y, 0, 0, HOTBAR_WIDTH, HOTBAR_HEIGHT);
    }

    private void drawItems(int x, int y, int index) {
        // Draw items on hotbar
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < 9; i++) {
            ItemStack item = minecraft.thePlayer.inventory.mainInventory[index * 9 + i];
            if (item != null) {
                int itemX = x + 3 + 16 * i + 4 * i;
                int itemY = y + 3;
                RenderItem.getInstance().renderItemIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), item,
                        itemX, itemY, true);
                RenderItem.getInstance().renderItemOverlayIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), item, itemX, itemY);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
