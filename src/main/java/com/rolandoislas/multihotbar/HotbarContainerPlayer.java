package com.rolandoislas.multihotbar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarContainerPlayer extends ContainerPlayer {

    public HotbarContainerPlayer(InventoryPlayer inventory, boolean isLocalWorld, EntityPlayer player) {
        super(inventory, isLocalWorld, player);
    }
}
