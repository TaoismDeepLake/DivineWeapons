package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

//Test Weapon.
public class DFutureSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DFutureSword(String name, ToolMaterial material) {
		super(name, material);
		
	}
	
	public static final int NORMAL_MODE = 0;
	public static final int CAN_RAIN_MODE = 1;
	public static final int RAINING_MODE = 2;
	static final int skyBuffTick = 100;
	private static int weatherSummonTick = 100;
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		boolean isWaterRelated = repairMaterial.getItem() == Items.WATER_BUCKET;
		
		return isWaterRelated || super.getIsRepairable(stack, repairMaterial);
	}
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		float damage = 5 * ratio + GetPearlCount(stack);
		if (IsSky(stack))
		{
			damage *= 5;
		} 
		else if (IsEarth(stack))
		{
			damage *= 2;
		}
		
		return damage;
	}
	
	public float getDamageMultiplier(ItemStack stack)
	{
		float damage = 2;
//		if (IsSky(stack))
//		{
//			damage = 10;
//		} 
//		else if (IsEarth(stack))
//		{
//			damage = 5;
//		}
		
		return damage;
	}

	@Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (entityIn instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)(entityIn);
			
			//creating snow
			
			if (IsSky(stack) && isSelected)
			{
				//player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, skyBuffTick, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, skyBuffTick, 0));
			}
			
			if (isSelected && player.isBurning())
			{
				stack.damageItem(1, (EntityLivingBase) entityIn);
				player.extinguish();
			}
			
			
			if (CanRainHere(player)) {
				boolean isRaining = IsRainingHere(player);
				if (isRaining) {
					if (stack.isItemDamaged())
					{//auto fix
						int curDamage = stack.getItemDamage();
						int fixAmount = 1 + player.getRNG().nextInt(1 + GetPearlCount(stack));
						
						stack.setItemDamage(Math.max(curDamage - fixAmount, 0));
					}
					SetWeaponMode(stack, RAINING_MODE);
				}
				else
				{
					SetWeaponMode(stack, CAN_RAIN_MODE);
				}
			}
			else
			{
				SetWeaponMode(stack, NORMAL_MODE);
			}
			
			
		}
		
		if (worldIn.isRemote && isSelected)
		{
			//DWeapons.LogWarning("create particle");
			//wont work in second hand!
			
			CreateParticle(stack,(EntityLivingBase) entityIn, 0.1f);
//			CreateParticle(stack,(EntityLivingBase) entityIn, -2);
//			CreateParticle(stack,(EntityLivingBase) entityIn, -3);
//			CreateParticle(stack,(EntityLivingBase) entityIn, -4);
//			CreateParticle(stack,(EntityLivingBase) entityIn, -5);
//			CreateParticle(stack,(EntityLivingBase) entityIn, 0);
		}
    }
	
	private void CreateParticle(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = -rand.nextDouble() * vm;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.PORTAL,
				x,y,z,vx,vy,vz);
	}
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}

		float damage = getActualDamage(stack, ratio);
		
		if (target.isBurning())
		{
			target.extinguish();
		}
		
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
		if (success)
		{
			stack.damageItem(1, player);
		}
			
		return success;
	}
	
	//----------
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
		int mode = GetWeaponMode(stack);
		if (IsSky(stack) && mode != NORMAL_MODE) 
		{
			return EnumAction.BOW;
		}
		else
		{
			return EnumAction.NONE;
		}
		
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	public static final int rainTime = 1200;
	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//DWeapons.LogWarning("onPlayerStoppedUsing");
		int mode = GetWeaponMode(stack);
		
		if (IsSky(stack) && 
				(getMaxItemUseDuration(stack) - time >= weatherSummonTick)) {	
			
			if (!world.isRemote) {
				if (mode != NORMAL_MODE)
				{
					EntityPlayerMP playerMP = (EntityPlayerMP)(living); 
					WorldInfo worldInfo = playerMP.mcServer.worlds[0].getWorldInfo();
					
					worldInfo.setRaining(true);
					worldInfo.setThundering(true);
					worldInfo.setThunderTime(0);
					
					worldInfo.setRainTime(rainTime);
				}
			}
			else
			{
				if (mode == NORMAL_MODE) {//cannot snow here
					living.playSound(SoundEvents.ENTITY_BLAZE_BURN, 0.6f, 1);
				} else
				{
					living.playSound(SoundEvents.ENTITY_BOAT_PADDLE_WATER, 1.5f, 1);
				}
			}
		}
		
		return;
	}
	
	
	//----------
	
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
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		return getActualDamage(stack, 1);

    }
}
