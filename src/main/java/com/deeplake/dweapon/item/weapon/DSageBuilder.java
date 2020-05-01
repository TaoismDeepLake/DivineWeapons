package com.deeplake.dweapon.item.weapon;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deeplake.dweapon.potion.ModPotions;
import com.deeplake.dweapon.util.NBTStrDef.IDLGeneral;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.deeplake.dweapon.util.DWNBT.TICK_PER_SECOND;

//Fu yue is raised from building
public class DSageBuilder extends DWeaponSwordBase {
	// /give @p dweapon:blood_sword 1 0 {is_earth:false, is_sky:false, pearl_count:0}
	public DSageBuilder(String name, ToolMaterial material) {
		super(name, material);
	}

	float base_damage = 4;
	float sky_damage = 10;
	
	float sky_damage_sword = 40;
	float earth_damage_sword = 25;
	
	public float getActualDamage(ItemStack stack, float ratio)
	{
		int mode = GetWeaponMode(stack);
		if (mode == SWORD_MODE)
		{
			if (IsSky(stack)) {
				return sky_damage_sword * ratio;
			}else {
				return earth_damage_sword * ratio;
			}
		}
			
		if (IsSky(stack)) {
			return sky_damage * ratio;
		}
		else
		{
			return (base_damage * ratio + GetPearlCount(stack));
		}
	}

	
	@Override
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio) {
		if (player.world.isRemote) {
			return false;
		}
		float damage = getActualDamage(stack, ratio);
		boolean success = target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);

		stack.damageItem(1, player);
		
		return success;
	}
	
	//---------------------------------------------------------
	//right click
	
	 public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	 public IBlockState getBlockToPlace(ItemStack stack)
	 {
		 if (IsSky(stack))
		 {
			 //this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE)
			 return Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.values()[GetWeaponMode(stack)]);
			 //return Blocks.CONCRETE.getDefaultState();
		 }
		 else if (IsEarth(stack))
		 {
			 return Blocks.BRICK_BLOCK.getDefaultState();
		 }
		 else
		 {
			 return Blocks.DIRT.getDefaultState();
		 }
	 }
	 
	 
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		ItemStack stack = player.getHeldItem(hand);
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
	
	private void CreateParticle2(ItemStack stack, EntityLivingBase living, double vm) {
		Random rand = new Random();
		double r = 1d;
		double x = living.posX + (rand.nextDouble() - 0.5d) * r;
		double y = living.posY + rand.nextDouble() * living.height;
		double z = living.posZ + (rand.nextDouble() - 0.5d) * r;
		
		double vx = 0d;
		double vy = rand.nextDouble() * vm + 1d;
		double vz = 0d;
		
		living.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
				x,y,z,vx,vy,vz);
	}

	@Override
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count) {
//		//Particle;
		//DWeapons.LogWarning(String.format("onUsingTick %s",count));

		if (getMaxItemUseDuration(stack) - count >= changeNeedTick)
		{
			if (IsEarth(stack) || IsSky(stack)) {
				for (int i = 0; i < 20; i++) {
					CreateParticle(stack, living, -3d);
				}
			}
		}
		else if (getMaxItemUseDuration(stack) - count >= changeColorNeedTick)
		{
			if (IsSky(stack)){
				int mode = GetWeaponMode(stack);
				if (mode != SWORD_MODE) {
					CreateParticle2(stack, living, -0.5d);
				}
			}
		}		
	}

	public float getHaloRange(ItemStack stack)
	{
		return 15f;
	}

	public int getApplyBuffLevel(ItemStack stack){
		return IsSky(stack) ? 1 : IsEarth(stack) ? 0 : -1;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (isSelected)
		{
			float range = getHaloRange(stack);
			Vec3d mypos = entityIn.getPositionVector();
			if (worldIn.isRemote)
			{
				//CreateParticleStorm(effectLevel, player);
			}
			else {
				int buffLevel = getApplyBuffLevel(stack);
				if (buffLevel >= 0)
				{
					List<EntityLivingBase> list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, IDLGeneral.ServerAABB(mypos.addVector(-range, -range, -range), mypos.addVector(range, range, range)));
					for (EntityLivingBase living : list) {
						if (living instanceof EntityPlayer) {
//						if (living == entityIn ||
//								(living.getTeam() == entityIn.getTeam() && (entityIn.getTeam() != null))) {
							//snow sword counters the effect.
							if (GetWeaponMode(stack) == SWORD_MODE)
							{
								living.addPotionEffect(new PotionEffect(ModPotions.KING_BOON, TICK_PER_SECOND, getApplyBuffLevel(stack)));
								living.addPotionEffect(new PotionEffect(MobEffects.SPEED, TICK_PER_SECOND, getApplyBuffLevel(stack)));
							}else //shovel mode
							{
								living.addPotionEffect(new PotionEffect(ModPotions.SAGE_BOON, TICK_PER_SECOND, getApplyBuffLevel(stack)));
								living.addPotionEffect(new PotionEffect(MobEffects.HASTE, TICK_PER_SECOND, getApplyBuffLevel(stack)));
							}
						}
						else {

						}
					}
				}
			}
		}
	}

	protected Item.ToolMaterial toolMaterial = ModItems.TOOL_MATERIAL_DIVINE;
	/**
	 * Check whether this Item can harvest the given Block
	 */
	public boolean canHarvestBlock(IBlockState blockIn, ItemStack stack)
	{
		if (GetWeaponMode(stack) == SWORD_MODE)
		{
			//merely copied ItemSword
			return blockIn.getBlock() == Blocks.WEB;
		}else {
			//merely copied ItemPickaxe
			Block block = blockIn.getBlock();

			if (block == Blocks.OBSIDIAN)
			{
				return this.toolMaterial.getHarvestLevel() == 3;
			}
			else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE)
			{
				if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK)
				{
					if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE)
					{
						if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE)
						{
							if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE)
							{
								if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE)
								{
									Material material = blockIn.getMaterial();

									if (material == Material.ROCK)
									{
										return true;
									}
									else if (material == Material.IRON)
									{
										return true;
									}
									else
									{
										return material == Material.ANVIL;
									}
								}
								else
								{
									return this.toolMaterial.getHarvestLevel() >= 2;
								}
							}
							else
							{
								return this.toolMaterial.getHarvestLevel() >= 1;
							}
						}
						else
						{
							return this.toolMaterial.getHarvestLevel() >= 1;
						}
					}
					else
					{
						return this.toolMaterial.getHarvestLevel() >= 2;
					}
				}
				else
				{
					return this.toolMaterial.getHarvestLevel() >= 2;
				}
			}
			else
			{
				return this.toolMaterial.getHarvestLevel() >= 2;
			}
		}
	}

	protected float efficiency = 4.0F;
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		if (GetWeaponMode(stack) == SWORD_MODE)
		{
			//Sword
			Material material = state.getMaterial();
			return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? super.getDestroySpeed(stack, state) : this.efficiency;
		}
		else {
			//Pickaxe
			Block block = state.getBlock();

			if (block == Blocks.WEB) {
				return 15.0F;
			} else {
				Material material = state.getMaterial();
				return material != Material.PLANTS && material != Material.VINE && material != Material.CORAL && material != Material.LEAVES && material != Material.GOURD ? 1.0F : 1.5F;
			}
		}
	}

	 /**
	     * Called when a Block is right-clicked with this Item
	     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = player.getHeldItem(hand);
    	
    	if (GetWeaponMode(stack) == SWORD_MODE)
    	{
    		return EnumActionResult.PASS;
    	}
    	
    	BlockPos target = pos.offset(facing);
    	//BlockPos target = pos.up();
    	//DWeapons.LogWarning(String.format("Target = (%s, %s, %s)", target.getX(), target.getY(), target.getZ()) );
    	
    	IBlockState targetBlock = worldIn.getBlockState(target);
    	
    	int entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target, target.add(1, 1, 1))).size();
    	boolean noEntities = entities == 0;

    	if (player.isSneaking()) {
			//removes a block
			IBlockState removeTarget = worldIn.getBlockState(pos);
			if (removeTarget.getBlock() == getBlockToPlace(stack).getBlock())
			{
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				return EnumActionResult.SUCCESS;
			}
		} else {
    		//builds a block
			if ((targetBlock.getBlock() == Blocks.AIR || (targetBlock.getBlock().isReplaceable(worldIn, target)))
					&& noEntities)
			{
				if (!worldIn.isRemote)
				{
					worldIn.setBlockState(target, getBlockToPlace(stack));

					Random rand = new Random();
					if (!IsSky(stack) || rand.nextInt(GetPearlCount(stack) + 1) == 0)
					{
						//stack.setItemDamage(stack.getItemDamage() - 1);
						stack.damageItem(1, player);
						//stack.attemptDamageItem(amount, rand, damager)
					}

					TrueNameReveal(stack, worldIn, player);
				}
				else
				{
//	    		worldIn.playSound(null, player.posX, player.posY, player.posZ,
//	    				SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.6F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
					player.playSound(SoundEvents.BLOCK_GRAVEL_PLACE, 0.6f, 1);
				}
			}

			return EnumActionResult.SUCCESS;
		}
    	
//    	if (IsEarth(stack) ||IsSky(stack)) {
//    		return EnumActionResult.PASS;
//    	}
//
    	return EnumActionResult.SUCCESS;
    }
	
	//--info----------------------------------------------------
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    	if (IsNameHidden(stack)) 
    	{
    		return;
    	}
    	
    	int mode = GetWeaponMode(stack);
    	String modeStr = GetModeNBT(mode);
    	
    	String shared = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_SHARED);
		tooltip.add(shared);
    	
    	if (IsSky(stack)) 
    	{
    		String key = getUnlocalizedName()+DWNBTDef.TOOLTIP_SKY;
			if (mode == SWORD_MODE)
			{
				key = key + "." + SWORD_STR;
			}
    		String skyDesc = I18n.format(key);
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
			String key = getUnlocalizedName()+DWNBTDef.TOOLTIP_EARTH;
			if (mode == SWORD_MODE)
			{
				key = key + "." + SWORD_STR;
			}
			String skyDesc = I18n.format(key);
    		tooltip.add(skyDesc);
    	}else
    	{
    		String earthDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_NORMAL);
    		tooltip.add(earthDesc);
    	}
    	addDamageInformation(stack, worldIn, tooltip, flagIn);
    }
	
	@SideOnly(Side.CLIENT)
    public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		return getActualDamage(stack, 1);
    }
	
	public static final int changeNeedTick = 60;//3 sec
	public static final int changeColorNeedTick = 20;//1 sec
	
	public static final int SWORD_MODE = -1;
	public static final int NORM_MODE = 0;
	
	public static final int MAX_COLOR_MODE = 15;
	
	public static final String SWORD_STR = "sword";
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
	//@SuppressWarnings("unused")
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int time) {
		//change mode
		if (!world.isRemote) {
			int mode = GetWeaponMode(stack);
			if (getMaxItemUseDuration(stack) - time >= changeNeedTick &&
					(IsEarth(stack)||IsSky(stack)))
			{
				//switch between sword and builder
				
				if (mode == SWORD_MODE)
				{
					SetWeaponMode(stack, NORM_MODE);
				}
				else
				{
					SetWeaponMode(stack, SWORD_MODE);
				}
				
				//DWeapons.LogWarning("Weapon mode is:" + GetWeaponMode(stack));
			}else if (getMaxItemUseDuration(stack) - time >= changeColorNeedTick && 
					IsSky(stack))
			{
				//change color
				if (mode != SWORD_MODE) {
					if (mode == MAX_COLOR_MODE){
						SetWeaponMode(stack, NORM_MODE);
					} else {
						SetWeaponMode(stack, mode + 1);
					}
				}
				
				//DWeapons.LogWarning("Weapon mode is:" + GetWeaponMode(stack));
			}
			
		}
		return;
	}
	
	public String GetModeNBT(int mode)
	{
		if (mode == SWORD_MODE)
		{
			return ".sword";
		}
		else
		{
			//bed:
			//super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName();
			
			return "." + EnumDyeColor.byMetadata(mode).getUnlocalizedName();
		}
	}
	
	//Animation
	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		int mode = GetWeaponMode(stack);
		if (mode == SWORD_MODE || IsSky(stack) || IsEarth(stack))
		{
			return EnumAction.BOW;
		}
		return EnumAction.NONE;
		
	}

	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack)
    {
		int mode = GetWeaponMode(stack);
    	String modeStr = GetModeNBT(mode);
		
    	String strMain ="";
    	if (IsNameHidden(stack))
    	{
//    		if (IsManualReady(stack))
//    		{
//    			
//    		}
//    		else
    		{
    			strMain = I18n.format(getUnlocalizedName(stack) + DWNBTDef.TOOLTIP_HIDDEN);
    		}
    		
    	}
    	else
    	{
    		strMain = I18n.format(getUnlocalizedName(stack) + ".name");
    		if (mode == SWORD_MODE)
			{
    			strMain = I18n.format(getUnlocalizedName(stack)  + ".name" + modeStr);

			}else if (IsSky(stack))
			{
				strMain = I18n.format(getUnlocalizedName(stack) + ".name." + EnumDyeColor.byMetadata(mode).getUnlocalizedName());
			}
    		
    	}
    		
    	String strLevel = "";
    	if (IsSky(stack))
    	{
    		strLevel = I18n.format("postfix.is_sky.name");
    	}else if (IsEarth(stack))
    	{
    		strLevel = I18n.format("postfix.is_earth.name");
    	}
    	String strPearl = "";
    	if (GetPearlCount(stack) > 0) {
    		strPearl = I18n.format("postfix.pearl_count.name", GetPearlCount(stack));
    	}
        return I18n.format("item.dweapon_name_format",strMain, strLevel, strPearl);
    }
}
