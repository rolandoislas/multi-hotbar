package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.data.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    private static boolean showDefault = false;
    public static final int VANILLA_HOTBAR_SIZE = 9;

    /**
     * Checks if the custom hotbar should be shown
     * @return boolean
     */
    static boolean shouldShowDefault() {
        boolean isSpectator = Minecraft.getMinecraft().player != null &&
                Minecraft.getMinecraft().player.isSpectator();
        boolean modifierEnabled = KeyBindings.scrollModifier.isKeyDown() && Config.singleHotbarModeShowOnModiferKey;
        return getShowDefault() || isSpectator || (!modifierEnabled && Config.singleHotbarMode);
    }

    /**
     * Set the state of the hotbar.
     * @param showDefault should the normal hotbar be shown
     */
    public static void setShowDefault(boolean showDefault) {
        HotbarLogic.showDefault = showDefault;
    }

    /**
     * Get the state of the vanilla hotbar visibility
     */
    private static boolean getShowDefault() {
        return showDefault;
    }

    /**
     * Handles mouse scroll for hotbar
     * @param event mouse event
     */
    public void mouseEvent(MouseEvent event) {
        if ((HotbarLogic.shouldShowDefault() && !Config.singleHotbarMode) || HotbarLogic.getShowDefault())
            return;
        // Scrolled
        if (event.getDwheel() != 0) {
            updateTooltips();
            // Handle hotbar selector scroll
            EntityPlayer player = Minecraft.getMinecraft().player;
            // Scrolled right
            if (event.getDwheel() < 0) {
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToNextHotbar();
                else if (player.inventory.currentItem < getHotbarSize() - 1)
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
                else {
                    player.inventory.currentItem = getHotbarSize() - 1;
                }
            }
            event.setCanceled(true);
            resetTooltipTicks();
        }
    }

    public static int getHotbarSize() {
        if (shouldShowDefault())
            return VANILLA_HOTBAR_SIZE;
        return Config.numberOfHotbars * VANILLA_HOTBAR_SIZE;
    }

    /**
     * Go to previous hotbar keeping currently selected item. Loops to last hotbar.
     */
    private void moveSelectionToPreviousHotbar() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player.inventory.currentItem - VANILLA_HOTBAR_SIZE < 0)
            player.inventory.currentItem = getHotbarSize() -
                    Math.abs(player.inventory.currentItem - VANILLA_HOTBAR_SIZE);
        else
            player.inventory.currentItem -= VANILLA_HOTBAR_SIZE;
    }

    /**
     * Go to next hotbar. Loops to first hotbar.
     */
    private static void moveSelectionToNextHotbar() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player.inventory.currentItem + VANILLA_HOTBAR_SIZE >= getHotbarSize())
            player.inventory.currentItem = (player.inventory.currentItem + VANILLA_HOTBAR_SIZE) - getHotbarSize();
        else
            player.inventory.currentItem += VANILLA_HOTBAR_SIZE;
    }

    /**
     * Handles button events
     * @param event Key input
     */
    public void keyPressed(InputEvent.KeyInputEvent event) {
        // Check toggle key
        if (KeyBindings.showDefaultHotbar.isPressed()) {
            setShowDefault(!getShowDefault());
            Minecraft.getMinecraft().gameSettings.heldItemTooltips = shouldShowDefault();
        }
        // Update tool tip in case of modifier key
        updateTooltips();
        // Default render
        if ((HotbarLogic.shouldShowDefault() && !Config.singleHotbarMode) || getShowDefault())
            return;
        // Check hotbar keys
        int slot = KeyBindings.isHotbarKeyDown();
        int currentItem = Minecraft.getMinecraft().player.inventory.currentItem;
        // Change hotbar if modifier key is down and a number is pressed
        if (slot > -1 && KeyBindings.scrollModifier.isKeyDown() && slot < Config.numberOfHotbars)
            moveSelectionToHotbar(slot);
        // Change hotbars if pressed number matches currently selected slot
        else if (slot > -1 && currentItem % 9 == slot % 9 && Config.numberOfHotbars > 1 && !shouldShowDefault())
            moveSelectionToNextHotbar();
        // Select a slot
        else if (slot > - 1) {
            int hotbar = Math.floorDiv(slot, InventoryPlayer.getHotbarSize());
            if (hotbar < Config.numberOfHotbars) {
                Minecraft.getMinecraft().player.inventory.currentItem = slot % InventoryPlayer.getHotbarSize();
                // Custom slot key was pressed. Move to appropriate hotbar
                if (slot >= InventoryPlayer.getHotbarSize())
                    moveSelectionToHotbar(hotbar);
                // Standard handling
                else if (!Config.relativeHotbarKeys && !Config.singleHotbarMode)
                    moveSelectionToHotbar(0);
            }
        }
        if (slot > -1)
            resetTooltipTicks();
        // Next hotbar key
        if (KeyBindings.nextHotbar.isPressed())
            moveSelectionToNextHotbar();
        // Previous hotbar key
        if (KeyBindings.previousHotbar.isPressed())
            moveSelectionToPreviousHotbar();
    }

    /**
     * Set tooltip ticks to default show time
     */
    private void resetTooltipTicks() {
        HotBarRenderer.tooltipTicks = 128;
    }

    /**
     * Move to a specific hotbar.
     * @param index hotbar index
     */
    public static void moveSelectionToHotbar(int index) {
        while (getCurrentHotbar() != index)
            moveSelectionToNextHotbar();
    }

    /**
     * Get the current hotbar index based on the current item.
     * @return 0-Config$MAX_HOTBARS
     */
    static int getCurrentHotbar() {
        return (int) Math.floor(Minecraft.getMinecraft().player.inventory.currentItem / 9);
    }

    /**
     * Updates minecraft game config to show or hide vanilla item tooltips. Based on if the vanilla hotbar is shown.
     */
    private static void updateTooltips() {
        Minecraft.getMinecraft().gameSettings.heldItemTooltips = shouldShowDefault();
    }
}
