package com.deeplake.dweapon.recipe.special;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.weapon.DFutureSword;
import com.deeplake.dweapon.item.weapon.DTrueNameSword;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MarkHate extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	private String pearlName = Items.PAPER.getUnlocalizedName();//--ModItems.SKY_CHARM.getUnlocalizedName();

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World var2) {
		boolean foundSky = false;
		boolean foundSword = false;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DTrueNameSword)
				{
					if (foundSword) {
						return false;//only one sword at a time
					}
					foundSword = true;
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{//found a pearl
					if (foundSky) {
						return false;//only one sword at a time
					}
					//String hater = stack.getDisplayName();
					//DWeapons.logger.warn("Found paper hating:" + hater);
					foundSky = true;
				}
				else
				{
					return false; //Found an upgrader or other.
				}
			}
		}
		

		return foundSky && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		ItemStack sword = ItemStack.EMPTY;
		String hater = "";
		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{
					hater = stack.getDisplayName();
					DWeapons.logger.warn("Found paper hating:" + hater);
				}
			}
		}

		if(sword.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack swordResult = sword.copy();
		DTrueNameSword.SetHate(swordResult, hater);

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

