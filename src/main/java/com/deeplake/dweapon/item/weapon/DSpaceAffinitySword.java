package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deeplake.dweapon.util.Reference;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import static com.deeplake.dweapon.init.ModPotions.DEADLY;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
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
		return GetPearlCount(stack) + 1;
	}
	
	@Override
	public float getAttackDamage()
    {
        return super.getAttackDamage();
    }

    static final int debuffLevel = 0;
	static final int slowTime = 60;//3s
	private static float range = 5f;

	@SubscribeEvent
	public static void onCreatureTeleport(EnderTeleportEvent evt) {
		World world = evt.getEntity().getEntityWorld();
		EntityLivingBase teleportee = evt.getEntityLiving();
		Vec3d pos = evt.getEntity().getPositionEyes(0);
		Vec3d pos2 = new Vec3d(evt.getTargetX(), evt.getTargetY(), evt.getTargetZ());
		if (!world.isRemote) {
			//wielder stops nearby end-power teleporting and damage teleporters.
			List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.addVector(-range, -range, -range), pos.addVector(range, range, range)));
			for (EntityLivingBase living : list) {
				ItemStack stack = living.getHeldItemMainhand();
				if (stack.getItem() instanceof DSpaceAffinitySword) {
					teleportee.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slowTime, debuffLevel));
					//play sound
					world.playSound(null, living.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.AMBIENT, 1f, 1f);
					evt.setCanceled(true);
					return;
				}
			}

			list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos2.addVector(-range, -range, -range), pos2.addVector(range, range, range)));
			for (EntityLivingBase living : list) {
				ItemStack stack = living.getHeldItemMainhand();
				if (stack.getItem() instanceof DSpaceAffinitySword) {
					teleportee.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slowTime, debuffLevel));
					//play sound
					world.playSound(null, living.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.AMBIENT, 1f, 1f);
					evt.setCanceled(true);
					return;
				}
			}
		} else {
			//currently do nothing
		}
	}
	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}

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
			TrueNameReveal(stack, player.getEntityWorld(), player);
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
		if (getMaxItemUseDuration(stack) - count >=  backTick) 
		{
			for (int i = 0; i < 20; i++)
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
    		String skyDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH, minDamage(stack));
    		tooltip.add(earthDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, minDamage(stack));
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		if (IsSky(stack)) {
			return 70f;
		}
		else
		{
			return 7.5f + (float)GetPearlCount(stack) / 2;
		}

    }
}
