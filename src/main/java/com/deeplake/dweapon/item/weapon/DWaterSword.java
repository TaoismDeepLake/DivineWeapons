package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class DWaterSword extends DWeaponSwordBase {
	// /give @p dweapon:water_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DWaterSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	static final float base_damage = 4;
	public static final float baseDamageAttackMode = 4;//2 hearts
	public static final float pearlDamage = 1;//0.5 hearts
	public static final float earthDamageModifier = 1.5f;
	public static final float skyDamageModifier = 3f;

	static final float pearl_damage_factor = 0.2f;
	static final float t_base = 1.0f;//temperature_base
	static final float t_max = 2.0f;//hotest temperature

	static final float wet_multiplier = 2.0f;//damage rate when wet
	static final float sky_base_damage = 25.0f;//damage rate when wet
	static final int skyBuffTick = 10;

	private static int weatherSummonTick = 100;
	public static final int NORMAL_MODE = 0;
	public static final int CAN_RAIN_MODE = 1;
	public static final int RAINING_MODE = 2;

	int buff_tick_per_pearl = 60;
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		boolean isWaterRelated = repairMaterial.getItem() == Items.WATER_BUCKET;

		return isWaterRelated || super.getIsRepairable(stack, repairMaterial);
	}

	@Override
	public float getAttackDamage()
    {
		//useless
		NBTTagString s = new NBTTagString();
		return base_damage;
		
    }
	
	public float getActualDamage(ItemStack stack, float ratio, boolean isWet)
	{
		float damage = baseDamageAttackMode + pearlDamage * GetPearlCount(stack);
		if (IsSky(stack))
		{
			damage = sky_base_damage;
		} 
		else if (IsEarth(stack))
		{
			damage *= earthDamageModifier;
		}
		if (isWet) {
			damage *= wet_multiplier;
		}
		return damage;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (entityIn instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)(entityIn);
			//gives buff
			if ((IsSky(stack) || IsEarth(stack)) && isSelected)
			{
				playerMP.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, skyBuffTick, 0));
				if (playerMP.isWet()) {
					if (IsSky(stack)) {
						playerMP.addPotionEffect(new PotionEffect(MobEffects.SPEED, skyBuffTick, 2));
					} else if (IsEarth(stack)) {
						playerMP.addPotionEffect(new PotionEffect(MobEffects.SPEED, skyBuffTick, 0));
					}
				}

				//washes away debuff
				Collection<PotionEffect> activePotionEffects = playerMP.getActivePotionEffects();

				for (PotionEffect buff:activePotionEffects) {
					if (buff.getPotion().isBadEffect()){
						playerMP.removeActivePotionEffect(buff.getPotion());
					}
				}
			}

			//put out fire
			if (playerMP.isBurning() && isSelected)
			{
				playerMP.extinguish();
				stack.damageItem(1, playerMP);
			}

			if (CanRainHere(playerMP)) {
				boolean isRainingHere = IsRainingHere(playerMP);
				if (isRainingHere) {
					if (stack.isItemDamaged())
					{//auto fix
						int curDamage = stack.getItemDamage();
						int fixAmount = 1 + playerMP.getRNG().nextInt(1 + GetPearlCount(stack));

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

		}
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float preHP = player.getHealth();
		
		float damage = getActualDamage(stack, ratio, player.isWet());
		
		boolean success = false;
		success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);

		if (success)
		{
			target.extinguish();
			stack.damageItem(1, player);
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
    	return getActualDamage(stack, 1, false);
    }
}
