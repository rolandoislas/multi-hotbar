package com.rolandoislas.multihotbar;

import com.rolandoislas.multihotbar.proxy.CommonProxy;
import cpw.mods.fml.common.SidedProxy;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = MultiHotbar.MODID, version = MultiHotbar.VERSION)
public class MultiHotbar
{
    public static final String MODID = "multihotbar";
    public static final String VERSION = "1.0";
    @Mod.Instance(MODID)
    public static MultiHotbar instance;
    @SidedProxy(clientSide = "com.rolandoislas.multihotbar.proxy.ClientProxy",
            serverSide = "com.rolandoislas.multihotbar.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
		proxy.init();
    }
}
