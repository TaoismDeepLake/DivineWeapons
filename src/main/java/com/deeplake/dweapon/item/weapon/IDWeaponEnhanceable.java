package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.item.ItemStack;

public interface IDWeaponEnhanceable {
	public boolean IsSky(ItemStack stack);

	public void SetSky(ItemStack stack);

	public static boolean IsNameHidden(ItemStack stack)
	{
		return DWNBTUtil.GetBooleanDF(stack, DWNBTDef.IS_NAME_HIDDEN, true);
	}
	
	public static void SetNameHidden(ItemStack stack, boolean isHidden)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_NAME_HIDDEN, isHidden);
	}
	
	public boolean IsEarth(ItemStack stack);

	public void SetEarth(ItemStack stack);

	public int GetPearlCount(ItemStack stack);

	public void SetPearlCount(ItemStack stack, int count);
	
	public int GetPearlMax(ItemStack stack);

	public int GetPearlEmptySpace(ItemStack stack);

	public static boolean IsManualReady(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_MANUAL_READY);
	}
	
	public static void SetManualReady(ItemStack stack, boolean isReady)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_MANUAL_READY, isReady);
	}
}
