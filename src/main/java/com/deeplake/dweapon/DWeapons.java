package com.deeplake.dweapon;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import com.deeplake.dweapon.init.ModRecipes;
import com.deeplake.dweapon.proxy.ProxyBase;
import com.deeplake.dweapon.util.Reference;
import com.deeplake.dweapon.world.ModWorldGen;

@Mod(modid = DWeapons.MODID, name = DWeapons.NAME, version = DWeapons.VERSION)
public class DWeapons
{
    public static final String MODID = "dweapon";
    public static final String NAME = "Divine Weapon";
    public static final String VERSION = "0.3.1";

    public static Logger logger;
    
    public static final boolean SHOW_WARN = true;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static ProxyBase proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        GameRegistry.registerWorldGenerator(new ModWorldGen(), 100);
    }

    @EventHandler
	public static void Init(FMLInitializationEvent event)
	{
		ModRecipes.Init();
	}
    
    public static void LogWarning(String str)
    {
    	if (SHOW_WARN) 
    	{
    		logger.warn(str);
    	}
    }

    public static void Log(String str)
    {
        if (SHOW_WARN)
        {
            logger.info(str);
        }
    }
}
