package com.rolandoislas.multihotbar.gui;

import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.data.Constants;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Rolando on 6/10/2016.
 */
public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {
    public GuiConfig(GuiScreen parentScreen) {
        super(parentScreen,
                new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                Constants.MODID,
                false, // Require world reload
                false, // Require Minecraft reload
                Constants.NAME + " Config"); // Title
        titleLine2 = Config.config.getConfigFile().getAbsolutePath();
    }
}
