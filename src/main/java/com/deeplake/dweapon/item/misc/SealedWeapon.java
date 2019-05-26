
package com.deeplake.dweapon.item.misc;

import java.util.Random;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.ItemBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

enum WeaponIndex{
	BLOOD_INDEX,
	DEATH_INDEX,
	SPACE_AF_INDEX,
	LAST,
}

public class SealedWeapon extends ItemBase {

	public SealedWeapon(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		//ItemStack item = player.getItemStackFromSlot(slotIn)
		if (!worldIn.isRemote) {
			ItemStack itemstack = player.getHeldItem(hand);
			
			itemstack.shrink(1);
			
			player.addItemStackToInventory(GetRandomWeapon());
		}
		
        return EnumActionResult.SUCCESS;
    }
	
	public ItemStack GetRandomWeapon()
	{
		ItemStack result;
		
		int count = WeaponIndex.LAST.ordinal();
		int[] factor = new int[count];
		
		factor[WeaponIndex.BLOOD_INDEX.ordinal()] = 10;
		factor[WeaponIndex.DEATH_INDEX.ordinal()] = 10;
		factor[WeaponIndex.SPACE_AF_INDEX.ordinal()] = 10;
		
		int factorSum = 0;
		
		for (int i = 0; i < count; i++)
		{
			factorSum += factor[i];
		}
		
		int i = 0;
		Random rand = new Random();
		int random = rand.nextInt(factorSum);
		for (; i < count; i++)
		{
			random -= factor[i];
			if (random <= 0)
			{
				break;
			}
		}
		if (i > count) 
		{
			i = count;
		}
		
		WeaponIndex resultEnum = WeaponIndex.values()[i];
		
		switch(resultEnum)
		{
		case BLOOD_INDEX:
			result = new ItemStack(ModItems.BLOOD_SWORD);
			break;
		case DEATH_INDEX:
			result = new ItemStack(ModItems.Death_SWORD);
			break;
		case SPACE_AF_INDEX:
			result = new ItemStack(ModItems.SPACE_SWORD);
			break;
		default:
			result = new ItemStack(Items.IRON_SWORD);
			break;
		}
		
		return result;
	}

}
