package com.rolandoislas.multihotbar.event;

import com.rolandoislas.multihotbar.HotBarRenderer;
import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.MultiHotbar;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

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
    public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.getConnectionType().equals("VANILLA")) {
            MultiHotbar.logger.warn("Connecting to vanilla server. Multi-Hotbar 4+ currently does not support " +
                    "vanilla servers. Please consider downgrading to a version 3 release.");
            HotbarLogic.setShowDefault(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        HotbarLogic.setShowDefault(false);
    }
}
