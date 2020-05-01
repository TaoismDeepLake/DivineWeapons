package com.deeplake.dweapon.util.config;

import com.deeplake.dweapon.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Config(modid = Reference.MOD_ID, category = "")
public class ModConfig {
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler {

        private EventHandler() {
        }

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Reference.MOD_ID)) {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

    @Config.LangKey("configgui.dweapon.category.Menu0.GENERAL_CONF")
    @Config.Comment("Divine Weapons config.")
    public static final GeneralConf GENERAL_CONF = new GeneralConf();

    public static class GeneralConf {
        /*
         * SKY
         */
        @Config.LangKey("dweapon.conf.general.give_starter_items")
        @Config.Comment("A starter manual and a heirloom will be given to new players.")
        @Config.RequiresMcRestart
        public boolean GIVE_STARTER_ITEMS = true;

    }

    @Config.LangKey("configgui.dweapon.category.Menu0.CraftConf")
    @Config.Comment("Crafting: Recipes of anvils, crafting and smelting.")
    public static final CraftConf CRAFT_CONF = new CraftConf();

    public static class CraftConf {
        @Config.LangKey("dweapon.conf.allow_craft_sky")
        @Config.Comment("Players can craft sky weapons with sky charms.")
        @Config.RequiresMcRestart
        public boolean ALLOW_SKY_CRAFT = true;

        @Config.LangKey("dweapon.conf.allow_pure_cycle_burn")
        @Config.Comment("Pure pearls can be burnt to pure ingots, causing an indefinite loop.")
        @Config.RequiresMcRestart
        public boolean ALLOW_PURE_CYCLE_BURN = true;

        @Config.LangKey("dweapon.conf.allow_craft_table_upgrade")
        @Config.Comment("Only affects sky and earth upgrades. If true, the player does not need anvils to do the upgrade.")
        @Config.RequiresMcRestart
        public boolean ALLOW_CRAFT_TABLE_UPGRADE = false;
    }

    @Config.LangKey("configgui.dweapon.category.Menu0.LotteryConf")
    @Config.Comment("Chances of getting weapons from sealed weapons.")
    public static final LotteryConf LOTTERY_CONF_NORM = new LotteryConf();

    @Config.LangKey("configgui.dweapon.category.Menu0.LotteryConf_Heirloom")
    @Config.Comment("Chances of getting weapons from heirloom.")
    public static final LotteryConf LOTTERY_CONF_HEIRLOOM = new LotteryConf();

    public static class LotteryConf {
        @Config.LangKey("item.blood_sword.name")
        @Config.Comment("Bloodsword")
        public int BLOOD_SWORD = 10;

        @Config.LangKey("item.death_sword.name")
        @Config.Comment("death_sword")
        public int death_sword = 10;

        @Config.LangKey("item.space_affinity_sword.name")
        @Config.Comment("space_affinity_sword")
        public int space_affinity_sword = 10;

        @Config.LangKey("item.sage_builder.name")
        @Config.Comment("sage_builder")
        public int sage_builder = 6;

        @Config.LangKey("item.snow_sword.name")
        @Config.Comment("snow_sword")
        public int snow_sword = 6;

        @Config.LangKey("item.power_triangle.name")
        @Config.Comment("power_triangle")
        public int power_triangle = 6;

        @Config.LangKey("item.gold_sword.name")
        @Config.Comment("gold_sword")
        public int gold_sword = 6;

        @Config.LangKey("item.true_name_sword.name")
        @Config.Comment("true_name_sword")
        public int true_name_sword = 10;

        @Config.LangKey("item.disarm_ring.name")
        @Config.Comment("disarm_ring")
        public int disarm_ring = 6;

        @Config.LangKey("item.water_sword.name")
        @Config.Comment("water_sword")
        public int water_sword = 10;

        @Config.LangKey("item.monk_beads.name")
        @Config.Comment("monk_beads")
        public int monk_beads = 6;

        //todo
    }

    @Config.LangKey("configgui.dweapon.category.Menu0.gameplay")
    @Config.Comment("Gameplay:Some gameplay options.")
    public static final GameplayConf GAMEPLAY_CONF = new GameplayConf();

    public static class GameplayConf {
        @Config.LangKey("conf.spawn.BLOOD_SWORD_DRAIN")
        @Config.Comment("Blood sword will hurt the user when user.")
        @Config.RequiresMcRestart
        public boolean BLOOD_SWORD_DRAIN = true;

        @Config.LangKey("conf.spawn.BLOOD_SWORD_SUICIDE")
        @Config.Comment("Blood sword can kill the user by using.")
        @Config.RequiresMcRestart
        public boolean BLOOD_SWORD_SUICIDE = true;

        @Config.LangKey("conf.spawn.DEATH_SWORD_REVIVE")
        @Config.Comment("The Death sword can revive player at 1/4 max durability.")
        @Config.RequiresMcRestart
        public boolean DEATH_SWORD_REVIVE = true;

        @Config.LangKey("conf.spawn.DEATH_SWORD_PREVENT_BREAK")
        @Config.Comment("The death sword won't break it self to revive the player.")
        @Config.RequiresMcRestart
        public boolean DEATH_SWORD_PREVENT_BREAK = true;

        @Config.LangKey("conf.spawn.MONK_BEADS_MAX_EFFECTIVE_LEVEL")
        @Config.Comment("The monk beads can't give xp when the player is over this level. (-1 = unlimited)")
        @Config.RequiresMcRestart
        public int MONK_BEADS_MAX_EFFECTIVE_LEVEL = -1;

        @Config.LangKey("conf.spawn.DISARM_COST_DURABILITY")
        @Config.Comment("The disarm ring will spent durability when disarming.")
        @Config.RequiresMcRestart
        public boolean DISARM_COST_DURABILITY = false;

        @Config.LangKey("conf.spawn.NAME_SWORD_HUMAN_FACTOR")
        @Config.Comment("The true name sword damage multiplier at correct name in human level")
        @Config.RequiresMcRestart
        public int NAME_SWORD_HUMAN_FACTOR = 2;

        @Config.LangKey("conf.spawn.NAME_SWORD_EARTH_FACTOR")
        @Config.Comment("The true name sword damage multiplier at correct name in earth level")
        @Config.RequiresMcRestart
        public int NAME_SWORD_EARTH_FACTOR = 3;

        @Config.LangKey("conf.spawn.NAME_SWORD_SKY_FACTOR")
        @Config.Comment("The true name sword damage multiplier at correct name in sky level")
        @Config.RequiresMcRestart
        public int NAME_SWORD_SKY_FACTOR = 5;

    }
}
