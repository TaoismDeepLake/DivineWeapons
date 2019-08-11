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

		//DWeapons.logger.warn("Entered judging");
		
		
		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DTrueNameSword ||stack.getItem() instanceof DFutureSword)
				{
					if (foundSword) {
						//DWeapons.logger.warn("Found more than 1 sword");
						return false;//only one sword at a time
					}
					
					//DWeapons.logger.warn("Found sword");
					DWeaponSwordBase sword = (DWeaponSwordBase)stack.getItem();
					foundSword = true;
					
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{//found a pearl
					if (foundSky) {
						//DWeapons.logger.warn("Found more than 1 sky charm");
						return false;//only one sword at a time
					}
					String hater = stack.getDisplayName();
					
					DWeapons.logger.warn("Found paper hating:" + hater);
					foundSky = true;
				}
				else
				{
					//DWeapons.logger.warn("Find useless components:[{}], instanceof", stack.getItem().getUnlocalizedName());
					return false; //Found an upgrader or other.
				}
			}
		}
		

		return foundSky && foundSword;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		boolean foundSword = false;
		
//		DWeapons.logger.warn("Entered result getting");
		
		ItemStack sword = ItemStack.EMPTY;
		String hater = "";
		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof DWeaponSwordBase)
				{
					sword = stack;
//					DWeapons.logger.warn("Result found sword");
				}
				else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
				{//found a pearl

					hater = stack.getDisplayName();
					
					DWeapons.logger.warn("Found paper hating:" + hater);
					
				}
			}
		}

		if(sword.isEmpty()) {
//			DWeapons.logger.warn("No sword");
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

