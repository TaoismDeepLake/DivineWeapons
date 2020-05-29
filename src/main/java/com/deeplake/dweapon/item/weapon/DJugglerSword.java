package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.potion.ModPotions;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

//WIP
public class DJugglerSword extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DJugglerSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	float base_damage = 4;
	public static final float baseDamageAttackMode = 1;//2 hearts
	public static final float pearlDamageAttackMode = 1;//0.5 hearts
	public static final float earthDamageModifier = 1.5f;
	public static final float skyDamageModifier = 4f;

	public static final float wetIncreaseDamage = 2f;
	public static final float pushByWaterIncreaseDamage = 2f;

	public static final float fireIncreaseDamage = 5f;
	public static final float airIncreaseDamage = 3f;

	public static final float otherIncreaseDamage = 1f;

	public static final int earthBuffTicks = 100;
	public static final int skyBuffTicks = 1000;

	int buff_tick_per_pearl = 60;
	
	@Override
	public float getAttackDamage()
    {
		//useless
		NBTTagString s = new NBTTagString();
		return base_damage;
		
    }
	
	public float getActualDamage(ItemStack stack, float ratio, EntityPlayer player)
	{
		float damage = baseDamageAttackMode + pearlDamageAttackMode * GetPearlCount(stack);
		if (player != null)
		{
			if (player.isInWater())
			{
				damage += wetIncreaseDamage;
			}
			if (player.isAirBorne)
			{
				damage += airIncreaseDamage;
			}
			if (player.isPushedByWater())
			{
				damage += pushByWaterIncreaseDamage;
			}
			if (player.isBurning())
			{
				damage += fireIncreaseDamage;
			}
			if (player.isGlowing())
			{
				damage += otherIncreaseDamage;
			}
			if (player.isSprinting())
			{
				damage += otherIncreaseDamage;
			}
			//gets power from each debuff
			Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
			for (int i = 0; i < activePotionEffects.size(); i++) {
				PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
				if (buff.getPotion().isBadEffect()){
					damage += otherIncreaseDamage;
				}
				else
				{

				}
			}

			if (player.getActivePotionEffect(MobEffects.ABSORPTION) != null)
			{
				damage -= otherIncreaseDamage * (player.getActivePotionEffect(MobEffects.ABSORPTION).getAmplifier() + 1);
			}
			if (player.isSneaking())
			{
				damage -= otherIncreaseDamage;
			}

			if (player.isInLava())
			{
				damage *= 2f;
			}
			if (player.getActivePotionEffect(MobEffects.WITHER) != null)
			{
				damage *= 1.5f;
			}

			if (IsSky(stack))
			{
				damage *= 2 - (player.getHealth() / player.getMaxHealth());
			}
		}

		if (IsSky(stack))
		{
			damage *= skyDamageModifier;
		} 
		else if (IsEarth(stack))
		{
			damage *= earthDamageModifier;
		}

		damage *= ratio;

		return damage;
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float preHP = player.getHealth();
		
		float damage = getActualDamage(stack, ratio, player);
		
		boolean success = false;
		if (player instanceof EntityPlayer) {
			success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		}
		else
		{
			success = target.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
		}
		
		if (success) {
			stack.damageItem(1, player);
			player.setPrimaryHand(player.getPrimaryHand() == EnumHandSide.LEFT ? EnumHandSide.RIGHT : EnumHandSide.LEFT);
			if (target instanceof EntityLivingBase) {
				EntityLivingBase livingBase = (EntityLivingBase) target;
				if (IsEarth(stack)) {
					livingBase.addPotionEffect(new PotionEffect(ModPotions.DEATH_BOOM, earthBuffTicks, 0));
				}else if (IsSky(stack)) {
					livingBase.addPotionEffect(new PotionEffect(ModPotions.DEATH_BOOM, skyBuffTicks, 0));
				}
			}
		}
		return success;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote)
		{
			EntityPlayer player = (EntityPlayer)entityIn;
			if (IsSky(stack) && player.getHealth() / player.getMaxHealth() < 0.5f)
			{
				if (player.isSprinting())
				{
					player.setFire(20);
					player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20, 2));
				}
				if (player.isInWater())
				{
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 20, 0));
				}

				if (player.isBurning())
				{
					player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20, 0));
				}
			}
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
	
	@SideOnly(Side.CLIENT)
    public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn)
    {
    	return getActualDamage(stack, 1, null);
    }
}
