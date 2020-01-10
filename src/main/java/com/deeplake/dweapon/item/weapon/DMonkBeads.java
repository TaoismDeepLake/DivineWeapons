package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.init.ModPotions;
import com.deeplake.dweapon.util.DWEntityUtil;
import com.deeplake.dweapon.util.IHasModel;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sun.security.ssl.Debug;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;

//Just for copying
public class DMonkBeads extends DWeaponSwordBase implements IHasModel, IDWeaponEnhanceable {
	// /give @p dweapon:monk_beads 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DMonkBeads(String name, ToolMaterial material) {
		super(name, material);
		RefundPearlsAtSky = false;
		maxPearlCount = 108;
	}

	public static final float baseDamageAttackMode = 0;
	public static final float pearlDamageAttackMode = 1;//0.5 hearts
	public static final int initialPearlCount = 8;

	@Override
	public ItemStack getDefaultInstance() {
		return super.getDefaultInstance();
	}

	//special growth------------------------------------
	static final int minEarthPearlCount = 36;
	public boolean IsSky(ItemStack stack)
	{
		return GetPearlCount(stack) >= maxPearlCount;
	}
	public void SetSky(ItemStack stack)
	{
		AddPearlCount(stack, maxPearlCount - minEarthPearlCount);
	}

	public static boolean IsNameHidden(ItemStack stack)
	{
		return DWNBTUtil.GetBooleanDF(stack, DWNBTDef.IS_NAME_HIDDEN, true);
	}

	public static void SetNameHidden(ItemStack stack, boolean isHidden)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_NAME_HIDDEN, isHidden);
	}

	public boolean IsEarth(ItemStack stack){return GetPearlCount(stack) > minEarthPearlCount;}

	public void SetEarth(ItemStack stack)
	{
		AddPearlCount(stack, minEarthPearlCount);
	}

	//---------------------------------------------------
	public float GetDamageReduction(ItemStack stack)
	{
		return GetPearlCount(stack) / 5;
	}

	@SubscribeEvent
	public static void onCreatureDamaged(LivingDamageEvent evt) {
		World world = evt.getEntity().getEntityWorld();
		EntityLivingBase hurtOne = evt.getEntityLiving();
		if (!world.isRemote) {
			Entity trueSource = evt.getSource().getTrueSource();
			if (trueSource instanceof EntityLivingBase){
				EntityLivingBase sourceCreature = (EntityLivingBase)trueSource;
				if (sourceCreature.isEntityUndead())
				{
					ItemStack stack = hurtOne.getHeldItemMainhand();
					if (stack.getItem() instanceof DMonkBeads){
						float oriDamage = evt.getAmount();
						float reduction = ModItems.MONK_BEADS.GetDamageReduction(stack);
						if (oriDamage <= reduction) {
							evt.setCanceled(true);
							for (int i = 1; i <= oriDamage && i <= 10; i++){
								CreateParticle(stack, hurtOne, i);
							}
						}
						else {
							evt.setAmount(oriDamage - reduction);
						}
					}
				}

			}

		} else {
			//currently do nothing
		}
	}

	//---------------------------------------------------

	private static void CreateParticle(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;

		double vx = 0d;
		double vy = -rand.nextDouble() * vm;
		double vz = 0d;

		living.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
				x,y,z,vx,vy,vz);
	}

	@Override
	public float getAttackDamage()
    {
		//useless
		NBTTagString s = new NBTTagString();
		return baseDamageAttackMode;
		
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		float damage = baseDamageAttackMode + pearlDamageAttackMode * GetPearlCount(stack);
		return damage;
	}
	
	boolean IsCleansable(Entity target)
	{
		if (target instanceof EntityZombieVillager){
			return true;
		}
		return false;
	}

	boolean TryCleanse(Entity target, EntityPlayer source)
	{
		if (IsCleansable(target)){
			Cleanse(target, source);
			return true;
		}
		return false;
	}

	@Nullable
	void Cleanse(Entity target, EntityPlayer source)
	{
		if (target instanceof EntityZombieVillager){
			//Copied and slightly modified vanilla ZombieVillager code
			EntityZombieVillager targetZBV = (EntityZombieVillager)target;
			EntityVillager entityvillager = new EntityVillager(targetZBV.world);
			entityvillager.copyLocationAndAnglesFrom(targetZBV);
			entityvillager.setProfession(targetZBV.getForgeProfession());
			entityvillager.finalizeMobSpawn(targetZBV.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null, false);
			entityvillager.setLookingForHome();

			if (targetZBV.isChild())
			{
				entityvillager.setGrowingAge(-24000);
			}

			targetZBV.world.removeEntity(targetZBV);
			entityvillager.setNoAI(targetZBV.isAIDisabled());

			if (targetZBV.hasCustomName())
			{
				entityvillager.setCustomNameTag(targetZBV.getCustomNameTag());
				entityvillager.setAlwaysRenderNameTag(targetZBV.getAlwaysRenderNameTag());
			}

			targetZBV.world.spawnEntity(entityvillager);

			if (source != null)
			{
				if (source instanceof EntityPlayerMP)
				{
					CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((EntityPlayerMP)source, targetZBV, entityvillager);
				}
			}

			entityvillager.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
			targetZBV.world.playEvent((EntityPlayer)null, 1027, new BlockPos((int)targetZBV.posX, (int)targetZBV.posY, (int)targetZBV.posZ), 0);

			//Added code
			//Foreach cleansing, add a pearl
			if (source != null){
				ItemStack stack = source.getHeldItemMainhand();
				if (stack.getItem() instanceof DMonkBeads){
					if (GetPearlCount(stack) < maxPearlCount)
					{
						SetPearlCount(stack, GetPearlCount(stack) + 1);
					}
				}
			}
		}
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		float damage = getActualDamage(stack, ratio);
		boolean success = false;
		success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
		if (success)
		{
			stack.damageItem(1, player);
		}
			
		return success;
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (playerIn.world.isRemote) {
			return false;
		}

		if (target instanceof EntityLiving) {
			Cleanse(target, playerIn);
			return true;
		}

		//Removes one debuff
		Collection<PotionEffect> activePotionEffects = target.getActivePotionEffects();
		for (int i = 0; i < activePotionEffects.size(); i++) {
			PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
			if (buff.getPotion().isBadEffect()){
				target.removePotionEffect(buff.getPotion());
				return true;
			}
			else
			{

			}
		}

		return false;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);

		//DWeapons.Log(player.getDisplayNameString() + " made a pray.");
		if ((player.getRNG().nextInt(player.experienceLevel + 1) == 0)) {
			if (world.isRemote) {
				for (int i = 1; i <= 5; i++){
					CreateParticle(stack, player, i);
				}
			}else {
				player.addExperience(1);
				world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.PLAYERS, 1f, 2f);
			}
		}
		else {
			if (!world.isRemote) {
				world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.PLAYERS, 0.5f, player.getRNG().nextFloat());
			}
		}

		if (!world.isRemote) {
			//Removes one debuff
			Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
			for (int i = 0; i < activePotionEffects.size(); i++) {
				PotionEffect buff = (PotionEffect) activePotionEffects.toArray()[i];
				if (buff.getPotion().isBadEffect()) {
					player.removePotionEffect(buff.getPotion());
					return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
				} else {

				}
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			if (GetPearlCount(stack) < initialPearlCount) {
				SetPearlCount(stack, initialPearlCount);
				DWeapons.LogWarning("Set to " + initialPearlCount);
			}
		}

		if (((IsEarth(stack) && isSelected) || IsSky(stack)) && entityIn instanceof EntityLivingBase){
			if (!worldIn.isRemote) {
				DWEntityUtil.TryRemoveDebuff((EntityLivingBase) entityIn);
				if (IsSky(stack)){
					((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(ModPotions.ZEN_HEART, 100, 0));
					//DWeapons.Log("cast zen heart");
				}
			}
			else {
				CreateParticle(stack, (EntityLivingBase)entityIn, 4);
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
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, GetDamageReduction(stack));
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, GetDamageReduction(stack));
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	return getActualDamage(stack, 1);
    }
}
