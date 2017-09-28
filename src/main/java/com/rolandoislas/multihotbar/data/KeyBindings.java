package com.rolandoislas.multihotbar.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

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
    private static KeyBinding[] hotbarKeys;
    public static KeyBinding previousHotbar;

    public static void load() {
        scrollModifier = new KeyBinding(getDescription("scrollModifier"), Keyboard.KEY_LMENU, CATEGORY_GENERAL);
        showDefaultHotbar = new KeyBinding(getDescription("showDefaultHotbar"), Keyboard.KEY_GRAVE, CATEGORY_GENERAL);
        nextHotbar = new KeyBinding(getDescription("nextHotbar"), Keyboard.KEY_X, CATEGORY_GENERAL);
        previousHotbar = new KeyBinding(getDescription("previousHotbar"), Keyboard.KEY_C, CATEGORY_GENERAL);
        ClientRegistry.registerKeyBinding(scrollModifier);
        ClientRegistry.registerKeyBinding(showDefaultHotbar);
        ClientRegistry.registerKeyBinding(nextHotbar);
        ClientRegistry.registerKeyBinding(previousHotbar);

        hotbarKeys = new KeyBinding[(Config.MAX_HOTBARS - 1) * InventoryPlayer.getHotbarSize()];
        for (int hotbar = 0; hotbar < Config.MAX_HOTBARS - 1; hotbar++) {
            for (int slot = 0; slot < InventoryPlayer.getHotbarSize(); slot++) {
                int key = hotbar * InventoryPlayer.getHotbarSize() + slot;
                hotbarKeys[key] = new KeyBinding(
                        getDescription("hotbarkey." + (key + InventoryPlayer.getHotbarSize() + 1)),
                        Keyboard.KEY_NONE, CATEGORY_HOTBAR_KEYS);
                ClientRegistry.registerKeyBinding(hotbarKeys[key]);
            }
        }
    }

    private static String getDescription(String description) {
        return String.format("key.%s.%s", Constants.MODID, description);
    }

    public static int isHotbarKeyDown() {
        KeyBinding[] bindings = new KeyBinding[Minecraft.getMinecraft().gameSettings.keyBindsHotbar.length +
                hotbarKeys.length];
        System.arraycopy(Minecraft.getMinecraft().gameSettings.keyBindsHotbar, 0, bindings, 0,
                Minecraft.getMinecraft().gameSettings.keyBindsHotbar.length);
        System.arraycopy(hotbarKeys, 0, bindings, Minecraft.getMinecraft().gameSettings.keyBindsHotbar.length,
                hotbarKeys.length);
        for (int i = 0; i < bindings.length; i++)
            if (bindings[i].isPressed())
                return i;
        return -1;
    }
}
