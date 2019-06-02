package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.item.ItemStack;

public interface IDWeaponEnhanceable {
	public static boolean IsSky(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_SKY);
	}
	
	public static void SetSky(ItemStack stack)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_SKY, true);
	}
	
	public static boolean IsNameHidden(ItemStack stack)
	{
		return DWNBTUtil.GetBooleanDF(stack, DWNBTDef.IS_NAME_HIDDEN, true);
	}
	
	public static void SetNameHidden(ItemStack stack, boolean isHidden)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_NAME_HIDDEN, isHidden);
	}
	
	public static boolean IsEarth(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_EARTH);
	}
	
	public static void SetEarth(ItemStack stack)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_EARTH, true);
	}
	
	public static int GetPearlCount(ItemStack stack)
	{
		return DWNBTUtil.GetInt(stack, DWNBTDef.PEARL_COUNT);
	}
	
	public static void SetPearlCount(ItemStack stack, int count)
	{
		if (!(stack.getItem() instanceof DWeaponSwordBase)) {
			return;
		}
		
		if (count > 0 && count < GetPearlMax(stack)) {
			DWNBTUtil.SetInt(stack, DWNBTDef.PEARL_COUNT, count);
		}
	}
	
	public static int GetPearlMax(ItemStack stack)
	{
		if (!(stack.getItem() instanceof DWeaponSwordBase)) {
			return 0;
		}
		return 5;//Most Weapons can socket 5 pearls
	}
	
	public static int GetPearlEmptySpace(ItemStack stack)
	{
		if (!(stack.getItem() instanceof DWeaponSwordBase)) {
			return 0;
		}
		if (IsSky(stack)) {
			return 0;//sky weapon needs no pearls to function.
		}
		
		return GetPearlMax(stack) - GetPearlCount(stack);
	}
	
	public static boolean IsManualReady(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_MANUAL_READY);
	}
	
	public static void SetManualReady(ItemStack stack, boolean isReady)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_MANUAL_READY, isReady);
	}
}
