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

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					if (foundSword) {
						return false;//only one sword at a time
					}

					DWeaponSwordBase sword = (DWeaponSwordBase)stack.getItem();
					foundSword = true;
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(bookName))
				{//found a pearl
					if (foundBook) {
						return false;//only one book at a time
					}
					foundBook = true;
				}
				else
				{
					return false; 
				}
			}
		}

		return foundBook && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {

		ItemStack sword = ItemStack.EMPTY;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
				}
			}
		}

		if(sword.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack swordResult = sword.copy();
		
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

