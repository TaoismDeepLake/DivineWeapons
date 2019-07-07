package com.deeplake.dweapon.init;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab {
	public static final CreativeTabs DW_MISC = new CreativeTabs(CreativeTabs.getNextID(), "DWeaponMiscTab")
	{
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.WEAPON_PEARL);
        }
    };
    
    public static final CreativeTabs DW_WEAPON = new CreativeTabs(CreativeTabs.getNextID(), "DWeaponWeaponTab")
	{
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.BLOOD_SWORD);
        }
    };
	
}
