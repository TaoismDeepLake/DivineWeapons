package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector3d;

import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DSnowSword extends DWeaponSwordBase {
	// /give @p dweapon:snow_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DSnowSword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	static final float base_damage = 5f;
	static final float base_damage_earth = 10f;
	static final float base_damage_sky = 20f;

	static final float pearl_damage_factor = 0.2f;
	static final float t_base = 1.0f;//temperature_base
	//static final float t_max = 2.0f;//hotest temperature
	
	static final float snowing_multiplier = 2.0f;//damage rate when snowing
	
	static final int skyBuffTick = 100;

	public static final int snowTime = 1200;
	
	private static int weatherSummonTick = 100;
	public static final int NORMAL_MODE = 0;
	public static final int CAN_SNOW_MODE = 1;
	public static final int SNOWING_MODE = 2;
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		boolean isSnowBlock = repairMaterial.getItem() == Item.getItemFromBlock(Blocks.SNOW);
		
		return isSnowBlock || super.getIsRepairable(stack, repairMaterial);
	}
	
	@Override
	public float getAttackDamage()
    {
		//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio, float t, boolean isSnowing)
	{	
		float result = IsSky(stack) ? base_damage_sky :
				(IsEarth(stack) ? base_damage_earth :
						base_damage) ;
		result *= ratio;

		int pearlCount = GetPearlCount(stack);
		if (IsSky(stack)){
			pearlCount = GetPearlMax(stack);
		}
		else
		{
			result += pearlCount;
		}

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

		if (isSnowing) {
			result *= snowing_multiplier;
		}

		return result;
	}
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		boolean isSnowing = IsSnowingHere(player);
		float t = GetTemperatureHere(player);
		float damage = getActualDamage(stack, ratio, t, isSnowing);
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);

		if (player.world.isRemote) {
			CreateOnHitExplosion(player, target, damage);
		}
		else {
			stack.damageItem(1, player);
			if (IsNameHidden(stack) && (isSnowing)) {
				TrueNameReveal(stack, player.getEntityWorld(), player);
			}
		}
		return success;
	}

	private static int maxSnowFlake = 20;
	private void CreateOnHitExplosion(final EntityPlayer player, final Entity target, final float damage){
		int snowFlakeCount = Math.min ((int)damage, maxSnowFlake);
		float flySpeed =  Math.min (damage / 3f, 0.3f);
		//DWeapons.LogWarning(String.format("snowflak count = %s, flySpeed = %s", snowFlakeCount, flySpeed));
		for (int i = 1; i <= snowFlakeCount; i++){
			CreateParticleExplosion(target,0.3f, player.getLookVec(),  flySpeed);
		}
	}

	@Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (entityIn instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)(entityIn); 
			
			//creating snow
			if (IsSky(stack) && isSelected)
			{//same as snow golem
				int i = 0;
	            int j = 0;
	            int k = 0;
				
				playerMP.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, skyBuffTick, 0));
			
				for (int l = 0; l < 4; ++l)
	            {
	                i = MathHelper.floor(playerMP.posX + (double)((float)(l % 2 * 2 - 1) * 0.25F));
	                j = MathHelper.floor(playerMP.posY);
	                k = MathHelper.floor(playerMP.posZ + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
	                BlockPos blockpos = new BlockPos(i, j, k);

	                if (playerMP.world.getBlockState(blockpos).getMaterial() == Material.AIR && 
	                		playerMP.world.getBiome(blockpos).getTemperature(blockpos) < 0.8F &&
	                		Blocks.SNOW_LAYER.canPlaceBlockAt(playerMP.world, blockpos))
	                {
	                	playerMP.world.setBlockState(blockpos, Blocks.SNOW_LAYER.getDefaultState());
	                }
	            } 
			}

			if (CanSnowHere(playerMP)) {
				boolean isSnowing = IsSnowingHere(playerMP);
				if (isSnowing) {
					if (stack.isItemDamaged())
					{//auto fix
						int curDamage = stack.getItemDamage();
						int fixAmount = 1 + playerMP.getRNG().nextInt(1 + GetPearlCount(stack));
						
						stack.setItemDamage(Math.max(curDamage - fixAmount, 0));
					}
					SetWeaponMode(stack, SNOWING_MODE);
				}
				else
				{
					if (IsSky(stack)) {
						SetWeaponMode(stack, CAN_SNOW_MODE);
					}else {
						SetWeaponMode(stack, NORMAL_MODE);
					}

				}
			}
			else
			{
				SetWeaponMode(stack, NORMAL_MODE);
			}
		}
		
		if (worldIn.isRemote && isSelected)
		{
			CreateParticle(stack,(EntityLivingBase) entityIn, 0.1f);
		}
    }

	/**
     * Called when a Block is right-clicked with this Item
     */
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

	@Override
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count) {
		if (!IsSky(stack))//only sky can summon snow storm
		{
			return;
		}
		
		if (getMaxItemUseDuration(stack) - count >=  weatherSummonTick) 
		{
			for (int i = 0; i < 10; i++)
			{
				CreateParticle(stack, living, -3d);
			}
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
		
		living.world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL,
				x,y,z,vx,vy,vz);
	}

	private void CreateParticleExplosion(Entity living, double vRandom, Vec3d baseDir, float baseSpeed) {
		Vec3d flyDir = baseDir.addVector(0,1f,0).normalize();

		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;

		double vx = (rand.nextDouble() - 0.5d) * vRandom + flyDir.x * baseSpeed;
		double vy = (rand.nextDouble() - 0.5d) * vRandom + flyDir.y * baseSpeed;
		double vz = (rand.nextDouble() - 0.5d) * vRandom + flyDir.z * baseSpeed;

		//DWeapons.LogWarning(String.format("pos = (%.2f, %.2f, %.2f), v = (%.2f, %.2f, %.2f),",x,y,z,vx,vy,vz));
		living.world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL,
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
		int mode = GetWeaponMode(stack);
		if (IsSky(stack) && mode != NORMAL_MODE) 
		{
			return EnumAction.BOW;
		}
		else {
			return EnumAction.NONE;
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	

	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//DWeapons.LogWarning("onPlayerStoppedUsing");
		int mode = GetWeaponMode(stack);
		
		if (IsSky(stack) && 
				(getMaxItemUseDuration(stack) - time >= weatherSummonTick)) {	
			
			if (!world.isRemote) {
				if (mode != NORMAL_MODE)
				{
					EntityPlayerMP playerMP = (EntityPlayerMP)(living); 
					WorldInfo worldInfo = playerMP.mcServer.worlds[0].getWorldInfo();
					
					worldInfo.setRaining(true);
					
					worldInfo.setRainTime(snowTime);
				}
			}
			else
			{
				if (mode == NORMAL_MODE) {//cannot snow here
					living.playSound(SoundEvents.ENTITY_BLAZE_BURN, 0.6f, 1);
				} else
				{
					living.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.5f, 1);
				}
			}
		}
		
		return;
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
//		int pearlCount = GetPearlCount(stack);
//
//		if (IsSky(stack)){
//			pearlCount = GetPearlMax(stack);
//		}
//
//		if (IsSky(stack)||IsEarth(stack)) {
//			return base_damage  * (1 + (0.5f) * ( 1 + pearl_damage_factor * pearlCount));
//		}
//		else
//		{
//			return base_damage * (1 + (0.1f) * ( 1 + pearl_damage_factor * pearlCount));
//		}
		return getActualDamage(stack, 1, 0.9f, false);
    }
}
