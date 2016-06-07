package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.Config;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Rolando on 6/6/2016.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Config.load(event.getSuggestedConfigurationFile());
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}
}
