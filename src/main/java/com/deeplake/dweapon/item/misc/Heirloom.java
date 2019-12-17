
package com.deeplake.dweapon.item.misc;

import java.util.Random;

import javax.annotation.Nonnull;

import com.deeplake.dweapon.init.ModCreativeTab;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.ItemBase;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundSetupEvent;


public class Heirloom extends SealedWeapon {

	public Heirloom() {
		super();
	}
	
	public Heirloom(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		//setCreativeTab(ModCreativeTab.DW_MAIN);
		
		ModItems.ITEMS.add(this);
		//This item will not be obtainable from the creation inventory.
	}

	protected int[] GetFactorGroup()
	{
		int count = WeaponIndex.LAST.ordinal();
		int[] factor = new int[count];
		factor[WeaponIndex.BLOOD_INDEX.ordinal()] = 10;
		factor[WeaponIndex.DEATH_INDEX.ordinal()] = 10;
		factor[WeaponIndex.SPACE_AF_INDEX.ordinal()] = 10;
		factor[WeaponIndex.BUILDER_INDEX.ordinal()] = 3;
		factor[WeaponIndex.SNOW_INDEX.ordinal()] = 3;
		factor[WeaponIndex.POWER_TRIANGLE_INDEX.ordinal()] = 3;
		factor[WeaponIndex.GOLD_INDEX.ordinal()] = 3;
		factor[WeaponIndex.TRUENAME_INDEX.ordinal()] = 10;
		factor[WeaponIndex.DISARMER_INDEX.ordinal()] = 10;
		factor[WeaponIndex.WATER_INDEX.ordinal()] = 10;
		factor[WeaponIndex.BEADS_INDEX.ordinal()] = 3;
		return  factor;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		if (!world.isRemote) {
			//EntityPlayer player = (EntityPlayer)living;
			ItemStack resultStack = GetRandomWeapon();
			DWeaponSwordBase.SetHeirloom(resultStack, true);

			String ownerName = player.getDisplayNameString();
			String preOwner = DWeaponSwordBase.GetOwner(stack);
			if (preOwner != "")
			{   //  even if you get someone else's heirloom, it's still theirs.
				ownerName = preOwner;
			}

			DWeaponSwordBase.SetOwner(resultStack, ownerName);

			player.addItemStackToInventory(resultStack);
			player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1f, 1f);
			player.addExperience(10);

			stack.shrink(1);
				
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

}
