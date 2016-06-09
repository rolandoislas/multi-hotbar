package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.client.event.MouseEvent;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    public void mouseEvent(MouseEvent event) {
        if (event.dwheel != 0) { // Scrolled
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if (event.dwheel < 0) {
                if (player.inventory.currentItem < Config.numberOfHotbars * 9 - 1)
                    player.inventory.currentItem++;
                else
                    player.inventory.currentItem = 0;
            }
            else {
                if (player.inventory.currentItem > 0)
                    player.inventory.currentItem--;
                else
                    player.inventory.currentItem = Config.numberOfHotbars * 9 - 1;
            }
            event.setCanceled(true);
        }
    }
}
