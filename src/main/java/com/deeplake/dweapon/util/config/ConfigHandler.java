package com.deeplake.dweapon.util.config;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;

    //GENERAL
    public static boolean GIVE_STARTER_ITEMS = true;

    //Crafting
    public static boolean ALLOW_SKY_CRAFT = true;
    public static boolean ALLOW_PURE_CYCLE_BURN = true;
    public static boolean ALLOW_CRAFT_TABLE_UPGRADE = false;

    //Gameplay
    public static boolean BLOOD_SWORD_DRAIN = true;
    public static boolean BLOOD_SWORD_SUICIDE = false;
    public static boolean DEATH_SWORD_REVIVE = true;
    public static boolean DEATH_SWORD_PREVENT_BREAK = false;

    public static void init(File file)
    {
        config = new Configuration(file);

        String category;

        category = "GENERAL";
        config.addCustomCategoryComment(category, "Divine Weapons config");
        GIVE_STARTER_ITEMS = config.getBoolean("GIVE_STARTER_ITEMS", category, true, "A starter manual and a heirloom will be given to new players.");

        category = "CRAFTING";
        config.addCustomCategoryComment(category, "Recipes of anvils, crafting and smelting.");
        ALLOW_SKY_CRAFT = config.getBoolean("ALLOW_SKY_CRAFT", category, true, "Players can craft sky weapons with sky charms.");
        ALLOW_PURE_CYCLE_BURN = config.getBoolean("ALLOW_PURE_CYCLE_BURN", category, true, "Pure pearls can be burnt to pure ingots, causing an indefinite loop.");
        ALLOW_CRAFT_TABLE_UPGRADE = config.getBoolean("ALLOW_CRAFT_TABLE_UPGRADE", category, false, "Only affects sky and earth upgrades. If true, the player does not need anvils to do the upgrade.");

        category = "LOTTERY";
        config.addCustomCategoryComment(category, "Chances of getting weapons.");

        category = "GAMEPLAY";
        config.addCustomCategoryComment(category, "Some gameplay options.");
        BLOOD_SWORD_DRAIN = config.getBoolean("BLOOD_SWORD_DRAIN", category, true, "Blood sword will hurt the user when user.");
        BLOOD_SWORD_SUICIDE = config.getBoolean("BLOOD_SWORD_SUICIDE", category, true, "Blood sword can kill the user by using.");
        DEATH_SWORD_REVIVE = config.getBoolean("DEATH_SWORD_REVIVE", category, true, "The Death sword can revive player at 1/4 max durability.");
        DEATH_SWORD_PREVENT_BREAK = config.getBoolean("DEATH_SWORD_PREVENT_BREAK", category, false, "The death sword won't break it self to revive the player.");

        config.save();
    }

    public  static void registerConfig(FMLPreInitializationEvent event)
    {
        DWeapons.config = new File((event.getModConfigurationDirectory() + "/" + Reference.MOD_ID));
        DWeapons.config.mkdirs();
        init(new File(DWeapons.config.getPath() + "/" + Reference.MOD_ID + ".cfg"));

    }
}
