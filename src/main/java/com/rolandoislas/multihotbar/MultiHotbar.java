package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.data.Constants;
import com.rolandoislas.multihotbar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
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

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
	    logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
		proxy.init(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SuppressWarnings("unused")
    @NetworkCheckHandler
    public boolean networkCheck(Map<String, String> mods, Side side) {
        // Ignore if the request is on the server
        if (side.isClient())
            return true;
        boolean hasCore = false;
        for (String mod : mods.keySet())
            if (mod.equals("multihotbarcore"))
                hasCore = true;
        HotbarLogic.setHasCoreMod(hasCore);
        HotbarLogic.setSentPlayerMessage(false);
        return true;
    }
}
