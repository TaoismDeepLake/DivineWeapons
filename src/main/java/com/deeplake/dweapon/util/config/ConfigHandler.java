package com.deeplake.dweapon.util.config;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;

    public static int TEST_INT = 1;
    public static float TEST_FLOAT = 2f;
    public static String WELCOME_MSG = "The White Tower illuminates you.";

    //Crafting
    public static boolean ALLOW_SKY_CRAFT = true;

    public static void init(File file)
    {
        config = new Configuration(file);

        String catergory;

        catergory = "GENERAL";
        config.addCustomCategoryComment(catergory, "COMMENT 1");
        TEST_INT = config.getInt("TEST_INT", catergory, 1, 0, 999, "For testing");
        WELCOME_MSG = config.getString("WELCOME_MSG", catergory, "The White Tower illuminates you.", "The text shown when a player logs in.");

        catergory = "TYPE_TWO";
        config.addCustomCategoryComment(catergory, "COMMENT 2");
        TEST_FLOAT = config.getFloat("TEST_INT", catergory, 1, 0, 999, "For testing float");
       //WELCOME_MSG = config.getString("WELCOME_MSG", catergory, "The White Tower illuminates you.", "The text shown when a player logs in.");

        config.save();
    }

    public  static void registerConfig(FMLPreInitializationEvent event)
    {
        DWeapons.config = new File((event.getModConfigurationDirectory() + "/" + Reference.MOD_ID));
        DWeapons.config.mkdirs();
        init(new File(DWeapons.config.getPath() + "/" + Reference.MOD_ID + ".cfg"));

    }
}
