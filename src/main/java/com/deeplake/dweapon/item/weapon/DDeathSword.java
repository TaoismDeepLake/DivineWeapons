package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModPotions;
import com.deeplake.dweapon.util.Reference;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

import static com.deeplake.dweapon.init.ModPotions.DEADLY;
import static com.deeplake.dweapon.init.ModPotions.ZEN_HEART;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class DDeathSword extends DWeaponSwordBase {
	// /give @p dweapon:death_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0, name_hidden:false}
	public DDeathSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	private float base_damage = 4;
	private float base_damage_sky = 15;
	float base_killFactor = 1f;
	
	private float damage_per_pearl = 1;
	float killFactor_per_pearl = 1f;
	
	private float earth_Chance_bonus = 0.01f;
	
	private float sky_suicide_rate = 0.2f;

	private static int deadly_buff_ticks_per_pearl = 20;
	public static int getDeadlyBuffTicks(ItemStack stack) {
		if (((DWeaponSwordBase)stack.getItem()).IsSky(stack)) {
			return 200;//10sec
		}else if (((DWeaponSwordBase)stack.getItem()).IsEarth(stack)){
			return 100 + ((DWeaponSwordBase)stack.getItem()).GetPearlCount(stack) * deadly_buff_ticks_per_pearl;
		}else {
			return 60 + ((DWeaponSwordBase)stack.getItem()).GetPearlCount(stack) * deadly_buff_ticks_per_pearl;
		}
	}

	public static int getDeadlyBuffMaxLevel(ItemStack stack) {
		if (((DWeaponSwordBase)stack.getItem()).IsSky(stack)) {
			return 10;
		}else if (((DWeaponSwordBase)stack.getItem()).IsEarth(stack)){
			return 3;
		}else {
			return 1;
		}
	}

	public static float getDeathHealAmount(ItemStack stack) {
		if (((DWeaponSwordBase)stack.getItem()).IsSky(stack)) {
			return 4f;
		}else if (((DWeaponSwordBase)stack.getItem()).IsEarth(stack)){
			return 2f;
		}else {
			return 1f;
		}
	}

	//0 is no buff
	private static int GetDeadlyBuffLevel(EntityLivingBase living){
		PotionEffect curBuff = living.getActivePotionEffect(DEADLY);
		if (curBuff == null) {
			return 0;
		} else {
			int buffLevel = curBuff.getAmplifier();
			return buffLevel + 1;
		}
	}

	private static int deadly_buff_full_divider = 10;
	private static float range = 5f;
	//LivingDeathEvent
	@SubscribeEvent
	public static void onCreatureDie(LivingDeathEvent evt) {
		World world = evt.getEntity().getEntityWorld();
		EntityLivingBase dieOne = evt.getEntityLiving();
		Vec3d pos = evt.getEntity().getPositionEyes(0);
		if (!world.isRemote) {
			//wielder resist death
			ItemStack stackDie = dieOne.getHeldItemMainhand();
			if (stackDie.getItem() instanceof DDeathSword && (((DWeaponSwordBase)stackDie.getItem()).IsEarth(stackDie) || ((DWeaponSwordBase)stackDie.getItem()).IsSky(stackDie))) {
				stackDie.damageItem(256, dieOne);
				dieOne.setHealth(dieOne.getMaxHealth() / 4);
				dieOne.clearActivePotions();
				world.playSound(null, dieOne.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.PLAYERS, 1f, 2f);
				evt.setCanceled(true);
				return;
			}

			//wielder draws power from nearby deaths
			List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.addVector(-range, -range, -range), pos.addVector(range, range, range)));
			for (EntityLivingBase living : list) {
				ItemStack stack = living.getHeldItemMainhand();
				if (stack.getItem() instanceof DDeathSword) {
					int buffLevel = GetDeadlyBuffLevel(living);
					//DWeapons.Log(String.format("deadly level = %s", buffLevel));
					//gives deadly buff
					if (buffLevel >= getDeadlyBuffMaxLevel(stack)) {
						buffLevel = getDeadlyBuffMaxLevel(stack) - 1;
					}

					living.addPotionEffect(new PotionEffect(DEADLY, getDeadlyBuffTicks(stack), buffLevel));
					//play sound
					world.playSound(null, living.getPosition(), SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.PLAYERS, 1f, buffLevel * 0.2f);
					//heal
					living.heal(getDeathHealAmount(stack));
				}
			}
		} else {
			//currently do nothing
		}
	}

	@Override
	public float getAttackDamage()
    {
		//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		if (IsSky(stack)){
			return base_damage_sky * ratio;
		}else {
			return base_damage * ratio + damage_per_pearl * GetPearlCount(stack);
		}
	}
	
//	public float getKillRate(ItemStack stack, EntityLivingBase target)
//	{
//		float targetHealth = Math.max(target.getHealth(), 1f);
//
//		if (IsSky(stack))
//		{
//			return 1;
//		}
//		else if (IsEarth(stack))
//		{
//			return earth_Chance_bonus + 1 / targetHealth;
//		}
//		else
//		{
//			return  1 / targetHealth;
//		}
//	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}

		float damage = getActualDamage(stack, ratio);
		int buffLevel = GetDeadlyBuffLevel(player);
		damage += buffLevel;

		boolean success = false;
		success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
		if (success) {
			if (target instanceof EntityLivingBase && ratio > 0.3) {
				PotionEffect zenBuff = ((EntityLivingBase)target).getActivePotionEffect(ZEN_HEART);
				if (zenBuff == null) {
					if (buffLevel > 0) {
						float killRate = (float) buffLevel / deadly_buff_full_divider;
						if (Math.random() < killRate) {
							if (IsNameHidden(stack)) {
								TrueNameReveal(stack, player.getEntityWorld(), player);
							}
							damage = Float.MAX_VALUE;
							player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 1f, 2f);
						}
					}
				}

//			if (Math.random() < getKillRate(stack, (EntityLivingBase) target))
//			{
//				if (IsNameHidden(stack))
//				{
//					TrueNameReveal(stack, player.getEntityWorld(), player);
//				}
//				damage = Float.MAX_VALUE;
//			}

//			if (IsSky(stack))
//			{
//				if (Math.random() < sky_suicide_rate)
//				{
//					player.setHealth(0);
//				}
//			}
			}
			stack.damageItem(1, player);
		}
		return success;
	}

	//Mode Switch, WIP
	final static int MAX_MODE = 5;
	final static int tickPerStage = 40;
	@Override
	public void serverUseTick(ItemStack stack, EntityLivingBase living, int count) {

		int mode = GetWeaponMode(stack);
		int curUseTickCount = getMaxItemUseDuration(stack) - count;
		if (curUseTickCount % tickPerStage == 0)
		{
//			for (int i = 0; i < 20; i++)
//			{
//				CreateParticle(stack, living, -3d);
//			}
			if (mode < MAX_MODE) {
				SetWeaponMode(stack, mode + 1);
				DWeapons.LogWarning("Mode ++");
			}
		}
	}

//	@Override
//	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count) {
////		//Particle;
//		//DWeapons.LogWarning(String.format("onUsingTick %s",count));
//
//		if (getMaxItemUseDuration(stack) - count >= GetChangeNeedTick(stack))
//		{
//			for (int i = 0; i < 20; i++)
//			{
//				CreateParticle(stack, living, -3d);
//			}
//		}
//		else
//		{
//			//CreateParticle(stack, living, -0.5d);
//		}
//	}
	/**
	 * Called when the player stops using an Item (stops holding the right mouse button).
	 */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//change mode
		if (!world.isRemote) {


		}
		return;
	}

	//-----------
	
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY, getDeadlyBuffTicks(stack) / 20f);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, getDeadlyBuffTicks(stack) / 20f);
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, getDeadlyBuffTicks(stack) / 20f);
    		tooltip.add(earthDesc);
    	}
    
		addDamageInformation(stack, worldIn, tooltip, flagIn);
	}
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		return getActualDamage(stack, 1f);
	}

}
