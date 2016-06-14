package com.rolandoislas.multihotbar;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by Rolando on 6/10/2016.
 */
public class KeyBindings {
    public static KeyBinding scrollModifier;
    public static KeyBinding showDefaultHotbar;

    public static void load() {
        scrollModifier = new KeyBinding("key.scrollModifier", Keyboard.KEY_LMENU, "key.category.main");
        showDefaultHotbar = new KeyBinding("key.showDefaultHotbar", Keyboard.KEY_GRAVE, "key.category.main");
        ClientRegistry.registerKeyBinding(scrollModifier);
        ClientRegistry.registerKeyBinding(showDefaultHotbar);
    }

    public static int isHotbarKeyDown() {
        KeyBinding[] bindings = Minecraft.getMinecraft().gameSettings.keyBindsHotbar;
        for (int i = 0; i < bindings.length; i++)
            if (bindings[i].isPressed())
                return i;
        return -1;
    }
}
