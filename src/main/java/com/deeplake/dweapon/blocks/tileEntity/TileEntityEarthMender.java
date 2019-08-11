package com.deeplake.dweapon.blocks.tileEntity;

import com.deeplake.dweapon.DWeapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class TileEntityEarthMender extends TileEntity implements ITickable {

	public void onLoad()
    {
		DWeapons.LogWarning("onLoad-------------------------");
    }

	@Override
	public void update() {
		if (this.world.getTotalWorldTime() % 20L == 0L)
        {
			DWeapons.LogWarning("Tick-------------------------");
        }
		

	}
	
	static 
	{
		register("earth_mender_basic", TileEntityEarthMender.class);
	}

}
