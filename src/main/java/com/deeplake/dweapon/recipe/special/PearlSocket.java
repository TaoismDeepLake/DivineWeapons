package com.deeplake.dweapon.recipe.special;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

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

//		DWeapons.logger.warn("Entered judging");
		
		
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
					maxPearlAccepted = sword.GetPearlEmptySpace(stack);
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{//found a pearl
					foundPearl++;
					//DWeapons.logger.warn("found a pearl, foundPearl:[{}]", foundPearl);
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
			//DWeapons.logger.warn("Too many pearls II");
			return false;//such sword cannot have so many pearls, or no sword found at all
		}
		
		//DWeapons.logger.warn("pearl:[{}]", pearlName);
		//DWeapons.logger.warn("Found pearl:[{}], foundSword:[{}]", foundPearl, foundSword);
		return foundPearl > 0 && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		int foundPearl = 0;
		int curPearl = 0;
		boolean foundSword = false;
		
		//DWeapons.logger.warn("Entered result getting");
		
		ItemStack sword = ItemStack.EMPTY;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
					curPearl = DWeaponSwordBase.GetPearlCount(stack);
					//DWeapons.logger.warn("Result found sword");
				}
				else if(stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{
					foundPearl++;
					//DWeapons.logger.warn("found a pearl, foundPearl:[{}]", foundPearl);
				}
			}
		}

		if(sword.isEmpty() || foundPearl == 0) {
			//DWeapons.logger.warn("No sword or pearl");
			return ItemStack.EMPTY;
		}

		ItemStack swordResult = sword.copy();
		DWeaponSwordBase.SetPearlCount(swordResult, foundPearl + curPearl);

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

