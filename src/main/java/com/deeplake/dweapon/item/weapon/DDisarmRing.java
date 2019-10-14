package com.deeplake.dweapon.item.weapon;

import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

//TODO: Consider using ProjectileImpactEvent

public class DDisarmRing extends DWeaponSwordBase {
	public DDisarmRing(String name, ToolMaterial material) {
		super(name, material);
	}

	private float base_damage = 4;
	private static final float baseDamage = 5;//2 hearts
	public static final float pearlDamage = 1;//2 hearts
	public static final float earthDamageModifier = 2f;
	public static final float skyDamageModifier = 10f;
	
	@Override
	public float getAttackDamage()
    {//useless
		return base_damage;
    }
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		float damage = baseDamage * ratio + pearlDamage * GetPearlCount(stack);
		if (IsSky(stack))
		{
			damage *= skyDamageModifier;
		} 
		else if (IsEarth(stack))
		{
			damage *= earthDamageModifier;
		}
		
		return damage;
	}

	private static float baseChance = 0.5f;
	private static float pearlChance = 0.1f;
	private float DisarmChance(final ItemStack stack)
	{
		if (IsSky(stack) || IsEarth(stack))
		{
			return 1f;
		}
		return baseChance + pearlChance * GetPearlCount(stack);
	}

	private void TryDisarm(final ItemStack stack, final EntityLivingBase targetCreature, final EntityPlayer player)
	{
		if (targetCreature.getRNG().nextFloat() < DisarmChance(stack))
		{
			if (!targetCreature.world.isRemote) {
				if (IsSky(stack)) {
					DrawItemDirect(targetCreature, player, true);
				}
				else{
					Disarm(targetCreature);
				}
			}
		}
		else{
			//Disarm Failed
			if (targetCreature.world.isRemote) {
				targetCreature.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1.5f, 1);
			}
		}
	}

	//makes the target throw away the main-hand item
	private void Disarm(final EntityLivingBase targetCreature)
	{
		if (targetCreature.world.isRemote) {
			targetCreature.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.5f, 1);
		}
		else {
			ItemStack held = targetCreature.getHeldItemMainhand();
			EnumHand enumHand = EnumHand.MAIN_HAND;
			targetCreature.setHeldItem(enumHand, ItemStack.EMPTY);
			targetCreature.entityDropItem(held, 0);
		}
	}
	//takes the main-hand item directly to owner's inventory
	private void DrawItemDirect(final EntityLivingBase targetCreature, final EntityPlayer player, final boolean includeOffhand)
	{
		if (targetCreature.world.isRemote) {
			targetCreature.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.5f, 1);
		}
		else {
			ItemStack held = targetCreature.getHeldItemMainhand();
			EnumHand enumHand = EnumHand.MAIN_HAND;
			targetCreature.setHeldItem(enumHand, ItemStack.EMPTY);
			player.addItemStackToInventory(held);
			if (includeOffhand) {
				enumHand = EnumHand.OFF_HAND;
				held = targetCreature.getHeldItemOffhand();
				targetCreature.setHeldItem(enumHand, ItemStack.EMPTY);
				player.addItemStackToInventory(held);
			}
		}
	}

	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {

		if (player.world.isRemote) {
			return false;
		}

		float damage = getActualDamage(stack, ratio);
		
		boolean success = false;
		success = target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
		if (success)
		{
			if (target instanceof EntityLiving) {
				TryDisarm(stack, (EntityLiving)target, player);
			}
			stack.damageItem(1, player);
		}
			
		return success;
	}
	
	//Note: only players will call this.
	//Monsters won't.
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (isSelected && IsSky((stack)))
		{
			disarmProjectiles(stack, (EntityLivingBase) entityIn);
		}
	}

	@Override
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count)
	{
		//todo: add particle flying towards player.
		float particleSpeed = 2f;
		CreateParticle(stack, living, particleSpeed);
	}

	@Override
	public void serverUseTick(ItemStack stack, EntityLivingBase living, int count)
	{
		if (IsSky(stack))
		{
			//Todo: ranged disarm, needs to know how to do not-axis-aligned bounding box
			disarmNearbyLiving(stack, living);
			RemoveWaterAndFire(stack, living);
		}else
		{
			//sky level do this in tick.
			disarmProjectiles(stack, living);
		}
	}

	void RemoveWaterAndFire(ItemStack stack, EntityLivingBase living)
	{
		BlockPos origin = living.getPosition();
		BlockPos target = origin;
		int range = 2;
		World worldIn = living.world;

		for (int x = -range; x <= range; x++){
			for (int y = -range; y <= range; y++){
				for (int z = -range; z <= range; z++){
					target = origin.add(x,y,z);
					IBlockState targetBlock = worldIn.getBlockState(target);

					if (targetBlock.getBlock() == Blocks.WATER || targetBlock.getBlock() == Blocks.FLOWING_WATER ||
							targetBlock.getBlock() == Blocks.LAVA || targetBlock.getBlock() == Blocks.FLOWING_LAVA ||
							targetBlock.getBlock() == Blocks.MAGMA || targetBlock.getBlock() == Blocks.FIRE )
					{
						worldIn.setBlockState(target, Blocks.AIR.getDefaultState());
					}
				}
			}
		}


	}

//	private static final Predicate<Entity> WEAPON_WIELDER = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>()
//	{
//		public boolean apply(@Nullable Entity p_apply_1_)
//		{
//			return p_apply_1_.canBeCollidedWith();
//		}
//	});

	//I still haven't figured out how to make a not-axis-aligned AABB
//	@Nullable
//	protected Entity findEntityOnPath(EntityLivingBase caster, Vec3d start, Vec3d end)
//	{
//		Entity entity = null;
//		List<Entity> list = caster.world.getEntitiesInAABBexcluding(caster, caster.getEntityBoundingBox().expand(caster.motionX, caster.motionY, caster.motionZ).grow(1.0D), WEAPON_WIELDER);
//		double d0 = 0.0D;
//
//		for (int i = 0; i < list.size(); ++i)
//		{
//			Entity entity1 = list.get(i);
//
//			if (entity1 != caster)
//			{
//				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
//				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
//
//				if (raytraceresult != null)
//				{
//					double d1 = start.squareDistanceTo(raytraceresult.hitVec);
//
//					if (d1 < d0 || d0 == 0.0D)
//					{
//						entity = entity1;
//						d0 = d1;
//					}
//				}
//			}
//		}
//
//		return entity;
//	}
	
	private double horizontalRange = 4d;
	private void disarmProjectiles(ItemStack stack, EntityLivingBase living)
	{
		if (living.world.isRemote) {
			return;
		}

		if (living instanceof EntityPlayer) {
			World worldIn = living.world;
			Vec3d pos = living.getPositionEyes(1.0F);

			List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class,
					new AxisAlignedBB(pos.addVector(-horizontalRange, -2, -horizontalRange), pos.addVector(horizontalRange, 2, horizontalRange)));
			for (Entity entity : entities) {
				HandleProjectile(stack, entity, (EntityPlayer) living);
			}
		}
	}

	private ItemStack GetCorrespondingStack(Entity projectile)
	{
		if (projectile instanceof IProjectile) {
			if (projectile instanceof EntityArrow) {
				EntityArrow arrow = (EntityArrow) projectile;
				return GetArrowStack(arrow);
			}
		} else if (projectile instanceof EntityFireball)
		{
			return new ItemStack(Items.FIRE_CHARGE);
		}else if  (projectile instanceof EntityTNTPrimed) {
			return new ItemStack(Blocks.TNT.getItemDropped(Blocks.TNT.getDefaultState(), null, 0));
		}

		return ItemStack.EMPTY;
	}

	void SimulatePickUp(EntityItem itemEntity, EntityPlayer player)
	{
		ItemStack itemstack = itemEntity.getItem();
		Item item = itemstack.getItem();
		int i = itemstack.getCount();

		int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(itemEntity, player);
		if (hook < 0) return;
		ItemStack clone = itemstack.copy();

		if ((hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack) || clone.getCount() > itemEntity.getItem().getCount()))
		{
			clone.setCount(clone.getCount() - itemEntity.getItem().getCount());
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, itemEntity, clone);

			if (itemstack.isEmpty())
			{
				player.onItemPickup(itemEntity, i);
				itemEntity.setDead();
				itemstack.setCount(i);
			}

			player.addStat(StatList.getObjectsPickedUpStats(item), i);
		}
	}
	
	private void HandleProjectile(ItemStack stack, Entity projectile, EntityPlayer player)
	{
		if (projectile.isDead || player.world.isRemote)
		{
			return;
		}
		if (projectile instanceof EntityItem){
			EntityItem itemEntity = (EntityItem)projectile;
			ItemStack itemStack = itemEntity.getItem();
			if (itemStack.isEmpty() == false)
			{
				SimulatePickUp(itemEntity, player);
			}
		}
		
		boolean isDirect = IsSky(stack);
		if (projectile instanceof IProjectile ||
				projectile instanceof EntityFireball ||
				projectile instanceof EntityTNTPrimed ||
				projectile instanceof EntityShulkerBullet) {
			ItemStack result = GetCorrespondingStack(projectile);
			if (result != null) {
				if (projectile instanceof EntityTNTPrimed) {
					((EntityTNTPrimed) projectile).setFuse(88);
				}

				if (isDirect) {
					player.addItemStackToInventory(result);
				} else {
					projectile.entityDropItem(result, 0.1F);
				}
			}
			projectile.setDead();
		}
		//todo: should handle falling block, and such.
	}

	private void disarmNearbyLiving(ItemStack stack, EntityLivingBase caster)
	{
		if (caster.world.isRemote){
			return;
		}

		World worldIn = caster.world;
		Vec3d pos = caster.getPositionEyes(1.0F);

		List<Entity> entities = worldIn.getEntitiesWithinAABB(Entity.class,
				new AxisAlignedBB(pos.addVector(-horizontalRange,-2,-horizontalRange), pos.addVector(horizontalRange,2,horizontalRange)));
		for (Entity entity : entities)
		{
			if (entity == caster) {
				continue;//skip self
			}

			if (!(entity instanceof EntityPlayer) && entity instanceof EntityLivingBase) {
				DrawItemDirect((EntityLivingBase)entity, (EntityPlayer) caster, true);
			}
		}
	}
	//Tried to get the arrow from the entity.
	//there is an implemented method, but it's protected.
	//The only way I can think of to support light arrow and tipped arrows are rewriting them from nbt.
	private ItemStack GetArrowStack(EntityArrow arrow) {
		return new ItemStack(Items.ARROW);
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

		living.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
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
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (target instanceof EntityLiving) {
			if (IsSky(stack)) {
				DrawItemDirect(target, playerIn, true);
				return true;
			}
		    else if (IsEarth(stack)) {
				DrawItemDirect(target, playerIn, false);
				return true;
			}
		    else //man-made level
			{
				TryDisarm(stack, (EntityLiving) target, playerIn);
			}
		}

		return false;
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
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL, DisarmChance(stack) * 100);
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	return getActualDamage(stack, 1);
    }
}
