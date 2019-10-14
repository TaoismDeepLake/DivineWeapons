package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

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
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DPowerTriangle extends DWeaponSwordBase {

	public DPowerTriangle(String name, ToolMaterial material) {
		super(name, material);
		
        
	}

	public static final int changeNeedTick = 40;//2 sec
	public static final int pearlReduceNeedTick = 6;
	
	public static final float baseDamageAttackMode = 5;//2 hearts
	public static final float pearlDamageAttackMode = 1;//0.5 hearts
	
	public static final float baseDamageOtherMode = 1;//0.5 hearts
	
	public static final float earthDamageModifier = 1.5f;
	public static final float skyDamageModifier = 3f;
	
	public static final int ATTACK_MODE = 0;
	public static final int DEFENSE_MODE = 1;
	public static final int SPEED_MODE = 2;
	
	public static final int WEAR_DURATION = 100;
	
	public float getAttackDamage(final ItemStack stack, float ratio)
    {
		if (GetWeaponMode(stack) == ATTACK_MODE)
		{
			float damage = baseDamageAttackMode * ratio + pearlDamageAttackMode * GetPearlCount(stack);
			if (IsSky(stack))
			{
				damage *= skyDamageModifier;
			} 
			else if (IsEarth(stack))
			{
				damage *= earthDamageModifier;
			}
			
			return damage;
		}
		else
		{
			return baseDamageOtherMode;
		}
    }
	
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}
		float damage = getAttackDamage(stack, ratio);
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		if (success)
		{
			stack.damageItem(1, player);
		}
		
		return success;
	}
	
	/**
     * Called when a Block is right-clicked with this Item
     */
//	@Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//    {
//        return EnumActionResult.PASS;
//    }
	
	@Override
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count) {
//		//Particle;
		//DWeapons.LogWarning(String.format("onUsingTick %s",count));

		if (getMaxItemUseDuration(stack) - count >= GetChangeNeedTick(stack))
		{
			for (int i = 0; i < 20; i++)
			{
				CreateParticle(stack, living, -3d);
			}
		}
		else
		{
			//CreateParticle(stack, living, -0.5d);
		}		
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
	
	public float GetChangeNeedTick(ItemStack stack)
	{
		int count = GetPearlCount(stack);
		if (IsSky(stack))
		{
			count = GetPearlMax(stack);
		}
		return changeNeedTick - count * pearlReduceNeedTick;
	}
	
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		//playerIn.setActiveHand(handIn);
        //return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//change mode
		if (!world.isRemote) {

			if (getMaxItemUseDuration(stack) - time >= GetChangeNeedTick(stack))
			{
				//change mode
				int mode = GetWeaponMode(stack);
				if (mode == SPEED_MODE)
				{
					SetWeaponMode(stack, 0);
				}
				else
				{
					SetWeaponMode(stack, mode + 1);
				}
				
				//DWeapons.LogWarning("Weapon mode is:" + GetWeaponMode(stack));
			}
			
		}
		return;
	}
	
	private void AddTickBuff(EntityPlayer entityIn, Potion effect, int level)
	{
		entityIn.addPotionEffect(new PotionEffect(effect, 25, level));
	}
	
	private void WearOut(EntityPlayer entityIn, ItemStack stack)
	{
		Random rand = new Random();
	
		if (!IsSky(stack) && rand.nextInt(WEAR_DURATION) == 0)
		{	
			entityIn.playSound(SoundEvents.BLOCK_GLASS_STEP, 0.6f, 1);
			stack.damageItem(1, entityIn);
		}
		
	}
	
	final int buffRenewPeriod = 20;
	
	@Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		//DWeapons.LogWarning("onUpdate");
		//DWeapons.LogWarning(String.valueOf(worldIn.isRemote));
		if (worldIn.isRemote) {
			return;
		}

		if (entityIn instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)(entityIn); 
			
			if (IsSky(stack) || isSelected)//sky items works even when not selected
			{
				long tick = worldIn.getTotalWorldTime();
				
				int mode = GetWeaponMode(stack);
				if (mode == DEFENSE_MODE)
				{
					if (IsSky(stack))
					{
						//adding a 1 or 2 tick regen won't work, even in sky version.
						//the buff is there but won't regen health.
						//considering adding a buff for visual and manually healing by moding tick count.
						
						//vanilla health need tick count per level - 50, 25, 12, 6, 3, 1
						
						//and health boost added per tick also works strange. It clears out extra health every tick, causing a damage effect.
						//When you renew the health boost buff, it simply resets. Not what you can do this way.
						
						if (tick % buffRenewPeriod == 0)
						{
							AddTickBuff(playerMP, MobEffects.FIRE_RESISTANCE, 0);
							AddTickBuff(playerMP, MobEffects.RESISTANCE, 2);
							//AddTickBuff(playerMP, MobEffects.REGENERATION, 1);
							//AddTickBuff(playerMP, MobEffects.HEALTH_BOOST, 1);
							
							AddTickBuff(playerMP, MobEffects.MINING_FATIGUE, 1);
						}
						
						if (tick % 12 == 0)
						{
							playerMP.heal(1);
						}
						
					}else if (IsEarth(stack))
					{
						if (tick % buffRenewPeriod == 0) {
							//AddTickBuff(playerMP, MobEffects.HEALTH_BOOST, 0);
							AddTickBuff(playerMP, MobEffects.RESISTANCE, 1);
						}
						//AddTickBuff(playerMP, MobEffects.REGENERATION, 0);
						
						if (tick % 25 == 0)
						{
							playerMP.heal(1);
						}
					}else//man
					{
						if (tick % buffRenewPeriod == 0) {
							AddTickBuff(playerMP, MobEffects.RESISTANCE, 0);
						//AddTickBuff(playerMP, MobEffects.REGENERATION, 0);
						
							AddTickBuff(playerMP, MobEffects.SLOWNESS, 0);
						}
						
						if (tick % 50 == 0)
						{
							playerMP.heal(1);
						}
					}
					
					WearOut(playerMP, stack);
				}
				else if (mode == SPEED_MODE)
				{
					if (tick % buffRenewPeriod == 0) {
						if (IsSky(stack))
						{
							AddTickBuff(playerMP, MobEffects.HASTE, 1);
							AddTickBuff(playerMP, MobEffects.JUMP_BOOST, 1);
							AddTickBuff(playerMP, MobEffects.SPEED, 2);
							
							AddTickBuff(playerMP, MobEffects.WEAKNESS, 0);
						}else if (IsEarth(stack))
						{
							AddTickBuff(playerMP, MobEffects.JUMP_BOOST, 0);
							AddTickBuff(playerMP, MobEffects.SPEED, 1);
							
							AddTickBuff(playerMP, MobEffects.WEAKNESS, 0);
						}else//man
						{
							AddTickBuff(playerMP, MobEffects.SPEED, 0);
							
							AddTickBuff(playerMP, MobEffects.WEAKNESS, 0);
						}
					}
					WearOut(playerMP, stack);
				}
				else if (mode == ATTACK_MODE)
				{
					if (tick % buffRenewPeriod == 0) {
						if (IsSky(stack))
						{
							AddTickBuff(playerMP, MobEffects.STRENGTH, 2);
						}else if (IsEarth(stack))
						{
							AddTickBuff(playerMP, MobEffects.STRENGTH, 1);
						}else//man
						{
							//AddTickBuff(playerMP, MobEffects.MINING_FATIGUE, 0);
							
							AddTickBuff(playerMP, MobEffects.STRENGTH, 0);
						}
					}
				}
			}
		}
    }
	
	public String GetModeNBT(int mode)
	{
		if (mode == DEFENSE_MODE)
		{
			return ".defense";
		}
		else if (mode == SPEED_MODE)
		{
			return ".speed";
		}
		else
		{
			return ".attack";
		}
	}
	
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY+modeStr);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH+modeStr);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL+modeStr);
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		return getAttackDamage(stack, 1f);

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
    			strMain = I18n.format(getUnlocalizedName(stack) + DWNBTDef.TOOLTIP_HIDDEN + modeStr);
    		}
    		
    	}
    	else
    	{
    		strMain = I18n.format(getUnlocalizedName(stack) + ".name" + modeStr);
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
