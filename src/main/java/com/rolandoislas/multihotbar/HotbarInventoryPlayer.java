package com.rolandoislas.multihotbar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarInventoryPlayer extends InventoryPlayer {
    public HotbarInventoryPlayer(EntityPlayer player) {
        super(player);
    }

    @Override
    public ItemStack getCurrentItem() {
        return this.currentItem < Config.numberOfHotbars * 9 && this.currentItem >= 0 ?
                this.mainInventory[this.currentItem] : null;
    }
}
