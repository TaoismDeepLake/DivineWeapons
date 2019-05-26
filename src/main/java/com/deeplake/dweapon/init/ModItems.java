package com.deeplake.dweapon.init;

import java.util.ArrayList;
import java.util.List;

import com.deeplake.dweapon.item.ItemBase;
import com.deeplake.dweapon.item.misc.SealedWeapon;
import com.deeplake.dweapon.item.weapon.DBloodSword;
import com.deeplake.dweapon.item.weapon.DDeathSword;
import com.deeplake.dweapon.item.weapon.DSageBuilder;
import com.deeplake.dweapon.item.weapon.DSnowSword;
import com.deeplake.dweapon.item.weapon.DSpaceAffinitySword;
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
	//public static final ItemBase DIVINE_ANVIL = new ItemBase("divine_anvil");
	public static final ItemBase SKY_CHARM = new ItemBase("sky_charm");
	public static final ItemBase EARTH_CHARM = new ItemBase("earth_charm");
	
	public static final SealedWeapon SEALED_WEAPON = new SealedWeapon("sealed_weapon");
	
	
	
	public static final DBloodSword BLOOD_SWORD = new DBloodSword("blood_sword", TOOL_MATERIAL_DIVINE);
	public static final DDeathSword DEATH_SWORD = new DDeathSword("death_sword", TOOL_MATERIAL_DIVINE);
	public static final DSpaceAffinitySword SPACE_SWORD = new DSpaceAffinitySword("space_affinity_sword", TOOL_MATERIAL_DIVINE);
	public static final DSageBuilder SAGE_BUILDER = new DSageBuilder("sage_builder", TOOL_MATERIAL_DIVINE);
	public static final DSnowSword SNOW_SWORD = new DSnowSword("snow_sword", TOOL_MATERIAL_DIVINE);
	
	//public static final Item ANGEL_STATUE = new ItemBase("angel_statue");
	
	//public static final ItemSword COPPER_SWORD = new ToolSword("copper_sword", MATERIAL_COPPER);
	
	//public static final Item COPPER_HELMET = new ArmorBase("copper_helmet", ARMOR_MATERIAL_COPPER, 1, EntityEquipmentSlot.HEAD);
	
	//public static final Item BUFF_FOOD_1 = new FoodBase("buff_food_1", 8, 4.8f, false);
//	public static final Item BUFF_FOOD_1 = new FoodEffectBase("buff_food_1", 8, 4.8f, false,
//			new PotionEffect(MobEffects.FIRE_RESISTANCE, (60*20), 0, false, true));
}
