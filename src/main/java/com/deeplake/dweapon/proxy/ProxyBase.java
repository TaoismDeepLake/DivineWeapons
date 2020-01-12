package com.deeplake.dweapon.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyBase {
	public boolean isServer()
	{
		return true;
	}

	public void registerItemRenderer(Item item, int meta, String id) {
		//Ignored
	}
}
