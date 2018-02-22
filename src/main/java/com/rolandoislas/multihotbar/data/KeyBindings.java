package com.rolandoislas.multihotbar.data;

import com.rolandoislas.multihotbar.HotbarLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rolando on 6/10/2016.
 */
public class KeyBindings {
    private static final String CATEGORY_GENERAL = String.format("key.%s.category.main", Constants.MODID);
    private static final String CATEGORY_HOTBAR_KEYS = String.format("key.%s.category.hotbarkeys",
            Constants.MODID);
    public static KeyBinding scrollModifier;
    public static KeyBinding showDefaultHotbar;
    public static KeyBinding nextHotbar;
    private static List<KeyBinding> hotbarKeys;
    public static KeyBinding previousHotbar;
    public static boolean nextHotbarWasPressed;
    public static boolean previousHotbarWasPressed;
    public static KeyBinding scrollIgnore;

    public static void load() {
        scrollModifier = new KeyBinding(getDescription("scrollModifier"), Keyboard.KEY_LMENU, CATEGORY_GENERAL);
        showDefaultHotbar = new KeyBinding(getDescription("showDefaultHotbar"), Keyboard.KEY_GRAVE, CATEGORY_GENERAL);
        nextHotbar = new KeyBinding(getDescription("nextHotbar"), Keyboard.KEY_X, CATEGORY_GENERAL);
        previousHotbar = new KeyBinding(getDescription("previousHotbar"), Keyboard.KEY_C, CATEGORY_GENERAL);
        scrollIgnore = new KeyBinding(getDescription("scrollIgnore"), Keyboard.KEY_NONE, CATEGORY_GENERAL);
        ClientRegistry.registerKeyBinding(scrollModifier);
        ClientRegistry.registerKeyBinding(showDefaultHotbar);
        ClientRegistry.registerKeyBinding(nextHotbar);
        ClientRegistry.registerKeyBinding(previousHotbar);
        ClientRegistry.registerKeyBinding(scrollIgnore);

        hotbarKeys = new ArrayList<>();
        for (int slot = HotbarLogic.VANILLA_HOTBAR_SIZE + 1; slot <= InventoryPlayer.getHotbarSize(); slot++) {
            hotbarKeys.add(new KeyBinding(
                    getDescription("hotbarkey." + slot),
                    Keyboard.KEY_NONE, CATEGORY_HOTBAR_KEYS));
            ClientRegistry.registerKeyBinding(hotbarKeys.get(hotbarKeys.size() - 1));
        }
    }

    private static String getDescription(String description) {
        return String.format("key.%s.%s", Constants.MODID, description);
    }

    public static int isHotbarKeyDown(boolean defaultOnly, boolean overrideSafe) {
        List<KeyBinding> bindings = new ArrayList<>();
        Collections.addAll(bindings, Minecraft.getMinecraft().gameSettings.keyBindsHotbar);
        if (!defaultOnly)
            bindings.addAll(hotbarKeys);
        for (KeyBinding keyBinding : bindings) {
            if (overrideSafe) {
                if ((Keyboard.isKeyDown(keyBinding.getKeyCode()) || Mouse.isButtonDown(keyBinding.getKeyCode())))
                    return bindings.indexOf(keyBinding);
            }
            else {
                if (keyBinding.isPressed())
                    return bindings.indexOf(keyBinding);
            }
        }
        return -1;
    }

    public static int isHotbarKeyDown() {
        return isHotbarKeyDown(false, false);
    }
}
