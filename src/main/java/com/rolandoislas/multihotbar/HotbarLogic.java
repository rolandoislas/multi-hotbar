package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.data.Constants;
import com.rolandoislas.multihotbar.data.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    private static boolean showDefaultToggle;
    public static final int VANILLA_HOTBAR_SIZE = 9;
    private static boolean hasCoreMod;
    private static boolean sentPlayerMessage;

    /**
     * Checks if the custom hotbar should be shown.
     * Takes other variables into account.
     * @return boolean
     */
    static boolean shouldShowDefault() {
        boolean isSpectator = Minecraft.getMinecraft().player != null &&
                Minecraft.getMinecraft().player.isSpectator();
        boolean modifierEnabled = KeyBindings.scrollModifier.isKeyDown() && Config.singleHotbarModeShowOnModiferKey;
        return getShowDefaultToggle() || isSpectator || (!modifierEnabled && Config.singleHotbarMode) || !hasCoreMod;
    }

    /**
     * Set the state of the vanilla hotbar visibility user toggle
     * @param showDefaultToggle should the normal hotbar be shown
     */
    private static void setShowDefaultToggle(boolean showDefaultToggle) {
        if (showDefaultToggle)
            Minecraft.getMinecraft().player.inventory.currentItem = 0;
        HotbarLogic.showDefaultToggle = showDefaultToggle;
    }

    /**
     * Get the state of the vanilla hotbar visibility user toggle
     * This does not take any other variable into account.
     */
    private static boolean getShowDefaultToggle() {
        return showDefaultToggle;
    }

    /**
     * Sets the missing core mod variable
     * @param hasCoreMod if the core mod is installed on the server
     */
    public static void setHasCoreMod(boolean hasCoreMod) {
        HotbarLogic.hasCoreMod = hasCoreMod;
    }

    /**
     * Set weather or not the player has been sent a message.
     * @param sentPlayerMessage has the player been sent a message
     */
    public static void setSentPlayerMessage(boolean sentPlayerMessage) {
        HotbarLogic.sentPlayerMessage = sentPlayerMessage;
    }

    /**
     * Handles mouse scroll for hotbar
     * @param event mouse event
     */
    public void mouseEvent(MouseEvent event) {
        if (!hasCoreMod || getShowDefaultToggle() || event.isCanceled())
            return;
        // Scrolled
        if (event.getDwheel() != 0) {
            updateTooltips();
            // Handle hotbar selector scroll
            EntityPlayer player = Minecraft.getMinecraft().player;
            // Calculate next/previous hotbar
            int nextHotbar = getNextHotbarWithCustomOrder();
            int previousHotbar = getPreviousHotbarWithCustomOrder();
            // Scrolled right
            if (event.getDwheel() < 0) {
                // Modifier button is pressed. Move to next hotbar
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToNextHotbar();
                // The end of the current hotbar has been reached. Move to the beginning of the next one.
                else if (player.inventory.currentItem % VANILLA_HOTBAR_SIZE == VANILLA_HOTBAR_SIZE - 1) {
                    if (!shouldShowDefault())
                        player.inventory.currentItem = (Config.inverseScrollDirection ? previousHotbar : nextHotbar) *
                                VANILLA_HOTBAR_SIZE;
                    else
                        player.inventory.currentItem = getCurrentHotbar() * VANILLA_HOTBAR_SIZE;
                }
                // Move to next item
                else
                   player.inventory.currentItem++;
            }
            // Scrolled left
            else {
                // Modifier is pressed. Move to previous hotbar.
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToPreviousHotbar();
                // The beginning of the current hotbar has been reached. Move to the end of the previous one.
                else if (player.inventory.currentItem % 9 == 0) {
                    if (!shouldShowDefault())
                        player.inventory.currentItem = (Config.inverseScrollDirection ? nextHotbar : previousHotbar) *
                                VANILLA_HOTBAR_SIZE + VANILLA_HOTBAR_SIZE - 1;
                    else
                        player.inventory.currentItem = getCurrentHotbar() * VANILLA_HOTBAR_SIZE +
                                VANILLA_HOTBAR_SIZE - 1;
                }
                // Move to previous item
                else
                    player.inventory.currentItem--;
            }
            event.setCanceled(true);
            resetTooltipTicks();
        }
        else if (event.isButtonstate()) {
            // FIXME the event registers as a button event on button down, not up
            //keyPressed();
        }
    }

    /**
     * Get the previous hotbar based on the custom order
     * @return hotbar index
     */
    private static int getPreviousHotbarWithCustomOrder() {
        int currentHotbar =getCurrentHotbarWithCustomOrder();
        return currentHotbar == 0 ?
                Config.hotbarOrder[Config.numberOfHotbars - 1] :
                Config.hotbarOrder[currentHotbar - 1];
    }

    /**
     * Get the next hotbar based on the custom order
     * @return hotbar index
     */
    private static int getNextHotbarWithCustomOrder() {
        int currentHotbar = getCurrentHotbarWithCustomOrder();
        return currentHotbar >= Config.numberOfHotbars - 1 ?
                Config.hotbarOrder[0] :
                Config.hotbarOrder[currentHotbar + 1];
    }

    /**
     * Get the current hotbar index based on the custom order.
     * @return custom order index
     */
    private static int getCurrentHotbarWithCustomOrder() {
        int currentHotbar = 0;
        for (int order : Config.hotbarOrder) {
            if (order == getCurrentHotbar())
                break;
            currentHotbar++;
        }
        return currentHotbar;
    }

    /**
     * Returns the hotbar size based on the current amount of visible hotbars
     * @return size
     */
    public static int getHotbarSize() {
        if (shouldShowDefault())
            return VANILLA_HOTBAR_SIZE;
        return Config.numberOfHotbars * VANILLA_HOTBAR_SIZE;
    }

    /**
     * Go to previous hotbar keeping currently selected item. Loops to last hotbar.
     * @param force ignore the inverse config option
     */
    private static void moveSelectionToPreviousHotbar(boolean force) {
        if (Config.inverseScrollDirection && !force) {
            moveSelectionToNextHotbar(true);
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        int previousHotbarStartIndex = getPreviousHotbarWithCustomOrder() * VANILLA_HOTBAR_SIZE;
        if (previousHotbarStartIndex - VANILLA_HOTBAR_SIZE < 0)
            player.inventory.currentItem = Config.hotbarOrder[0] * VANILLA_HOTBAR_SIZE +
                    player.inventory.currentItem % 9;
        else
            player.inventory.currentItem = previousHotbarStartIndex + player.inventory.currentItem % 9;
    }

    /**
     * @see HotbarLogic#moveSelectionToPreviousHotbar(boolean)
     */
    private static void moveSelectionToPreviousHotbar() {
        moveSelectionToPreviousHotbar(false);
    }

    /**
     * Go to next hotbar. Loops to first hotbar.
     * @param force ignore the inverse config option
     */
    private static void moveSelectionToNextHotbar(boolean force) {
        if (Config.inverseScrollDirection && !force) {
            moveSelectionToPreviousHotbar(true);
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        int nextHotbar = getNextHotbarWithCustomOrder();
        int nextHotbarStartIndex = nextHotbar * VANILLA_HOTBAR_SIZE;
        if (nextHotbarStartIndex + player.inventory.currentItem % 9 >= nextHotbarStartIndex + 9 ||
                Arrays.asList(Config.hotbarOrder).indexOf(nextHotbar) >= Config.numberOfHotbars)
            player.inventory.currentItem = Config.hotbarOrder[0] * VANILLA_HOTBAR_SIZE +
                    player.inventory.currentItem % 9;
        else
            player.inventory.currentItem = nextHotbarStartIndex + player.inventory.currentItem % 9;
    }

    /**
     * @see HotbarLogic#moveSelectionToNextHotbar(boolean)
     */
    private static void moveSelectionToNextHotbar() {
        moveSelectionToNextHotbar(false);
    }

    /**
     * Handles button events
     */
    public void keyPressed() {
        // Check toggle key
        if (KeyBindings.showDefaultHotbar.isPressed()) {
            setShowDefaultToggle(!getShowDefaultToggle());
            Minecraft.getMinecraft().gameSettings.heldItemTooltips = shouldShowDefault();
        }
        // Update tool tip in case of modifier key
        updateTooltips();
        // Check for vanilla's save and restore hotbar feature
        if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLoadToolbar.getKeyCode()) ||
                Mouse.isButtonDown(Minecraft.getMinecraft().gameSettings.keyBindLoadToolbar.getKeyCode()) ||
                Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSaveToolbar.getKeyCode()) ||
                Mouse.isButtonDown(Minecraft.getMinecraft().gameSettings.keyBindSaveToolbar.getKeyCode())) {
            int savedHotbar = KeyBindings.isHotbarKeyDown(true, true);
            if (savedHotbar > -1) {
                resetTooltipTicks();
                KeyBindings.nextHotbarWasPressed = false;
                KeyBindings.previousHotbarWasPressed = false;
                return;
            }
        }
        // Default render
        if ((HotbarLogic.shouldShowDefault() && !Config.singleHotbarMode) || getShowDefaultToggle() || !hasCoreMod)
            return;
        // Check hotbar keys
        int slot = KeyBindings.isHotbarKeyDown();
        int currentItem = Minecraft.getMinecraft().player.inventory.currentItem;
        // Change hotbar if modifier key is down and a number is pressed
        if (slot > -1 && KeyBindings.scrollModifier.isKeyDown() && slot < Config.numberOfHotbars)
            moveSelectionToHotbar(Config.hotbarOrder[Config.inverseScrollDirection ? Config.numberOfHotbars - 1 - slot :
                    slot]);
        // Change hotbars if pressed number matches currently selected slot
        else if (slot > -1 && currentItem % 9 == slot % 9 && Config.numberOfHotbars > 1 &&
                slot < VANILLA_HOTBAR_SIZE && Config.doubleTapMovesToNextHotbar)
            moveSelectionToNextHotbar();
        // Select a slot
        else if (slot > - 1) {
            int hotbar = (int) Math.floor(slot/ 9);
            if (slot < VANILLA_HOTBAR_SIZE && Config.relativeHotbarKeys)
                hotbar = getCurrentHotbarWithCustomOrder();
            if (hotbar < Config.numberOfHotbars)
                Minecraft.getMinecraft().player.inventory.currentItem = Config.hotbarOrder[hotbar] *
                        VANILLA_HOTBAR_SIZE + slot % VANILLA_HOTBAR_SIZE;
        }
        if (slot > -1)
            resetTooltipTicks();
        // Next hotbar key
        if (KeyBindings.nextHotbar.isPressed()) {
            KeyBindings.nextHotbarWasPressed = true;
        }
        else if (KeyBindings.nextHotbarWasPressed) {
            moveSelectionToNextHotbar();
            KeyBindings.nextHotbarWasPressed = false;
        }
        // Previous hotbar key
        if (KeyBindings.previousHotbar.isPressed()) {
            KeyBindings.previousHotbarWasPressed = true;
        }
        else if (KeyBindings.previousHotbarWasPressed) {
            moveSelectionToPreviousHotbar();
            KeyBindings.previousHotbarWasPressed = false;
        }
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
        int tries = 0;
        while (getCurrentHotbar() != index && tries < Config.numberOfHotbars) {
            moveSelectionToNextHotbar();
            tries++;
        }
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

    /**
     * Send the player a message if the client is connected to a server without the core mod.
     * @param event player tick
     */
    public static void sendPlayerMessage(TickEvent.PlayerTickEvent event) {
        if (hasCoreMod || sentPlayerMessage || event.player != Minecraft.getMinecraft().player)
            return;
        event.player.sendMessage(new TextComponentTranslation(
                String.format("%s.message.core_mod_missing", Constants.MODID)));
        setSentPlayerMessage(true);
    }
}
