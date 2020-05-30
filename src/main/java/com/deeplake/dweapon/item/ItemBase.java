package com.deeplake.dweapon.item;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModCreativeTab;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.IHasModel;

import com.deeplake.dweapon.util.NBTStrDef.IDLGeneral;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ItemBase extends Item implements IHasModel {

	boolean shiftToShowDesc = false;
	boolean showGuaSocketDesc = false;

	public ItemBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.DW_MISC);

		ModItems.ITEMS.add(this);
	}

	protected static boolean isShiftPressed()
	{
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {

		IDLGeneral.addInformation(stack,world,tooltip,flag,shiftToShowDesc,isShiftPressed(),showGuaSocketDesc,
				getMainDesc(stack,world,tooltip,flag));
	}

	@SideOnly(Side.CLIENT)
	public String getMainDesc(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		String key = stack.getUnlocalizedName() + ".desc";
		if (I18n.hasKey(key))
		{
			String mainDesc = I18n.format(key);
			return mainDesc;
		}
		return "";
	}


	
	@Override
	public void registerModels() 
	{
		DWeapons.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
}
