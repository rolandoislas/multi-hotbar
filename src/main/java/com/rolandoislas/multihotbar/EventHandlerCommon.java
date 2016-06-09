package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by Rolando on 6/8/2016.
 */
public class EventHandlerCommon {
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
