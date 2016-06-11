package com.rolandoislas.multihotbar;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.opengl.GL11;

/**
 * Created by Rolando on 6/6/2016.
 */
public class EventHandlerClient {
    private HotBarRenderer hotbarRender;
    private HotbarLogic hotbarLogic;
    private boolean renderPosted = true;

    public EventHandlerClient() {
        hotbarRender = new HotBarRenderer();
        hotbarLogic = new HotbarLogic();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void handleHotbarRender(RenderGameOverlayEvent event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HOTBAR) && event.isCancelable()) {
            event.setCanceled(true);
            hotbarRender.render();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void mouseEvent(MouseEvent event) {
        hotbarLogic.mouseEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void shiftOverlayUp(RenderGameOverlayEvent.Pre event) {
        if (Config.numberOfHotbars > 2 && isElementToShift(event.type)) {
            if (!renderPosted)
                GL11.glPopMatrix();
            renderPosted = false;
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -22, 0);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void shiftOverlayDown(RenderGameOverlayEvent.Post event) {
        if (Config.numberOfHotbars > 2 && isElementToShift(event.type)) {
            renderPosted = true;
            GL11.glPopMatrix();
        }
    }

    private boolean isElementToShift(RenderGameOverlayEvent.ElementType type) {
        return type == RenderGameOverlayEvent.ElementType.CHAT ||
                type == RenderGameOverlayEvent.ElementType.HEALTH ||
                type == RenderGameOverlayEvent.ElementType.AIR ||
                type == RenderGameOverlayEvent.ElementType.ARMOR ||
                type == RenderGameOverlayEvent.ElementType.EXPERIENCE ||
                type == RenderGameOverlayEvent.ElementType.FOOD ||
                type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT ||
                type == RenderGameOverlayEvent.ElementType.JUMPBAR;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(MultiHotbar.MODID)) {
            Config.config.save();
            Config.reload();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void keyPressed(InputEvent.KeyInputEvent event) {
        hotbarLogic.keyPressed(event);
    }
}
