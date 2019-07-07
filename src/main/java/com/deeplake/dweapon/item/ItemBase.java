package com.deeplake.dweapon.item;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModCreativeTab;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {
	public ItemBase() {
		
	}
	
	public ItemBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.DW_MISC);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels() 
	{
		DWeapons.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
}
