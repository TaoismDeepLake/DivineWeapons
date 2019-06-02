package com.deeplake.dweapon.util;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class DWNBT {
	public int pearlCount;
	public boolean isEarth;
	public boolean isSky;
	
	private final NBTTagCompound basic;
	
	public DWNBT()
	{
		pearlCount = 0;
		isEarth = false;
		isSky = false;
		
		basic = new NBTTagCompound();
	}
	
	public DWNBT(NBTTagCompound srcNBT)
	{
		readFromBasic(srcNBT);
		basic = srcNBT;
	}
	
	public void readFromBasic(NBTTagCompound tag)
	{
		if (tag != null)
		{			
			pearlCount = tag.getInteger(DWNBTDef.PEARL_COUNT);
			isEarth = tag.getBoolean(DWNBTDef.IS_EARTH);
			isSky = tag.getBoolean(DWNBTDef.IS_SKY);
		}
	}
	
	public void writeToBasic(NBTTagCompound tag)
	{
		if (tag == null)
		{			
			tag = new NBTTagCompound();
		}
		
		tag.setInteger(DWNBTDef.PEARL_COUNT, pearlCount);
		tag.setBoolean(DWNBTDef.IS_EARTH, isEarth);
		tag.setBoolean(DWNBTDef.IS_SKY, isSky);
	}
	
	public NBTTagCompound getBasic()
	{
		NBTTagCompound tag = basic.copy();
		writeToBasic(tag);

	    return tag;
	}
	
	public void save()
	{
		writeToBasic(basic);
	
	}
	
	public static NBTTagString bookPageFromLine(String str)
	{
		return new NBTTagString("[\"\",{\"text\":\"" + str + "\"}]");
	}
}
