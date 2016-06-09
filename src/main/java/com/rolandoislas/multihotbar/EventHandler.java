package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void playerJoined(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            player.writeToNBT(nbttagcompound);
            player.inventory = new HotbarInventoryPlayer(player);
            player.inventoryContainer = new HotbarContainerPlayer(player.inventory, !player.worldObj.isRemote, player);
            player.openContainer = player.inventoryContainer;
            player.readEntityFromNBT(nbttagcompound);
        }
    }
}
