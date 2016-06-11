package com.rolandoislas.multihotbar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Rolando on 6/8/2016.
 */
public class EventHandlerCommon {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
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
