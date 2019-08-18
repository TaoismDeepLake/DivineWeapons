package com.deeplake.dweapon.init;

import java.util.ArrayList;
import java.util.List;

import com.deeplake.dweapon.blocks.blockBasic.BlockEarthMender;
import com.deeplake.dweapon.blocks.blockBasic.DivineOre;

import com.deeplake.dweapon.blocks.blockBasic.PureOre;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.ResourceLocation;

public class ModBlocks {
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	/*
	 * To add a block, put a line here,
	 * -Create a json at assets.eo.blockstates
	 * -Create a json at assets.eo.models.block
	 * -Create a json at assets.eo.models.item
	 * -Add correspoding texture png
	 */
	
	//public static final Block BUFF_BLOCK_1 = new BlockBase("buff_block_1", Material.CLAY);
	//public static final Block BUFF_BLOCK_1 = new BuffBlock("buff_block_1", Material.IRON);
	
	public static final DivineOre DIVINE_ORE = new DivineOre("divine_ore", Material.ROCK);
	public static final PureOre PURE_ORE = new PureOre("pure_ore", Material.ROCK);
	//public static final BlockEarthMender EARTH_MENDER = new BlockEarthMender("earth_mender_basic", Material.ROCK);
	

}
