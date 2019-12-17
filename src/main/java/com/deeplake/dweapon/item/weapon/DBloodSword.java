package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.Reference;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.RegistryEvent;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
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

	private static float regen = 0.5f;
	private static float range = 5f;
	@SubscribeEvent
	public static void onCreatureHurt(LivingHurtEvent evt) {
		if (evt.getAmount() >= regen) {
			World world = evt.getEntity().getEntityWorld();
			Vec3d pos = evt.getEntity().getPositionEyes(0);
			if (!world.isRemote) {
				List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.addVector(-range,-range,-range), pos.addVector(range,range,range)));
				for (EntityLivingBase living:list ) {
					ItemStack stack = living.getHeldItemMainhand();

					if (stack.getItem() instanceof DBloodSword && (ModItems.BLOOD_SWORD.IsEarth(stack) || ModItems.BLOOD_SWORD.IsSky(stack))) {
						living.heal(regen);
						world.playSound(null, living.getPosition(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.PLAYERS, 1f,2f);
					}
				}
			}
			else
			{

			}
		}
	}

	@Override
	public float getAttackDamage()
    {
		//not of much use, since we have custom damage system
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		if (IsSky(stack)) {
			return skyDamage;
		}
		else
		{
			return getBloodBurst(stack) + base_damage * ratio;
		}
	}
	
	public float getBloodBurst(ItemStack stack)
	{
		return (GetPearlCount(stack) + 1) * damage_per_pearl ;
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

		boolean isRemote = player.world.isRemote;

		float preHP = player.getHealth();
		if (!player.capabilities.isCreativeMode && !isRemote) {
			//since in creative mode you can't see your health bar,
			//kiling you by draining makes no sense(yeah this code can kill creative players)
			player.setHealth(preHP - getHurt(stack));//drain self
		}
		float damage = getActualDamage(stack, ratio);
		
		boolean success = false;
		if (!isRemote) {
			success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
			if (success)
			{
				if (IsSky(stack))
				{
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, skyBuffTick, skyRegenLevel - 1));
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, skyBuffTick, skyStrengthLevel - 1));
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
			}
		}
		else{
			if (IsSky(stack))
			{
				for (int i = 1; i <= 9; i++)
				{
					CreateParticle(stack, player, 1);
				}
			}
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
		if (stack.isItemDamaged())
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
	
	public static final float healthPerRepair = 2f;
	public static final int durabilityPerRepair = 100;
	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//DWeapons.LogWarning("onPlayerStoppedUsing");
		if (stack.isItemDamaged())
		{
			if (!world.isRemote) {
				
				float preHP = living.getHealth();
				
				living.setHealth(preHP - healthPerRepair);//drain self
				int curDamage = stack.getItemDamage();
				
				stack.setItemDamage(Math.max(curDamage - durabilityPerRepair, 0));
			}
			else
			{
				living.playSound(SoundEvents.ENTITY_PLAYER_HURT, 1f, 2f);
			}
		}
		
		return;
	}
}
