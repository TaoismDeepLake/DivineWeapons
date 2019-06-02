package com.deeplake.dweapon.recipe.special;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NameRevealByBook extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private String bookName = Items.BOOK.getUnlocalizedName();
	
	
	
	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World var2) {
		boolean foundBook = false;
		
		boolean foundSword = false;

		//DWeapons.logger.warn("Entered judging");
		
		
		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					if (foundSword) {
						//DWeapons.logger.warn("Found more than 1 sword");
						return false;//only one sword at a time
					}
					
					//DWeapons.logger.warn("Found sword");
					DWeaponSwordBase sword = (DWeaponSwordBase)stack.getItem();
					foundSword = true;
					
					if (!sword.IsNameHidden(stack))
					{
						//cannot reveal true-name again
						return false;
					}
					
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(bookName))
				{//found a pearl
					if (foundBook) {
						//DWeapons.logger.warn("Found more than 1 blank book");
						return false;//only one book at a time
					}
					
					//DWeapons.logger.warn("Found book");
					foundBook = true;
				}
				else
				{
					//DWeapons.logger.warn("Find useless components:[{}], instanceof", stack.getItem().getUnlocalizedName());
					return false; //Found an upgrader or other.
				}
			}
		}
		

		return foundBook && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		boolean foundSword = false;
		
//		DWeapons.logger.warn("Entered result getting");
		
		ItemStack sword = ItemStack.EMPTY;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
//					DWeapons.logger.warn("Result found sword");
				}
			}
		}

		if(sword.isEmpty()) {
//			DWeapons.logger.warn("No sword");
			return ItemStack.EMPTY;
		}

		ItemStack swordResult = sword.copy();
		//DWeaponSwordBase.SetNameHidden(swordResult, false);
		//	I cant just reveal it here, other wise a player may use one book
		//to peek at all the true names of the weapons they have by not actually crafting it
		
		DWeaponSwordBase.SetManualReady(swordResult, true);
		return swordResult;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
}

