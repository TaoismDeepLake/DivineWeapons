package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DDeathSword extends DWeaponSwordBase {
	// /give @p dweapon:death_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0, name_hidden:false}
	public DDeathSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	float base_damage = 4;
	float base_killFactor = 1f;
	
	float damage_per_pearl = 1; 
	float killFactor_per_pearl = 1f;
	
	float earth_Chance_bonus = 0.01f;
	
	float sky_suicide_rate = 0.2f;
	
	@Override
	public float getAttackDamage()
    {
		//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack)
	{
		return base_damage + damage_per_pearl * GetPearlCount(stack);
	}
	
	public float getKillRate(ItemStack stack, EntityLivingBase target)
	{
		float targetHealth = Math.max(target.getHealth(), 1f);
		
		if (IsSky(stack))
		{
			return 1;
		}
		else if (IsEarth(stack))
		{
			return earth_Chance_bonus + 1 / targetHealth;
		}
		else
		{
			return  1 / targetHealth;
		}
	}
	
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float damage = getActualDamage(stack);
		
		if (target instanceof EntityLivingBase && ratio > 0.3) {
			if (Math.random() < getKillRate(stack, (EntityLivingBase) target))
			{
				if (IsNameHidden(stack))
				{
					TrueNameReveal(stack, player.getEntityWorld(), player);
				}
				damage = Float.MAX_VALUE;
			}
			
			if (IsSky(stack))
			{
				if (Math.random() < sky_suicide_rate)
				{
					player.setHealth(0);
				}
			}
		}
		
		boolean success = false;
		success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
	
		stack.damageItem(1, player);
		
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
    	
    	if (IsSky(stack)) 
    	{
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY, sky_suicide_rate * 100);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, GetPearlCount(stack), earth_Chance_bonus * 100);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, GetPearlCount(stack));
    		tooltip.add(earthDesc);
    	}
    }
}
