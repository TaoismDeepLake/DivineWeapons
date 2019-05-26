package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DSpaceAffinitySword extends DWeaponSwordBase {

	public DSpaceAffinitySword(String name, ToolMaterial material) {
		super(name, material);
		
	}

	public static final int backTick = 40;//2 sec
	
	float min_damage = 1f;
	//float max_damage = 16f;
	public float minDamage(ItemStack stack)
	{
		if (IsSky(stack)) {
			return 16;
		}
		return GetPearlCount(stack);
	}
	
	@Override
	public float getAttackDamage()
    {
        return super.getAttackDamage();
    }
	
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		Vec3d pos = player.getPositionVector();
		
		double damageDivider = 16d;
		if (IsSky(stack)) {
			damageDivider = 128d;
		}
		
		float argX = (float)(Math.abs(pos.x % damageDivider));
		float argY = (float)(Math.abs(pos.y % damageDivider));
		float argZ = (float)(Math.abs(pos.z % damageDivider));
		
		float damage = Math.max(Math.max(argX, argY), Math.max(argZ, minDamage(stack))) * ratio;
		
		boolean success = false;

		success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
		
		stack.damageItem(1, player);
		
		if (IsNameHidden(stack) && (ratio > 15f/16f))
		{
			SetNameHidden(stack, false);
		}
		
		return success;
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
//		//Particle;
		//DWeapons.LogWarning(String.format("onUsingTick %s",count));

		if (getMaxItemUseDuration(stack) - count >=  backTick) 
		{
			for (int i = 0; i < 20; i++)
			{
				CreateParticle(stack, living, -3d);
			}
		}
		else
		{
			//CreateParticle(stack, living, -0.5d);
		}
				
	}
	
	private void CreateParticle(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = rand.nextDouble() * vm + 1d;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.PORTAL,
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
		if (IsEarth(stack) || IsSky(stack)) 
		{
			return EnumAction.BOW;
		}
		else
		{
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
		if (!world.isRemote) {

			//DWeapons.LogWarning(String.format("onPlayerStoppedUsing %s",time));
			
			if ((IsEarth(stack) || IsSky(stack)) && 
					(getMaxItemUseDuration(stack) - time >= backTick)) {
				//DWeapons.LogWarning("Start check");
				
				//teleports back home
				BlockPos pos = world.getSpawnPoint();
				//DWeapons.LogWarning(String.format("Target = (%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ()) );
				
				if (living instanceof EntityPlayer) {
					//DWeapons.LogWarning("Is Player");
					EntityPlayer player = (EntityPlayer) living;
					BlockPos bedPos = player.getBedLocation();
					if (bedPos != null) {
						pos = bedPos;
					}
				}
				
				DWeapons.LogWarning(String.format("SpaceAffinity: %s to (%s, %s, %s)",living.getName() , pos.getX(), pos.getY(), pos.getZ()) );
				
				living.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
				stack.damageItem(10, living);
				
				//enderman, water
				//living.attemptTeleport(pos.getX(), pos.getY(), pos.getZ());
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY, GetPearlCount(stack));
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, GetPearlCount(stack));
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL);
    		tooltip.add(earthDesc);
    	}
    }
}
