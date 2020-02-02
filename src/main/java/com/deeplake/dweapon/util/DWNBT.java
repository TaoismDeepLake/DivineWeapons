package com.deeplake.dweapon.util;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import scala.tools.nsc.typechecker.Tags;

import static com.deeplake.dweapon.util.NBTStrDef.DWNBTDef.BASE_DATA;

public class DWNBT {
	public final static int TICK_PER_SECOND = 20;

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

	public static NBTTagString GetTranslatedTagString(String key)
	{
		return new NBTTagString(ITextComponent.Serializer.componentToJson(new TextComponentTranslation(key)));
	}

	public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
		if(tag == null) {
			return new NBTTagCompound();
		}

		return tag.getCompoundTag(key);
	}

	public static NBTTagCompound getBaseTag(NBTTagCompound root) {
		return getTagSafe(root, BASE_DATA);
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
		
		return DWNBT.GetTranslatedTagString(str);
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
		return bookPageFromLine(key);
	}
	
	public static NBTTagString bookPageFromUnlocalizedLine(String key, String url)
	{
		return bookPageFromLineAndUrl(key, url);
	}
	
	public static NBTTagString bookPageFromUnlocalizedLine(String key, EntityPlayer player)
	{
		return DWNBT.GetTranslatedTagString(key);
//		String playerName = I18n.format(DWNBTDef.DEFAULT_PLAYER_NAME);
//		if (player != null) {
//			playerName = player.getDisplayNameString();
//		}
//
//		String result = I18n.format(key).replace("{PlayerName}", playerName);
//		return bookPageFromLine(result);
	}
}
