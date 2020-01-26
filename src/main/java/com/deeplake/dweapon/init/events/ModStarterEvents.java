package com.deeplake.dweapon.init.events;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.misc.Heirloom;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;
import com.deeplake.dweapon.util.DWNBT;
import com.deeplake.dweapon.util.Reference;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.swing.*;

import static com.deeplake.dweapon.util.DWNBT.getTagSafe;
import static com.deeplake.dweapon.util.Reference.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class ModStarterEvents {
	
	public static final String TAG_PLAYER_HAS_BOOK = DWNBTDef.STARTER_BOOK_GIVEN;

	//if the event subscriber is static, so should the events be
	//Thanks Cadiboo for telling me that
	  @SubscribeEvent
	  public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		  NBTTagCompound playerData = event.player.getEntityData();
		  NBTTagCompound data = getTagSafe(playerData, EntityPlayer.PERSISTED_NBT_TAG);

		  if(!data.getBoolean(TAG_PLAYER_HAS_BOOK)) {
			  //ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(TinkerCommons.book));
			  data.setBoolean(TAG_PLAYER_HAS_BOOK, true);
			  playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);

			  EntityPlayer player = event.player;

			  //boolean isBookGiven = DWNBTUtil.GetBoolean(event.player, TAG_PLAYER_HAS_BOOK, false);
			  DWeapons.LogWarning(event.player.getUniqueID().toString());

			  //DWeapons.LogWarning(String.format("TAG_PLAYER_HAS_BOOK = %s", isBookGiven));
			  //if(!isBookGiven) {
				  ItemStack heirloom = new ItemStack(ModItems.HEIRLOOM);
				  DWeaponSwordBase.SetOwner(heirloom, player.getDisplayNameString());

				  //DWeapons.LogWarning("Temp skip give manual, because of a bug");
				  event.player.addItemStackToInventory(heirloom);
				  event.player.addItemStackToInventory(CreateManual(player));
				  //DWNBTUtil.SetBoolean(event.player, TAG_PLAYER_HAS_BOOK, true);
				  DWeapons.LogWarning(String.format("Given starter items to player %s", player.getDisplayNameString()));
			 // }
		  }
	  }

	  public static ItemStack CreateManual(EntityPlayer player) {
		  ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
			
			// https://minecraftjson.com/
			
			// /give @p written_book{pages:["[\"\",{\"text\":\"123 45\"}]","[\"\",{\"text\":\"678 90\"}]"],title:CustomBook,author:Player}
			
			NBTTagList bookPages = new NBTTagList();
			String name = "starter";

			if (DWeapons.proxy.isServer())
			{
				bookPages.appendTag(DWNBT.bookPageFromLineAndUrl(name + "Server manual is temporarily disabled. See info at the official website: ", "https://www.curseforge.com/minecraft/mc-mods/divineweapon"));

				book.setTagInfo("author", new NBTTagString("The Lost Weapon Smith"));
				book.setTagInfo("title", new NBTTagString("The Missing Manual"));

				book.setTagInfo("pages", bookPages);
				//DWeapons.LogWarning("[FFFFF: Book NBT]" + book.getTagCompound().toString());
				return book;
			}


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

	public static void grantAdvancement(EntityPlayerMP playerMP, String advancementResource) {
		Advancement advancement = playerMP.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(MOD_ID, advancementResource));
		if(advancement != null) {
			AdvancementProgress advancementProgress = playerMP.getAdvancements().getProgress(advancement);
			DWeapons.LogWarning( String.format("Achv %s %s", advancementProgress.toString(), advancementProgress.isDone()));
			if(!advancementProgress.isDone()) {
				// we use playerAdvancements.grantCriterion instead of progress.grantCriterion for the visibility stuff and toasts
				advancementProgress.getRemaningCriteria().forEach(criterion -> playerMP.getAdvancements().grantCriterion(advancement, criterion));
			}
		}
		else {
			DWeapons.LogWarning("Cannot find achv:" + advancementResource);
		}
	}
}
