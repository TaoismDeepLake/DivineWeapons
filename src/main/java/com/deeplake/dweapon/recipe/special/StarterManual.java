package com.deeplake.dweapon.recipe.special;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.init.events.ModStarterEvents;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class StarterManual extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private String bookName = Items.BOOK.getUnlocalizedName();
	private String ingotName = ModItems.DIVINE_INGOT.getUnlocalizedName();
	private String ingotName2 = ModItems.PURE_INGOT.getUnlocalizedName();

	private boolean IsIngot(String str){
		return str.equals(ingotName2) ||  str.equals(ingotName);
	}

	
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
				if(IsIngot(stack.getItem().getUnlocalizedName(stack)))
				{
					if (foundSword) {
						return false;//only one sword at a time
					}
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
		return ModStarterEvents.CreateManual(null);
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

