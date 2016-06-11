package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    public void mouseEvent(MouseEvent event) {
        // Scrolled
        if (event.dwheel != 0) {
            // Handle hotbar selector scroll
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            // Scrolled right
            if (event.dwheel < 0) {
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToNextHotbar();
                else if (player.inventory.currentItem < Config.numberOfHotbars * 9 - 1)
                    player.inventory.currentItem++;
                else
                    player.inventory.currentItem = 0;
            }
            // Scrolled left
            else {
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToPreviousHotbar();
                else if (player.inventory.currentItem > 0)
                    player.inventory.currentItem--;
                else
                    player.inventory.currentItem = Config.numberOfHotbars * 9 - 1;
            }
            event.setCanceled(true);
        }
    }

    private void moveSelectionToPreviousHotbar() {
        moveSelection(false);
    }

    private void moveSelection(boolean forward) {
        int currentItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        int index = (int) Math.floor(currentItem / 9);
        int slot = currentItem - index * 9;
        if (forward)
            index = index < Config.numberOfHotbars - 1 ? index + 1 : 0; // increment or reset to first hotbar
        else
            index = index > 0 ? index - 1 : Config.numberOfHotbars - 1; // Decrement or set to last hotbar
        Minecraft.getMinecraft().thePlayer.inventory.currentItem = index * 9 + slot;
    }

    private void moveSelectionToNextHotbar() {
        moveSelection(true);
    }

    public void keyPressed(InputEvent.KeyInputEvent event) {
        // Check hotbar keys
        int slot = KeyBindings.isHotbarKeyDown();
        int currentItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        int index = (int) Math.floor(currentItem / 9);
        // Change hotbars
        if (slot > -1 && currentItem == index * 9 + slot) {
            moveSelectionToNextHotbar();
        }
        // Select a slot
        else if (slot > - 1)
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = (Config.relativeHotbarKeys ? index * 9 : 0) +
                    slot;
    }
}
