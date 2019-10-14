package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DGoldSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DGoldSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	public static final float baseDamageAttackMode = 4;//2 hearts
	public static final float pearlDamageAttackMode = 1;//0.5 hearts
	public static final float earthDamageModifier = 1.5f;
	public static final float skyDamageModifier = 4f;
	

	
	@Override
	public float getAttackDamage()
    {
		//useless
		//NBTTagString s = new NBTTagString();
		return baseDamageAttackMode;
		
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		float damage = ratio * baseDamageAttackMode + pearlDamageAttackMode * GetPearlCount(stack);
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
	
	public ItemStack Yield(ItemStack sourceWeapon)
	{
		if (IsSky(sourceWeapon))
		{
			return new ItemStack(Blocks.GOLD_BLOCK);
		}else if (IsEarth(sourceWeapon))
		{
			return new ItemStack(Items.GOLD_INGOT);
		}else
		{
			return new ItemStack(Items.GOLD_NUGGET);
		}
		
		//return ItemStack.EMPTY;
	}

	public static final float pearlRateModifier = 0.10f;//10%
	static final float chanceBase = 0.25f;
	
	public float Chance(ItemStack stack)
	{
		return chanceBase + GetPearlCount(stack) * pearlRateModifier;
	}
	
	public boolean TryChance(ItemStack sourceWeapon)
	{
		float chance = chanceBase + GetPearlCount(sourceWeapon) * pearlRateModifier;
		Random rand = new Random();
		
		if (IsSky(sourceWeapon))
		{
			return true;
		}else if (IsEarth(sourceWeapon))
		{
			return rand.nextFloat() < chance;
		}else
		{
			return rand.nextFloat() < chance;
		}
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}
		
		float damage = getActualDamage(stack, ratio);
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		if (success)
		{
			stack.damageItem(1, player);
			if (TryChance(stack))
			{
				player.addItemStackToInventory(Yield(stack));
			}
		}
			
		return success;
	}
	
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
    	
		
		int percentage = (int)(Chance(stack) * 100);
		
    	if (IsSky(stack)) 
    	{
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, percentage);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, percentage);
    		tooltip.add(earthDesc);
    	}
    	
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	return getActualDamage(stack, 1);
    }
}
