package com.deeplake.dweapon.init;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.Reference;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.entity.Entity;
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

	  @SubscribeEvent
	  @EventHandler
	  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		  //Do something
		  
	      boolean isBookGiven = DWNBTUtil.GetBoolean(event.player, TAG_PLAYER_HAS_BOOK, false);
	      
	      DWeapons.LogWarning(String.format("TAG_PLAYER_HAS_BOOK = %s", isBookGiven));
	      
	      if(!isBookGiven) {
	    	  event.player.addItemStackToInventory(CreateManual());
	    	  DWNBTUtil.SetBoolean(event.player, TAG_PLAYER_HAS_BOOK, true);
	      }
	  }
	      
	  public static ItemStack CreateManual() {
		  DWeapons.LogWarning("Create Manual");
	      
		  
	  		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
	  		
	  		NBTTagList bookPages = new NBTTagList();
	  		bookPages.appendTag(new NBTTagString("This is the starter book."));
	          
	        book.setTagInfo("pages", bookPages);
	  		book.setTagInfo("author", new NBTTagString("Divine Weaponsmith"));
	  		book.setTagInfo("title", new NBTTagString("Your First Divine Weapon"));
  		
  			return book;
  	}
	
	  
	
}
