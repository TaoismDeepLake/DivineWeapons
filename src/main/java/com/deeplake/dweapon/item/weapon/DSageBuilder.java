package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
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
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
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
	float sky_damage = 10;
	
	float sky_damage_sword = 40;
	float earth_damage_sword = 25;
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		int mode = GetWeaponMode(stack);
		if (mode == SWORD_MODE)
		{
			if (IsSky(stack)) {
				return sky_damage_sword * ratio;
			}else {
				return earth_damage_sword * ratio;
			}
		}
			
		if (IsSky(stack)) {
			return sky_damage * ratio;
		}
		else
		{
			return (base_damage * ratio + GetPearlCount(stack));
		}
	}

	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}
		float damage = getActualDamage(stack, ratio);
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);

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
			 //this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE)
			 return Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.values()[GetWeaponMode(stack)]);
			 //return Blocks.CONCRETE.getDefaultState();
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
	 
	 
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	private void CreateParticle(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = rand.nextDouble() * vm + 1d;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.PORTAL,
				x,y,z,vx,vy,vz);
	}
	
	private void CreateParticle2(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = rand.nextDouble() * vm + 1d;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
				x,y,z,vx,vy,vz);
	}
	
	
	
	@Override
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count) {
//		//Particle;
		//DWeapons.LogWarning(String.format("onUsingTick %s",count));

		if (getMaxItemUseDuration(stack) - count >= changeNeedTick)
		{
			if (IsEarth(stack) || IsSky(stack)) {
				for (int i = 0; i < 20; i++) {
					CreateParticle(stack, living, -3d);
				}
			}
		}
		else if (getMaxItemUseDuration(stack) - count >= changeColorNeedTick)
		{
			if (IsSky(stack)){
				int mode = GetWeaponMode(stack);
				if (mode != SWORD_MODE) {
					CreateParticle2(stack, living, -0.5d);
				}
			}
		}		
	}
	 
	 /**
	     * Called when a Block is right-clicked with this Item
	     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = player.getHeldItem(hand);
    	
    	if (GetWeaponMode(stack) == SWORD_MODE)
    	{
    		return EnumActionResult.PASS;
    	}
    	
    	BlockPos target = pos.offset(facing);
    	//BlockPos target = pos.up();
    	//DWeapons.LogWarning(String.format("Target = (%s, %s, %s)", target.getX(), target.getY(), target.getZ()) );
    	
    	IBlockState targetBlock = worldIn.getBlockState(target);
    	
    	int entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target, target.add(1, 1, 1))).size();
    	boolean noEntities = entities == 0;
    	
		if ((targetBlock.getBlock() == Blocks.AIR 	|| (targetBlock.getBlock().isReplaceable(worldIn, target)))
			&& noEntities)
		{
			if (!worldIn.isRemote) 
	    	{
	    		worldIn.setBlockState(target, getBlockToPlace(stack));
	    		
	    		Random rand = new Random();
	    		if (!IsSky(stack) || rand.nextInt(GetPearlCount(stack) + 1) == 0)
	    		{	
	    			//stack.setItemDamage(stack.getItemDamage() - 1);
	    			stack.damageItem(1, player);
	    			//stack.attemptDamageItem(amount, rand, damager)
	    		}
	    		
	    		TrueNameReveal(stack, worldIn, player);
	    	}
	    	else
	    	{
//	    		worldIn.playSound(null, player.posX, player.posY, player.posZ, 
//	    				SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.6F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
	    		player.playSound(SoundEvents.BLOCK_GRAVEL_PLACE, 0.6f, 1);
	    	}
			
			return EnumActionResult.SUCCESS;
		}
    	
    	if (IsEarth(stack) ||IsSky(stack)) {
    		return EnumActionResult.PASS;
    	}
    	
    	return EnumActionResult.SUCCESS;
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
    	
    	int mode = GetWeaponMode(stack);
    	String modeStr = GetModeNBT(mode);
    	
    	String shared = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SHARED);
		tooltip.add(shared);
    	
    	if (IsSky(stack)) 
    	{
    		String key = getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY;
			if (mode == SWORD_MODE)
			{
				key = key + "." + SWORD_STR;
			}
    		String skyDesc = I18n.format(key);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
			String key = getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH;
			if (mode == SWORD_MODE)
			{
				key = key + "." + SWORD_STR;
			}
			String skyDesc = I18n.format(key);
    		tooltip.add(skyDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL);
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		return getActualDamage(stack, 1);
    }
	
	public static final int changeNeedTick = 60;//3 sec
	public static final int changeColorNeedTick = 20;//1 sec
	
	public static final int SWORD_MODE = -1;
	public static final int NORM_MODE = 0;
	
	public static final int MAX_COLOR_MODE = 15;
	
	public static final String SWORD_STR = "sword";
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	//@SuppressWarnings("unused")
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//change mode
		if (!world.isRemote) {
			int mode = GetWeaponMode(stack);
			if (getMaxItemUseDuration(stack) - time >= changeNeedTick &&
					(IsEarth(stack)||IsSky(stack)))
			{
				//switch between sword and builder
				
				if (mode == SWORD_MODE)
				{
					SetWeaponMode(stack, NORM_MODE);
				}
				else
				{
					SetWeaponMode(stack, SWORD_MODE);
				}
				
				//DWeapons.LogWarning("Weapon mode is:" + GetWeaponMode(stack));
			}else if (getMaxItemUseDuration(stack) - time >= changeColorNeedTick && 
					IsSky(stack))
			{
				//change color
				if (mode != SWORD_MODE) {
					if (mode == MAX_COLOR_MODE){
						SetWeaponMode(stack, NORM_MODE);
					} else {
						SetWeaponMode(stack, mode + 1);
					}
				}
				
				//DWeapons.LogWarning("Weapon mode is:" + GetWeaponMode(stack));
			}
			
		}
		return;
	}
	
	public String GetModeNBT(int mode)
	{
		if (mode == SWORD_MODE)
		{
			return ".sword";
		}
		else
		{
			//bed:
			//super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName();
			
			return "." + EnumDyeColor.byMetadata(mode).getUnlocalizedName();
		}
	}
	
	//Animation
	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		int mode = GetWeaponMode(stack);
		if (mode == SWORD_MODE || IsSky(stack) || IsEarth(stack))
		{
			return EnumAction.BOW;
		}
		return EnumAction.NONE;
		
	}
	
	public String getItemStackDisplayName(ItemStack stack)
    {
		int mode = GetWeaponMode(stack);
    	String modeStr = GetModeNBT(mode);
		
    	String strMain ="";
    	if (IsNameHidden(stack))
    	{
//    		if (IsManualReady(stack))
//    		{
//    			
//    		}
//    		else
    		{
    			strMain = I18n.format(getUnlocalizedName(stack) + DWNBTDef.TOOLTIP_HIDDEN);
    		}
    		
    	}
    	else
    	{
    		strMain = I18n.format(getUnlocalizedName(stack) + ".name");
    		if (mode == SWORD_MODE)
			{
    			strMain = I18n.format(getUnlocalizedName(stack)  + ".name" + modeStr);

			}else if (IsSky(stack))
			{
				strMain = I18n.format(getUnlocalizedName(stack) + ".name." + EnumDyeColor.byMetadata(mode).getUnlocalizedName());
			}
    		
    	}
    		
    	String strLevel = "";
    	if (IsSky(stack))
    	{
    		strLevel = I18n.format("postfix.is_sky.name");
    	}else if (IsEarth(stack))
    	{
    		strLevel = I18n.format("postfix.is_earth.name");
    	}
    	String strPearl = "";
    	if (GetPearlCount(stack) > 0) {
    		strPearl = I18n.format("postfix.pearl_count.name", GetPearlCount(stack));
    	}
        return I18n.format("item.dweapon_name_format",strMain, strLevel, strPearl);
    }
}
