package com.deeplake.dweapon.util;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
		//str.replace("{PlayerName}", replacement)
		
		return new NBTTagString("[\"\",{\"text\":\"" + str + "\"}]");
	}
	
	public static NBTTagString bookPageFromLineAndUrl(String str, String url)
	{
		String textStr = "\"text\":\"" + str + "\"";
		String styleStr = "\"color\": \"dark_blue\",\"underlined\": true";
		String linkStr = "\"clickEvent\": { \"action\": \"open_url\",  \"value\": \""+ url +"\"}";
		return new NBTTagString("[{" + textStr + "," + styleStr + "," + linkStr +
		"}]");
	}
	
	public static NBTTagString bookPageFromUnlocalizedLine(String key)
	{
		return bookPageFromLine(I18n.format(key));
	}
	
	public static NBTTagString bookPageFromUnlocalizedLine(String key, String url)
	{
		return bookPageFromLineAndUrl(I18n.format(key), url);
	}
	
	public static NBTTagString bookPageFromUnlocalizedLine(String key, EntityPlayer player)
	{
		String playerName = I18n.format(DWNBTDef.DEFAULT_PLAYER_NAME);
		if (player != null) {
			playerName = player.getDisplayNameString();
		}

		String result = I18n.format(key).replace("{PlayerName}", playerName);
		return bookPageFromLine(result);
	}
}
