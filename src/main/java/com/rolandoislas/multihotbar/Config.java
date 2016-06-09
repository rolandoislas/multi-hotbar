package com.rolandoislas.multihotbar;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Rolando on 6/6/2016.
 */
public class Config {
    public static int numberOfHotbars;
    private static Configuration config;

    public static void load(File suggestedConfigurationFile) {
        config = new Configuration(suggestedConfigurationFile);
        config.load();
        numberOfHotbars = config.getInt("Number of Hotbars", Configuration.CATEGORY_GENERAL, 2, 1, 2,
                "Defines the amount fo hotbars that should be displayed");
        config.save();
    }

    public static void reload() {
        load(config.getConfigFile());
    }
}
