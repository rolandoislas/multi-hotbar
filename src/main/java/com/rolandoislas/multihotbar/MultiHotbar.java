package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.data.Constants;
import com.rolandoislas.multihotbar.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = Constants.MODID, version = Constants.VERSION, name = Constants.NAME,
    guiFactory = "com.rolandoislas.multihotbar.gui.GuiFactory", canBeDeactivated = true,
    acceptedMinecraftVersions = Constants.MC_VERSION, dependencies = "required-after:multihotbarcore")
public class MultiHotbar
{
    @Mod.Instance(Constants.MODID)
    @SuppressWarnings("unused")
    public static MultiHotbar instance;
    @SidedProxy(clientSide = "com.rolandoislas.multihotbar.proxy.ClientProxy",
            serverSide = "com.rolandoislas.multihotbar.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Logger logger;
    public static SimpleNetworkWrapper networkChannel;

    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
	    logger = event.getModLog();
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

    @SuppressWarnings("unused")
    @NetworkCheckHandler
    public boolean networkCheck(Map<String, String> mods, Side side) {
        boolean hasCore = false;
        for (String mod : mods.keySet())
            if (mod.equals("multihotbarcore"))
                hasCore = true;
        HotbarLogic.setHasCoreMod(hasCore);
        HotbarLogic.setSentPlayerMessage(false);
        return true;
    }
}
