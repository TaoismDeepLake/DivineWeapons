package com.deeplake.dweapon.init;

import java.util.ArrayList;
import java.util.List;

import com.deeplake.dweapon.item.ItemBase;
import com.deeplake.dweapon.item.misc.Heirloom;
import com.deeplake.dweapon.item.misc.SealedWeapon;
import com.deeplake.dweapon.item.weapon.*;
import com.deeplake.dweapon.util.Reference;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {

	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	/*
	WOOD(0, 59, 2.0F, 0.0F, 15),
    STONE(1, 131, 4.0F, 1.0F, 5),
    IRON(2, 250, 6.0F, 2.0F, 14),
    DIAMOND(3, 1561, 8.0F, 3.0F, 10),
    GOLD(0, 32, 12.0F, 0.0F, 22);
    
    harvestLevel, maxUses, efficiency, damage, enchantability 
	*/
	
	//Materials
	public static final ToolMaterial TOOL_MATERIAL_DIVINE = 
			EnumHelper.addToolMaterial("material_divine", 3, 1024, 10F, 3F, 0);
	//Armor
//	public static final ArmorMaterial ARMOR_MATERIAL_COPPER = 
//			EnumHelper.addArmorMaterial("armor_material_copper", Reference.MOD_ID + ":copper", 14,
//					new int[] {2,5,7,3}, 10, SoundEvents.BLOCK_ANVIL_HIT, 17);
	
	/*
	 * To Add an item, 
	 * - add a line here,
	 * - add name in lang
	 * - add a json and corresponding png
	 * - 
	 */
	
	public static final ItemBase WEAPON_PEARL = new ItemBase("weapon_pearl");
	public static final ItemBase DIVINE_INGOT = new ItemBase("divine_ingot");
	public static final ItemBase PURE_INGOT = new ItemBase("pure_ingot");
	//public static final ItemBase DIVINE_ANVIL = new ItemBase("divine_anvil");
	public static final ItemBase SKY_CHARM = new ItemBase("sky_charm");
	public static final ItemBase EARTH_CHARM = new ItemBase("earth_charm");
	
	public static final SealedWeapon SEALED_WEAPON = new SealedWeapon("sealed_weapon");
	public static final Heirloom HEIRLOOM = new Heirloom("heirloom");
	
	
	public static final DBloodSword BLOOD_SWORD = new DBloodSword("blood_sword", TOOL_MATERIAL_DIVINE);
	public static final DDeathSword DEATH_SWORD = new DDeathSword("death_sword", TOOL_MATERIAL_DIVINE);
	public static final DSpaceAffinitySword SPACE_SWORD = new DSpaceAffinitySword("space_affinity_sword", TOOL_MATERIAL_DIVINE);
	public static final DSageBuilder SAGE_BUILDER = new DSageBuilder("sage_builder", TOOL_MATERIAL_DIVINE);
	public static final DSnowSword SNOW_SWORD = new DSnowSword("snow_sword", TOOL_MATERIAL_DIVINE);
	
	public static final DPowerTriangle POWER_TRIANGLE = new DPowerTriangle("power_triangle", TOOL_MATERIAL_DIVINE);
	public static final DGoldSword GOLD_SWORD = new DGoldSword("gold_sword", TOOL_MATERIAL_DIVINE);
	
	public static final DTrueNameSword TRUE_NAME_SWORD = new DTrueNameSword("true_name_sword", TOOL_MATERIAL_DIVINE);
	public static final DFutureSword FURTUE_SWORD = new DFutureSword("future_sword", TOOL_MATERIAL_DIVINE);
	public static final DDisarmRing DISARM_RING = new DDisarmRing("disarm_ring", TOOL_MATERIAL_DIVINE);
	public static final DWaterSword WATER_SWORD = new DWaterSword("water_sword", TOOL_MATERIAL_DIVINE);
	public static final Item WEAPON_HANDLE = new ItemBase("weapon_handle");

	public static final DMonkBeads MONK_BEADS = new DMonkBeads("monk_beads", TOOL_MATERIAL_DIVINE);
	//public static final Item COPPER_HELMET = new ArmorBase("copper_helmet", ARMOR_MATERIAL_COPPER, 1, EntityEquipmentSlot.HEAD);
	
	//public static final Item BUFF_FOOD_1 = new FoodBase("buff_food_1", 8, 4.8f, false);
//	public static final Item BUFF_FOOD_1 = new FoodEffectBase("buff_food_1", 8, 4.8f, false,
//			new PotionEffect(MobEffects.FIRE_RESISTANCE, (60*20), 0, false, true));
}
