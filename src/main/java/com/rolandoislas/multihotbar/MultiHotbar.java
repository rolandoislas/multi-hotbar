package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = MultiHotbar.MODID, version = MultiHotbar.VERSION, name = MultiHotbar.NAME, acceptableRemoteVersions = "*",
    guiFactory = "com.rolandoislas.multihotbar.gui.GuiFactory", canBeDeactivated = true,
    acceptedMinecraftVersions = MultiHotbar.MC_VERSION)
public class MultiHotbar
{
    public static final String MODID = "multihotbar";
    public static final String VERSION = "2.1.3";
    public static final String NAME = "Multi-Hotbar";
    // https://maven.apache.org/pom.html#Dependency_Version_Requirement_Specification
    public static final String MC_VERSION = "[1.10,1.10.2]";
    @Mod.Instance(MODID)
    @SuppressWarnings("unused")
    public static MultiHotbar instance;
    @SidedProxy(clientSide = "com.rolandoislas.multihotbar.proxy.ClientProxy",
            serverSide = "com.rolandoislas.multihotbar.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Logger logger;

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
}
