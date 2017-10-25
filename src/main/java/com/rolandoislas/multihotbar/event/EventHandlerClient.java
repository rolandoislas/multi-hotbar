package com.rolandoislas.multihotbar.event;

import com.rolandoislas.multihotbar.HotBarRenderer;
import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by Rolando on 6/6/2016.
 */
public class EventHandlerClient {
    private HotBarRenderer hotbarRender;
    private HotbarLogic hotbarLogic;

    public EventHandlerClient() {
        hotbarRender = new HotBarRenderer();
        hotbarLogic = new HotbarLogic();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayEvent(RenderGameOverlayEvent event) {
        hotbarRender.renderOverlayEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void mouseEvent(MouseEvent event) {
        hotbarLogic.mouseEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayEventPre(RenderGameOverlayEvent.Pre event) {
        hotbarRender.renderGameOverlayPre(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlayEventPost(RenderGameOverlayEvent.Post event) {
        hotbarRender.renderOverlayEventPost(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.configChanged(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void keyPressed(InputEvent.KeyInputEvent event) {
        hotbarLogic.keyPressed(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        HotbarLogic.setHasCoreMod(false);
        HotbarLogic.setSentPlayerMessage(false);
    }

    @SubscribeEvent(priority =  EventPriority.NORMAL)
    public void playerTick(TickEvent.PlayerTickEvent event) {
        HotbarLogic.sendPlayerMessage(event);
    }
}
