package com.deeplake.dweapon.util.NBTStrDef;

import javax.annotation.Nullable;

import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DWNBTUtil {
	public static NBTTagCompound getNBT(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null)
		{
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		return nbt;
	}
	
	public static NBTTagCompound getNBT(Entity entity) {
		NBTTagCompound nbt = entity.getEntityData();
		
		if(nbt == null) {
	      return new NBTTagCompound();
	    }

	    return nbt;
	  }
	
	
	public static NBTTagCompound getNBT(NBTTagCompound tag) {
	    if(tag == null) {
	      return new NBTTagCompound();
	    }

	    return tag;
	  }
	
	//writeEntityToNBT
	//readEntityFromNBT
	
	@Nullable
	public static boolean StackHasKey(ItemStack stack, String key)
	{
		return ! (stack.isEmpty() || !getNBT(stack).hasKey(key));
			
	}
	
	//Boolean
	public static boolean SetBoolean(ItemStack stack, String key, boolean value)
	{
		NBTTagCompound nbt = getNBT(stack);
		nbt.setBoolean(key, value);
		return true;
	}
	
	public static boolean GetBoolean(ItemStack stack, String key, boolean defaultVal)
	{	
		if (StackHasKey(stack, key))
		{
			NBTTagCompound nbt = getNBT(stack);
			return nbt.getBoolean(key);
		}		
		else
		{
			return defaultVal;
		}
	}
	
	public static boolean GetBoolean(ItemStack stack, String key)
	{	
		if (StackHasKey(stack, key))
		{
			NBTTagCompound nbt = getNBT(stack);
			return nbt.getBoolean(key);
		}		
		else
		{
			return false;
		}
	}
	//get with default val
	public static boolean GetBooleanDF(ItemStack stack, String key, boolean defaultVal)
	{	
		if (StackHasKey(stack, key))
		{
			NBTTagCompound nbt = getNBT(stack);
			return nbt.getBoolean(key);
		}		
		else
		{
			return defaultVal;
		}
	}
	
	//Integer
	public static boolean SetInt(ItemStack stack, String key, int value)
	{
		NBTTagCompound nbt = getNBT(stack);
		nbt.setInteger(key, value);
		return true;
	}
	
	public static int GetInt(ItemStack stack, String key, int defaultVal)
	{
		if (StackHasKey(stack, key))
		{
			NBTTagCompound nbt = getNBT(stack);
			return nbt.getInteger(key);
		}		
		else
		{
			return defaultVal;
		}
	}
	
	public static int GetInt(ItemStack stack, String key)
	{
		return GetInt(stack, key, 0);
	}
	
	//String
	public static String GetString(ItemStack stack, String key, String defaultVal)
	{
		if (StackHasKey(stack, key))
		{
			NBTTagCompound nbt = getNBT(stack);
			return nbt.getString(key);
		}		
		else
		{
			return defaultVal;
		}
	}
	
	public static boolean SetString(ItemStack stack, String key, String value)
	{
		NBTTagCompound nbt = getNBT(stack);
		nbt.setString(key, value);
		return true;
	}

	
	//entity
	@Nullable
	public static boolean EntityHasKey(Entity entity, String key)
	{
		return getNBT(entity).hasKey(key);
			
	}
	
	//Boolean
	public static boolean GetBoolean(Entity entity, String key, boolean defaultVal)
	{	
		if (EntityHasKey(entity, key))
		{
			NBTTagCompound nbt = getNBT(entity);
			return nbt.getBoolean(key);
		}		
		else
		{
			return defaultVal;
		}
	}
	
	public static boolean SetBoolean(Entity stack, String key, boolean value)
	{
		NBTTagCompound nbt = getNBT(stack);
		nbt.setBoolean(key, value);
		return true;
	}
		
}
