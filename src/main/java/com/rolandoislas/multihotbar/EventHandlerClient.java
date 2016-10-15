package com.rolandoislas.multihotbar;

import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void handleHotbarRender(RenderGameOverlayEvent event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HOTBAR) && event.isCancelable()) {
            if (!HotbarLogic.showDefault) {
                event.setCanceled(true);
                hotbarRender.render();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void mouseEvent(MouseEvent event) {
        hotbarLogic.mouseEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void inputEvent(InputEvent event) {
        hotbarLogic.inputEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void pickupEvent(EntityItemPickupEvent event) {
        hotbarLogic.pickupEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void shiftOverlayUp(RenderGameOverlayEvent.Pre event) {
        // If events preceding the hotbar are cancelled pop the maxtrix before the hotbar in rendered
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HOTBAR) && (!renderPosted)) {
            GL11.glPopMatrix();
            renderPosted = true;
        }
        // Apply the translation
        if ((!HotbarLogic.showDefault) && Config.numberOfHotbars > 2 && isElementToShift(event.type)) {
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
        if ((!HotbarLogic.showDefault) && Config.numberOfHotbars > 2 && isElementToShift(event.type)) {
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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void worldLoad(WorldEvent.Load event) {
        hotbarLogic.load(event.world);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        hotbarLogic.setWorldAddress(event.manager.getRemoteAddress().toString());
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void worldUnload(WorldEvent.Unload event) {
        hotbarLogic.save();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        hotbarLogic.playerChangedDimension();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.worldObj.getGameRules().getBoolean("keepInventory"))
            HotbarLogic.reset();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void playerTick(TickEvent.PlayerTickEvent event) {
        InventoryHelper.tick();
    }
}
