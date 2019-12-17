package com.deeplake.dweapon.recipe.special;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import com.deeplake.dweapon.item.weapon.IDWeaponEnhanceable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PearlSocket extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	private String pearlName = ModItems.WEAPON_PEARL.getUnlocalizedName();

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World var2) {
		int foundPearl = 0;
		int maxPearlAccepted = 999;//before finding the weapon, we dont know whats the max pearl accepted
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
					maxPearlAccepted = sword.GetPearlEmptySpace(stack);
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{//found a pearl
					foundPearl++;
					if (foundPearl > maxPearlAccepted && foundSword) {
						//DWeapons.logger.warn("Too many pearls");
						return false;//such sword cannot have so many pearls.
						//this cutting is for optimizaton, not a must
					}
				}
				else
				{
					//DWeapons.logger.warn("Find useless components:[{}], instanceof", stack.getItem().getUnlocalizedName());
					return false; //Found an upgrader or other.
				}
			}
		}
		
		if (foundPearl > maxPearlAccepted) {
			return false;//such sword cannot have so many pearls, or no sword found at all
		}

		return foundPearl > 0 && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		int foundPearl = 0;
		int curPearl = 0;
		
		ItemStack sword = ItemStack.EMPTY;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
					curPearl = ((DWeaponSwordBase)(stack.getItem())).GetPearlCount(stack);
				}
				else if(stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{
					foundPearl++;
				}
			}
		}

		if(sword.isEmpty() || foundPearl == 0) {
			return ItemStack.EMPTY;
		}

		ItemStack swordResult = sword.copy();
		((DWeaponSwordBase)(swordResult.getItem())).SetPearlCount(swordResult, foundPearl + curPearl);

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

