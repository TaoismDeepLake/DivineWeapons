package com.deeplake.dweapon.init.events;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.misc.Heirloom;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;
import com.deeplake.dweapon.util.DWNBT;
import com.deeplake.dweapon.util.Reference;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModStarterEvents {
	
	public static final String TAG_PLAYER_HAS_BOOK = DWNBTDef.STARTER_BOOK_GIVEN;

	//if the event subscriber is static, so should the events be
	//Thanks Cadiboo for telling me that
	  @SubscribeEvent
	  public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		  EntityPlayer player = event.player;
		  
	      boolean isBookGiven = DWNBTUtil.GetBoolean(event.player, TAG_PLAYER_HAS_BOOK, false);
	      
	      //DWeapons.LogWarning(String.format("TAG_PLAYER_HAS_BOOK = %s", isBookGiven));
	      if(!isBookGiven) {
	    	  ItemStack heirloom = new ItemStack(ModItems.HEIRLOOM);
	    	  DWeaponSwordBase.SetOwner(heirloom, player.getDisplayNameString());
	    	  
	    	  event.player.addItemStackToInventory(heirloom);
	    	  event.player.addItemStackToInventory(CreateManual(player));
	    	  DWNBTUtil.SetBoolean(event.player, TAG_PLAYER_HAS_BOOK, true);
	    	  DWeapons.LogWarning(String.format("Given starter items to player %s", player.getDisplayNameString()));
	      }
	  }
	      
	  public static ItemStack CreateManual(EntityPlayer player) {
		  ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
			
			// https://minecraftjson.com/
			
			// /give @p written_book{pages:["[\"\",{\"text\":\"123 45\"}]","[\"\",{\"text\":\"678 90\"}]"],title:CustomBook,author:Player}
			
			NBTTagList bookPages = new NBTTagList();
			String name = "starter";
			
			String pageCountString = I18n.format(name + DWNBTDef.MANUAL_PAGE_COUNT);
			
			int pageCount = 0;
			boolean hasManual = false;
			try {
				pageCount = Integer.parseInt(pageCountString);
				hasManual = true;
			} catch (NumberFormatException e) {
				pageCount = 0;
			}
			
			if (hasManual)
			{
				int i = 1;
				for (i = 1; i < pageCount; i++)
				{
					bookPages.appendTag(DWNBT.bookPageFromUnlocalizedLine(name + DWNBTDef.MANUAL_PAGE_KEY + i, player));
				}
				bookPages.appendTag(DWNBT.bookPageFromUnlocalizedLine(name + DWNBTDef.MANUAL_PAGE_KEY + pageCount, "https://www.curseforge.com/minecraft/mc-mods/divineweapon"));
				
				book.setTagInfo("author", new NBTTagString(I18n.format(name + DWNBTDef.MANUAL_AUTHOR)));
				book.setTagInfo("title", new NBTTagString(I18n.format(name + DWNBTDef.MANUAL_TITLE)));
			}
			else
			{
				bookPages.appendTag(DWNBT.bookPageFromUnlocalizedLine("item.shared.missing_manual_content"));
				book.setTagInfo("author", new NBTTagString(I18n.format("item.shared.missing_manual_author")));
				book.setTagInfo("title", new NBTTagString(I18n.format("item.shared.missing_manual_title")));
			}

	        book.setTagInfo("pages", bookPages);
			//DWeapons.LogWarning("[FFFFF: Book NBT]" + book.getTagCompound().toString());
			return book;
  	}
}
