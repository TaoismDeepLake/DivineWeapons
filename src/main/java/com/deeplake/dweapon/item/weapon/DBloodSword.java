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

public class DBloodSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DBloodSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	float base_damage = 4;
	float base_hurt = 1f;
	
	short pearl_count = 0;
	
	float damage_per_pearl = 4; 
	float hurt_per_pearl = 0f;
	
	int buff_tick_per_pearl = 60;
	
	//sky
	float skyDamage = 100;
	float skyHurt = 10;
	
	int skyStrengthLevel = 10;
	int skyRegenLevel = 2;
	int skyBuffTick = 600;//0:30
	
	@Override
	public float getAttackDamage()
    {
		//useless
		NBTTagString s = new NBTTagString();
		return base_damage;
		
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		if (IsSky(stack)) {
			return skyDamage;
		}
		else
		{
			return getBloodBurst(stack) + plainAtk(stack) * ratio;
		}
	}
	
	public float getBloodBurst(ItemStack stack)
	{
		return base_damage + GetPearlCount(stack) * damage_per_pearl;
	}
	
	public float getHurt(ItemStack stack)
	{
		if (IsSky(stack))
		{
			return skyHurt;
		}
		else
		{
			return base_hurt + GetPearlCount(stack) * hurt_per_pearl;
		}
	}
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float preHP = player.getHealth();
		
		player.setHealth(preHP - getHurt(stack));//drain self
		float damage = getActualDamage(stack, ratio);
		
		boolean success = false;
		if (player instanceof EntityPlayer) {
			success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		}
		else
		{
			success = target.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
		}
		
		if (success)
		{
			if (IsSky(stack))
			{
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, skyBuffTick, skyRegenLevel - 1));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, skyBuffTick, skyStrengthLevel - 1));
			
				for (int i = 1; i <= 9; i++)
				{	
					CreateParticle(stack, player, 1);
				}
			}
			else if (IsEarth(stack))
			{
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (pearl_count + 1) * buff_tick_per_pearl, 0));
			}
			
			stack.damageItem(1, player);
			
			if (IsNameHidden(stack) && (player.getHealth() / player.getMaxHealth() <= 0.6f))
			{
				TrueNameReveal(stack, player.getEntityWorld(), player);
			}
			
			CreateParticle(stack, player, 1);
		}
			
		return success;
	}
	
	private void CreateParticle(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 0.5d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = - rand.nextDouble() * vm - 1d;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR,
				x,y,z,vx,vy,vz);
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, getHurt(stack)/2, getActualDamage(stack, 1));
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, getHurt(stack)/2, getActualDamage(stack, 1));
    		tooltip.add(earthDesc);
    	}
    }
}
