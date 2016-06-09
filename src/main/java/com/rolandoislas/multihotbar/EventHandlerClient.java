package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void handleHotbarRender(RenderGameOverlayEvent event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HOTBAR) && event.isCancelable()) {
            event.setCanceled(true);
            hotbarRender.render();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void mouseEvent(MouseEvent event) {
        hotbarLogic.mouseEvent(event);
    }
}
