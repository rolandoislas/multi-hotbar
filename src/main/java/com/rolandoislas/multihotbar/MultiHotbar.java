package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod(modid = MultiHotbar.MODID, version = MultiHotbar.VERSION, name = MultiHotbar.NAME, acceptableRemoteVersions = "*",
    guiFactory = "com.rolandoislas.multihotbar.GuiFactory", canBeDeactivated = true)
public class MultiHotbar
{
    public static final String MODID = "multihotbar";
    public static final String VERSION = "1.0";
    public static final String NAME = "Multi-Hotbar";
    @Mod.Instance(MODID)
    @SuppressWarnings("unused")
    public static MultiHotbar instance;
    @SidedProxy(clientSide = "com.rolandoislas.multihotbar.proxy.ClientProxy",
            serverSide = "com.rolandoislas.multihotbar.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
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

    @NetworkCheckHandler
    @SuppressWarnings("unused")
    public boolean checkServerhasMod(Map<String,String> listData, Side side) {
        boolean hasMod = false;
        for (Map.Entry<String, String> entry : listData.entrySet())
            if (entry.getKey().equalsIgnoreCase(MODID))
                hasMod = true;
        // Set the hotbar to 1 (basically vanilla) if server does not have mod
        if (!hasMod)
            Config.numberOfHotbars = 1;
        else
            Config.reload();
        // Always return true to allow Forge to connect to server
        return true;
    }
}
