package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

import java.util.Map;

@Mod(modid = MultiHotbar.MODID, version = MultiHotbar.VERSION, name = MultiHotbar.NAME, acceptableRemoteVersions = "*",
    guiFactory = "com.rolandoislas.multihotbar.GuiFactory", canBeDeactivated = true)
public class MultiHotbar
{
    public static final String MODID = "multihotbar";
    public static final String VERSION = "2.1.2";
    public static final String NAME = "Multi-Hotbar";
    @Mod.Instance(MODID)
    @SuppressWarnings("unused")
    public static MultiHotbar instance;
    @SidedProxy(clientSide = "com.rolandoislas.multihotbar.proxy.ClientProxy",
            serverSide = "com.rolandoislas.multihotbar.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
		proxy.init(event);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
