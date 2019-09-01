package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

//Test Weapon.
public class DTrueNameSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DTrueNameSword(String name, ToolMaterial material) {
		super(name, material);
		
	}
	
	public static void SetHate(ItemStack stack, String name) {
		DWNBTUtil.SetString(stack, DWNBTDef.HATE, name);
	}
	
	public static String GetHate(ItemStack stack)
	{
		String hater = DWNBTUtil.GetString(stack, DWNBTDef.HATE, "-");
		
		return hater;
	}
	
	public static boolean IsHate(ItemStack stack, String name) {
		String hater = GetHate(stack);
		
		return hater.equalsIgnoreCase(name);
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
		if (IsSky(stack))
		{
			damage = 10;
		} 
		else if (IsEarth(stack))
		{
			damage = 5;
		}
		
		return damage;
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		float damage = getActualDamage(stack, ratio);
		
		String targetName = target.getName();
		if (player.world.isRemote && IsHate(stack, targetName)) {
			for (int i = 1; i <= 10; i++) {
				CreateParticle(stack, target, 1f);
			}
			return false;
		}

		if (IsHate(stack, targetName)) {
			damage *= getDamageMultiplier(stack);
		}
		
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		if (success)
		{
			stack.damageItem(1, player);
		}
			
		return success;
	}

	private void CreateParticle(ItemStack stack, Entity living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;

		double vx = 0d;
		double vy = rand.nextDouble() * vm + 1d;
		double vz = 0d;

		living.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY,GetHate(stack));
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH,GetHate(stack));
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL,GetHate(stack));
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		return getActualDamage(stack, 1);

    }
}
