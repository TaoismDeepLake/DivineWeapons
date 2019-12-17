
package com.deeplake.dweapon.item.misc;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.ItemBase;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.FMLCommonHandler;
import com.deeplake.dweapon.DWeapons;;

enum WeaponIndex{
	BLOOD_INDEX,
	DEATH_INDEX,
	SPACE_AF_INDEX,
	BUILDER_INDEX,
	SNOW_INDEX,
	POWER_TRIANGLE_INDEX,
	GOLD_INDEX,
	TRUENAME_INDEX,
	DISARMER_INDEX,
	WATER_INDEX,
	BEADS_INDEX,
	LAST,
}

public class SealedWeapon extends ItemBase {

	public SealedWeapon() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SealedWeapon(String name) {
		super(name);
		
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		//playerIn.setActiveHand(handIn);
        //return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		if (!world.isRemote) {
			//EntityPlayer player = (EntityPlayer)living;
			
				//TODO : Add a message.
				//String msg = "";
			
				//FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString(msg));
				ItemStack resultStack = GetRandomWeapon();
				DWeaponSwordBase.SetHeirloom(resultStack, false);
				DWeaponSwordBase.SetOwner(resultStack, player.getDisplayNameString());
				
				player.addItemStackToInventory(resultStack);
				player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1f, 1f);
				player.addExperience(10);
				
				//	Must do shrink AFTER addItemStackToInventory,
				//or it would make the addItemStackToInventory fail if the new thing were to be in the new place.
				//	Try do this when helding one sealed weapon in slot 1, and something else in slot 2.  
				stack.shrink(1);
			
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

	
//	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//    {
////		//ItemStack item = player.getItemStackFromSlot(slotIn)
////		if (!worldIn.isRemote) {
////			ItemStack itemstack = player.getHeldItem(hand);
////			
////			itemstack.shrink(1);
////			
////			player.addItemStackToInventory(GetRandomWeapon());
////			player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1f, 1f);
////			player.addExperience(10);
////		}
//		
//        return EnumActionResult.SUCCESS;
//    }
	
	/**
     * How long it takes to use or consume an item
     */
	@Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	//Animation
		@Nonnull
		@Override
		public EnumAction getItemUseAction(ItemStack stack) {

			return EnumAction.BOW;
			
		}
	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//change mode
//		if (!world.isRemote) {
//			EntityPlayer player = (EntityPlayer)living;
//
//				stack.shrink(1);
//				
//				player.addItemStackToInventory(GetRandomWeapon());
//				player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1f, 1f);
//				player.addExperience(10);
//
//			
//	        return ;
//			
//		}
//		return;
	}

	protected int[] GetFactorGroup()
	{
		int count = WeaponIndex.LAST.ordinal();
		int[] factor = new int[count];
		factor[WeaponIndex.BLOOD_INDEX.ordinal()] = 10;
		factor[WeaponIndex.DEATH_INDEX.ordinal()] = 10;
		factor[WeaponIndex.SPACE_AF_INDEX.ordinal()] = 10;
		factor[WeaponIndex.BUILDER_INDEX.ordinal()] = 6;
		factor[WeaponIndex.SNOW_INDEX.ordinal()] = 6;
		factor[WeaponIndex.POWER_TRIANGLE_INDEX.ordinal()] = 6;
		factor[WeaponIndex.GOLD_INDEX.ordinal()] = 6;
		factor[WeaponIndex.TRUENAME_INDEX.ordinal()] = 10;
		factor[WeaponIndex.DISARMER_INDEX.ordinal()] = 10;
		factor[WeaponIndex.WATER_INDEX.ordinal()] = 10;
		factor[WeaponIndex.BEADS_INDEX.ordinal()] = 6;
		return  factor;
	}

	public ItemStack GetRandomWeapon()
	{
		ItemStack result;
		
		int count = WeaponIndex.LAST.ordinal();
		int[] factor = GetFactorGroup();
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
			result = new ItemStack(ModItems.DEATH_SWORD);
			break;
		case SPACE_AF_INDEX:
			result = new ItemStack(ModItems.SPACE_SWORD);
			break;
		case BUILDER_INDEX:
			result = new ItemStack(ModItems.SAGE_BUILDER);
			break;
		case SNOW_INDEX:
			result = new ItemStack(ModItems.SNOW_SWORD);
			break;
		case POWER_TRIANGLE_INDEX:
			result = new ItemStack(ModItems.POWER_TRIANGLE);
			break;
		case GOLD_INDEX:
			result = new ItemStack(ModItems.GOLD_SWORD);
			break;
		case TRUENAME_INDEX:
			result = new ItemStack(ModItems.TRUE_NAME_SWORD);
			break;
		case DISARMER_INDEX:
			result = new ItemStack(ModItems.DISARM_RING);
			break;
		case WATER_INDEX:
			result = new ItemStack(ModItems.WATER_SWORD);
			break;
		case BEADS_INDEX:
			result = new ItemStack(ModItems.MONK_BEADS);
			break;
		default:
			result = new ItemStack(Items.IRON_SWORD);
			break;
		}
		
		DWeapons.LogWarning(result.getDisplayName());
		//DWeapons¡£LogWarning(result.getDisplayName());
		if (result.isEmpty()){
			DWeapons.LogWarning("EMPTY!!!");
		}
		
		 random = rand.nextInt(10);
		 if (random == 0)
		 {
			 ((DWeaponSwordBase)(result.getItem())).SetPearlCount(result, 1);
		 }
		
		 DWeapons.LogWarning("Given weapon:" + result.getDisplayName());
		 
		return result;
	}

	 @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		 String pearlDesc = I18n.format("item.shared.right_click_open");
		 tooltip.add(pearlDesc);
    
    }
}
