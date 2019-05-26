package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DSnowSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DSnowSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	static final float base_damage = 5f;
	
	static final float pearl_damage_factor = 0.2f;
	static final float t_base = 1.0f;//temperature_base
	static final float t_max = 2.0f;//hotest temperature
	
	static final float snowing_multiplier = 2.0f;//damage rate when snowing
	
	static final int skyBuffTick = 100;
	
	@Override
	public float getAttackDamage()
    {
		//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio, float t, boolean isSnowing)
	{	
		float result = isSnowing ? base_damage * snowing_multiplier : base_damage;
		
		int pearlCount = GetPearlCount(stack);
		
		if (t > t_base) 
		{
			if (IsEarth(stack) || IsSky(stack))
			{
				result *= 1 + (t - t_base) * ( 1 + pearl_damage_factor * pearlCount);
			}
			else
			{
				result *= 1 - (t - t_base);
			}
		}
		else
		{
			result *= 1 + (t_base - t) * ( 1 + pearl_damage_factor * pearlCount);
		}
			
		return result;

	}
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		BlockPos pos = player.getPosition();
		World world = player.getEntityWorld();
		Biome biome = world.getBiomeForCoordsBody(pos);
		float t = biome.getTemperature(pos);
		
		EntityPlayerMP playerMP = (EntityPlayerMP)(player); 
		
		WorldInfo worldInfo = playerMP.mcServer.worlds[0].getWorldInfo();
		boolean raining = worldInfo.isRaining();
		//biome.getEnableSnow
		boolean isSnowing = (t < 0.15f) && raining;
		
		float damage = getActualDamage(stack, ratio, t, isSnowing);
		
		boolean success = false;
		if (player instanceof EntityPlayer) {
			success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		}
		else
		{
			success = target.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
		}
		
		if (IsSky(stack))
		{
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, skyBuffTick, 0));
		}
		
		stack.damageItem(1, player);
		
		if (IsNameHidden(stack) && (isSnowing))
		{
			SetNameHidden(stack, false);
			player.addExperience(10);
		}
		
		return success;
	}
	
	@Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		//DWeapons.LogWarning("onUpdate");
		//DWeapons.LogWarning(String.valueOf(worldIn.isRemote));
		
		if (entityIn instanceof EntityPlayerMP)
		{
			
			EntityPlayerMP playerMP = (EntityPlayerMP)(entityIn); 
			
			//DWeapons.LogWarning(String.valueOf(worldIn.isRemote));
			//DWeapons.LogWarning(String.valueOf(playerMP.world.isRemote));
			
			BlockPos pos = playerMP.getPosition();
			World world = playerMP.getEntityWorld();
			Biome biome = world.getBiomeForCoordsBody(pos);
			float t = biome.getTemperature(pos);
			
			WorldInfo worldInfo = playerMP.mcServer.worlds[0].getWorldInfo();
			boolean raining = worldInfo.isRaining();
			//biome.getEnableSnow
			boolean isSnowing = (t < 0.15f) && raining;
			
			//auto fix
			if (stack.isItemDamaged())
			{
				int curDamage = stack.getItemDamage();
				int fixAmount = 1 + playerMP.getRNG().nextInt(1 + GetPearlCount(stack));
				
				stack.setItemDamage(Math.max(curDamage - fixAmount, 0));
			}
		
			//TODO: set block to snow if possible
			
			//TODO: create particles
			
		}
		
		if (worldIn.isRemote && isSelected)
		{
			//DWeapons.LogWarning("create particle");
			//wont work in main hand!
			
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
	
		//EnumParticleTypes.DAMAGE_INDICATOR;
		living.world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL,
				x,y,z,vx,vy,vz);
//		living.world.spawnParticle(EnumParticleTypes.PORTAL,
//				x,y,z,vx,vy,vz);
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
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL);
    		tooltip.add(earthDesc);
    	}
    }
}
