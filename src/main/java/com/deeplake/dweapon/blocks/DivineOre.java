package com.deeplake.dweapon.blocks;

import java.util.Random;

import com.deeplake.dweapon.init.ModItems;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class DivineOre extends BlockBase {

	public DivineOre(String name, Material material) {
		super(name, material);
		// TODO Auto-generated constructor stub
		
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setResistance(15.0F);
		setHarvestLevel("pickaxe", 3);
		setLightLevel(1f);
		setLightOpacity(1);
		
	}

	//optional
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return super.getItemDropped(state, rand, fortune);
		//return Items.ACACIA_BOAT
		//return ModItems.ANGEL_STATUE;
	}
	
	@Override
	public int quantityDropped(Random rand) {
//		int max = 4;
//		int min = 1;
//		return rand.nextInt(max) + min;
		
		return super.quantityDropped(rand);
	}
}
