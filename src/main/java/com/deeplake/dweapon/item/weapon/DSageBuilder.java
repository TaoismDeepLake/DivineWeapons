package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

//Fu yue is raised from building
public class DSageBuilder extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DSageBuilder(String name, ToolMaterial material) {
		super(name, material);
		
	}

	float base_damage = 4;
	float sky_damage = 7;

	@Override
	public float getAttackDamage()
    {
		//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		if (IsSky(stack)) {
			return sky_damage * ratio;
		}
		else
		{
			return plainAtk(stack) * ratio;
		}
	}

	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float preHP = player.getHealth();
		float damage = getActualDamage(stack, ratio);
		
		boolean success = false;
		if (player instanceof EntityPlayer) {
			success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		}
		else
		{
			success = target.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
		}
		
		stack.damageItem(1, player);
		
		return success;
	}
	
	//---------------------------------------------------------
	//right click
	
	 public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	 public IBlockState getBlockToPlace(ItemStack stack)
	 {
		 if (IsSky(stack))
		 {
			 return Blocks.CONCRETE.getDefaultState();
		 }
		 else if (IsEarth(stack))
		 {
			 return Blocks.BRICK_BLOCK.getDefaultState();
		 }
		 else
		 {
			 return Blocks.DIRT.getDefaultState();
		 }
	 }
	 
	 /**
	     * Called when a Block is right-clicked with this Item
	     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	//BlockPos target = pos.offset(facing);
    	BlockPos target = pos.up();
    	DWeapons.LogWarning(String.format("Target = (%s, %s, %s)", target.getX(), target.getY(), target.getZ()) );
    	
    	IBlockState topBlock = worldIn.getBlockState(target);
    	
    	ItemStack stack = player.getHeldItem(hand);
    	
    	
    	int entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target, target.add(1, 1, 1))).size();
    	boolean noEntities = entities == 0;
    	
		if (
				(topBlock.getBlock() == Blocks.AIR 
				|| (topBlock.getBlock().isReplaceable(worldIn, target)))
			&& noEntities)
		{
			if (!worldIn.isRemote) 
	    	{
	    		worldIn.setBlockState(pos.up(), getBlockToPlace(stack));
	    		
	    		Random rand = new Random();
	    		if (rand.nextInt(GetPearlCount(stack) + 1) == 0)
	    		{	
	    			//stack.setItemDamage(stack.getItemDamage() - 1);
	    			stack.damageItem(1, player);
	    			//stack.attemptDamageItem(amount, rand, damager)
	    		}
	    	}
	    	else
	    	{
//	    		worldIn.playSound(null, player.posX, player.posY, player.posZ, 
//	    				SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.6F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
	    		player.playSound(SoundEvents.BLOCK_GRAVEL_PLACE, 0.6f, 1);
	    	}
			
			return EnumActionResult.SUCCESS;
		}
    	
    	
    	
        return EnumActionResult.PASS;
    }
	
	//--info----------------------------------------------------
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    	if (IsNameHidden(stack)) 
    	{
    		return;
    	}
    	
    	String shared = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SHARED);
		tooltip.add(shared);
    	
    	if (IsSky(stack)) 
    	{
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL);
    		tooltip.add(earthDesc);
    	}
    }
}
