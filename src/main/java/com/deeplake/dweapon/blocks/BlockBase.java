package com.deeplake.dweapon.blocks;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModBlocks;
import com.deeplake.dweapon.init.ModCreativeTab;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel
{
	public BlockBase(String name, Material material)
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.DW_MISC);;
		
		ModBlocks.BLOCKS.add(this);
		//if (ModItems.ITEMS.contains(o))
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		
	}

	
	@Override
	public void registerModels() {
		DWeapons.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
