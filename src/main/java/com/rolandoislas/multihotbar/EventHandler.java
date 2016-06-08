package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by Rolando on 6/6/2016.
 */
public class EventHandler {
    private HotBarRenderer hotbarRender;
    private HotbarLogic hotbarLogic;

    public EventHandler() {
        hotbarRender = new HotBarRenderer();
        hotbarLogic = new HotbarLogic();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void disableVanillaHotbar(RenderGameOverlayEvent event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HOTBAR) && event.isCancelable())
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void renderMultiHotbar(RenderGameOverlayEvent event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.ALL))
            hotbarRender.render();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void mouseEvent(MouseEvent event) {
        hotbarLogic.mouseEvent(event);
    }
}
