package com.rolandoislas.multihotbar.event;

import com.rolandoislas.multihotbar.HotBarRenderer;
import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.gui.HotbarGuiChest;
import com.rolandoislas.multihotbar.gui.HotbarGuiInventory;
import com.rolandoislas.multihotbar.util.GuiUtil;
import com.rolandoislas.multihotbar.util.InvTweaksHelper;
import com.rolandoislas.multihotbar.util.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void pickupEvent(EntityItemPickupEvent event) {
        hotbarLogic.pickupEvent(event);
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
        hotbarLogic.connectedToServer(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void disconnectedFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        hotbarLogic.disconnectedFromServer(event);
        InvTweaksHelper.reset();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void deathEvent(LivingDeathEvent event) {
        hotbarLogic.deathEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void playerTick(TickEvent.PlayerTickEvent event) {
        InventoryHelper.tick();
        hotbarLogic.playerTick(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void guiEvent(GuiScreenEvent event) {
        GuiUtil.guiEvent(event);
    }
}