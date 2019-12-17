package com.deeplake.dweapon.item.weapon;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.init.ModCreativeTab;
import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.util.DWNBT;
import com.deeplake.dweapon.util.IHasModel;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTDef;
import com.deeplake.dweapon.util.NBTStrDef.DWNBTUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DWeaponSwordBase extends ItemSword implements IHasModel, IDWeaponEnhanceable{



	public DWeaponSwordBase(String name, ToolMaterial material)
	{
		super(material);

		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.DW_WEAPON);
		
		this.addPropertyOverride(new ResourceLocation(DWNBTDef.WEAPON_MODE), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return (float)GetWeaponMode(stack);//note this is a int in the NBT
            }
        });
		
		ModItems.ITEMS.add(this);
	}

	public static boolean RefundPearlsAtSky = true;

	public boolean IsSky(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_SKY);
	}
	
	public void SetSky(ItemStack stack)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_SKY, true);
	}
	
	public static boolean IsNameHidden(ItemStack stack)
	{
		return DWNBTUtil.GetBooleanDF(stack, DWNBTDef.IS_NAME_HIDDEN, true);
	}
	
	public static void SetNameHidden(ItemStack stack, boolean isHidden)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_NAME_HIDDEN, isHidden);
	}
	
	public boolean IsEarth(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_EARTH);
	}
	
	public void SetEarth(ItemStack stack)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_EARTH, true);
	}
	
	public int GetPearlCount(ItemStack stack)
	{
		return DWNBTUtil.GetInt(stack, DWNBTDef.PEARL_COUNT);
	}
	
	public void SetPearlCount(ItemStack stack, int count)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return;
		}
		
		if (count <= GetPearlMax(stack)) {
			DWNBTUtil.SetInt(stack, DWNBTDef.PEARL_COUNT, count);
		}
	}

	public void AddPearlCount(ItemStack stack, int count)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return;
		}

		int anticipatedCount = count + GetPearlCount(stack);
		if (anticipatedCount <= GetPearlMax(stack)) {
			DWNBTUtil.SetInt(stack, DWNBTDef.PEARL_COUNT, anticipatedCount);
		}
		else {
			DWNBTUtil.SetInt(stack, DWNBTDef.PEARL_COUNT, GetPearlMax(stack));
		}
	}

	public int maxPearlCount = 5;
	public int GetPearlMax(ItemStack stack)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return 0;
		}
		return maxPearlCount;//Most Weapons can socket 5 pearls
	}
	
	public int GetPearlEmptySpace(ItemStack stack)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return 0;
		}
		if (IsSky(stack)) {
			return 0;//sky weapon needs no pearls to function.
		}
		
		return GetPearlMax(stack) - GetPearlCount(stack);
	}
	
	public static boolean IsManualReady(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_MANUAL_READY);
	}
	
	public static void SetManualReady(ItemStack stack, boolean isReady)
	{
		DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_MANUAL_READY, isReady);
	}
	
	public static int GetWeaponMode(ItemStack stack)
	{
		return DWNBTUtil.GetInt(stack, DWNBTDef.WEAPON_MODE);
	}
	
	public static void SetWeaponMode(ItemStack stack, int mode)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return;
		}
		
		DWNBTUtil.SetInt(stack, DWNBTDef.WEAPON_MODE, mode);
	}
	
	public static boolean IsHeirloom(ItemStack stack)
	{
		return DWNBTUtil.GetBoolean(stack, DWNBTDef.IS_HEIRLOOM);
	}
	
	public static boolean SetHeirloom(ItemStack stack, boolean val)
	{
		return DWNBTUtil.SetBoolean(stack, DWNBTDef.IS_HEIRLOOM, val);
	}
	
	public static String GetOwner(ItemStack stack)
	{
		return DWNBTUtil.GetString(stack, DWNBTDef.OWNER, "");
	}
	
	public static void SetOwner(ItemStack stack, String owner)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return;
		}
		
		DWNBTUtil.SetString(stack, DWNBTDef.OWNER, owner);
	}
	
	public static void ForceHideVanillaAttr(ItemStack stack)
	{
		if (!(stack.getItem() instanceof IDWeaponEnhanceable)) {
			return;
		}
		//See ItemStack.getToolTip:
		//32 - addInformation
		//1 - enchantment
		//2 - attributes
		//4 - unbreakable
		//8 - can destroy
		//16- can place on
		//...
		// if (!multimap.isEmpty() && (i1 & 2) == 0)
		//...
		
		int curFlags = DWNBTUtil.GetInt(stack, "HideFlags");
		if ((curFlags & 2) == 0) {
			DWNBTUtil.SetInt(stack,"HideFlags", curFlags | 2);
		}
	}
	//---------------------------------------------------------
	
	
	/* Only Server side */
	public boolean AttackDelegate(final ItemStack stack, final EntityPlayer player, final Entity target, float ratio)
	{
		return false;
	}
	
	@Override
	public boolean onLeftClickEntity(final ItemStack stack, final EntityPlayer player, final Entity target) {
		//if (player.world.isRemote)
			//return true;
		
		//if (player.getCooledAttackStrength(0.0f) > 0.8f) {
			return AttackDelegate(stack, player, target, player.getCooledAttackStrength(0.0f));
		//}

		//return true;//ignore attack
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
		//Particle;
		super.onUsingTick(stack, living, count);
		//DWeapons.LogWarning(String.format("base onUsingTick %s",count));
		
		if (living.world.isRemote)
		{
			clientUseTick(stack, living, count);
		}
		else
		{
			serverUseTick(stack, living, count);
		}
	}
	
	public void clientUseTick(ItemStack stack, EntityLivingBase living, int count)
	{
		
	}
	
	public void serverUseTick(ItemStack stack, EntityLivingBase living, int count)
	{
		
	}
	//----------------------------------------------------------------
	public ItemStack CreateManual() {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		
		// https://minecraftjson.com/
		
		// /give @p written_book{pages:["[\"\",{\"text\":\"123 45\"}]","[\"\",{\"text\":\"678 90\"}]"],title:CustomBook,author:Player}
		
		NBTTagList bookPages = new NBTTagList();
		String name = getUnlocalizedName();
		
		String pageCountString = I18n.format(name + DWNBTDef.MANUAL_PAGE_COUNT);
		
		int pageCount = 0;
		boolean hasManual = false;
		try {
			pageCount = Integer.parseInt(pageCountString);
			hasManual = true;
		} catch (NumberFormatException e) {
			pageCount = 0;
		}
		
		if (hasManual)
		{
			int i = 1;
			for (i = 1; i <= pageCount; i++)
			{
				bookPages.appendTag(DWNBT.bookPageFromUnlocalizedLine(name + DWNBTDef.MANUAL_PAGE_KEY + i));
			}
			book.setTagInfo("author", new NBTTagString(I18n.format(name + DWNBTDef.MANUAL_AUTHOR)));
			book.setTagInfo("title", new NBTTagString(I18n.format(name + DWNBTDef.MANUAL_TITLE)));
		}
		else
		{
			bookPages.appendTag(DWNBT.bookPageFromUnlocalizedLine("item.shared.missing_manual_content"));
			book.setTagInfo("author", new NBTTagString(I18n.format("item.shared.missing_manual_author")));
			book.setTagInfo("title", new NBTTagString(I18n.format("item.shared.missing_manual_title")));
		}
		
		
        book.setTagInfo("pages", bookPages);
		
		
		//DWeapons.LogWarning("[FFFFF: Book NBT]" + book.getTagCompound().toString());
		
		return book;
	}
	
	//----------------------------------------------------------------
	@Override
	public void registerModels() 
	{
		DWeapons.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	//-----------------Any Item
	@Nonnull
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
	EntityItem entity = new EntityItem(world, location.posX, location.posY, location.posZ, itemstack);
		if(location instanceof EntityItem) {
		// workaround for private access on that field >_>
		  NBTTagCompound tag = new NBTTagCompound();
		  location.writeToNBT(tag);
		  entity.setPickupDelay(tag.getShort("PickupDelay"));
		}
		entity.motionX = location.motionX;
		entity.motionY = location.motionY;
		entity.motionZ = location.motionZ;
		return entity;
		//EnumRarity s = null;
	}
	
	@Nonnull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
	    if (IsSky(stack)) 
	    {
	    	return EnumRarity.EPIC;
	    }
	    else if (IsEarth(stack))
	    {
	    	return EnumRarity.RARE;
	    }
	    else
	    {
	    	return EnumRarity.UNCOMMON;
	    }
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
	    return false;
	}
	  
	  /**
	     * Called when a player drops the item into the world,
	     * returning false from this will prevent the item from
	     * being removed from the players inventory and spawning
	     * in the world
	     *
	     * @param player The player that dropped the item
	     * @param item The item stack, before the item is removed.
	     */
	@Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        return false;
    }
  
    public int getItemEnchantability()
    {
        return 0;
    }
	
    //TestCode
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
        //disable this after real play
        
		
		return true;
    }
    
//    public String getUnlocalizedName(ItemStack stack)
//    {
//        //return "item." + this.unlocalizedName;
//    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
    	if (entityIn instanceof EntityPlayer && !worldIn.isRemote)
    	{
    		if (IsManualReady(stack))
    		{
    			//gives the manual and erase the "give-manual" state
    			EntityPlayer player = (EntityPlayer) entityIn;
    			TrueNameReveal(stack, worldIn, player);
    		}
    		if (RefundPearlsAtSky && IsSky(stack) && GetPearlCount(stack) > 0 )
    		{
    			//gives back pearls as sky weapons ignore pearls
    			int pCount = GetPearlCount(stack);
    			ItemStack pearls = new ItemStack(ModItems.WEAPON_PEARL);
    			pearls.setCount(pCount);
    			((EntityPlayer)entityIn).addItemStackToInventory(pearls);
    			SetPearlCount(stack, 0);
    		}
    	}
    }
    
    public void TrueNameReveal(ItemStack stack, World worldIn, EntityPlayer player)
    {
    	//If you call this when its true name already revealed, it will just give the book. no experience.
    	if (!worldIn.isRemote) {
    		
    		if (IsNameHidden(stack))
    		{
    			SetNameHidden(stack, false);
    			player.addExperience(100);
    		}
    		
    		if (IsManualReady(stack))
    		{
    			player.addItemStackToInventory(CreateManual());
    			SetManualReady(stack, false);
    		}
    		//achievement TODO		
    	}
    }
    
    public void TrueNameRevealByUsing(ItemStack stack, World worldIn, EntityPlayer player)
    {
    	//Some weapons will self-reveal along using. This won't give duplicate books.
    	if (!worldIn.isRemote) {
    		
    		if (IsNameHidden(stack))
    		{
    			TrueNameReveal(stack, worldIn, player);
    		}	
    	}
    }
    
    public String getItemStackDisplayName(ItemStack stack)
    {
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
	  //----------------------------------------------------
	  /* NBT loading */

	  
	  /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
    	super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    String translateToLocalFormatted(String key, Object... format)
    {
    	return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, format);
    }
    	
    //translateToLocal
    String translateToLocal(String key)
    {
    	return net.minecraft.util.text.translation.I18n.translateToLocal(key);
    }
    
    
    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	//Trys to force override the ItemStack.getTooltip failed,
        //because the damage attr are added after getTooltip.
    	//This is the way I found to hide the damage descrption.
    	ForceHideVanillaAttr(stack);
    	
    	//An attempt to force override the name in ItemStack.getTooltip
    	//Abandoned because not much point
//    	List<String> list = tooltip;
//    	list.clear();
//        String s = stack.getDisplayName();
//
//        if (stack.hasDisplayName())
//        {
//            s = TextFormatting.ITALIC + s;
//        }
//
//        s = s + TextFormatting.RESET;
//
//        if (flagIn.isAdvanced())
//        {
//            String s1 = "";
//
//            if (!s.isEmpty())
//            {
//                s = s + " (";
//                s1 = ")";
//            }
//
//            int i = Item.getIdFromItem(stack.getItem());
//
//            if (this.getHasSubtypes())
//            {
//                s = s + String.format("#%04d/%d%s", i, stack.getItemDamage(), s1);
//            }
//            else
//            {
//                s = s + String.format("#%04d%s", i, s1);
//            }
//        }
//        else if (!stack.hasDisplayName() && stack.getItem() == Items.FILLED_MAP)
//        {
//            s = s + " #" + stack.getItemDamage();
//        }
//
//        list.add(s);
//        int i1 = 0;
//
//        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99))
//        {
//            i1 = stack.getTagCompound().getInteger("HideFlags");
//        }
        //cannot override this because playerIn is not passed here
//        if ((i1 & 32) == 0)//also, overriding this will cause stack overflow. This is where getToolTip will be called
//        {
//        	stack.getItem().addInformation(this, playerIn == null ? null : playerIn.world, list, advanced);
//        }
      //-----------------------
    	//Custom
    	
    	if (IsHeirloom(stack))
    	{
    		String ownerName = GetOwner(stack);
    		String ownerDesc = String.format(I18n.format("item.shared.heirloom_desc", ownerName)) ;
    		tooltip.add(ownerDesc);
    	}
    	
    	if (IsNameHidden(stack)) 
    	{
    		//strMain = I18n.format("");
    		if (IsManualReady(stack))
    		{
    			String pearlDesc = I18n.format("item.shared.true_name_reveal");
	    		tooltip.add(pearlDesc);
    		}
    		else
    		{
	    		String pearlDesc = I18n.format("item.shared.hidden_desc");
	    		tooltip.add(pearlDesc);
    		}
    		return;
    	}
    	
    	if (IsManualReady(stack))
		{
			String pearlDesc = I18n.format("item.shared.print_manual");
    		tooltip.add(pearlDesc);
		}
    	
    	//tooltip.clear();
    	if (GetPearlCount(stack) > 0)
    	{
    		String pearlDesc = I18n.format("item.shared.pearl_desc", GetPearlCount(stack));
    		tooltip.add(pearlDesc);
    	}
    	
    	if (IsSky(stack)) 
    	{
    		String skyDesc = I18n.format("item.shared.sky_desc");
    		tooltip.add(skyDesc);
    	}else if (IsEarth(stack))
    	{
    		String earthDesc = I18n.format("item.shared.earth_desc");
    		tooltip.add(earthDesc);
    	}

    }

    public boolean isNeedDamageDesc = true;
    @SideOnly(Side.CLIENT)
    public float GetReferenceDamage(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	return 7.0f;
    }
    @SideOnly(Side.CLIENT)
    public void addDamageInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	if (isNeedDamageDesc) {
	    	String dmgDesc = I18n.format(getUnlocalizedName()+DWNBTDef.TOOLTIP_DAMAGE, GetReferenceDamage(stack, worldIn, tooltip, flagIn));
			tooltip.add(dmgDesc);
    	}
    }
    
    

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *  
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return IsSky(stack) || IsEarth(stack);
    }
    
    @Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
    	
    	boolean isEnchantedBook = repairMaterial.getItem() == Items.ENCHANTED_BOOK;
		boolean isDivineIngot = OreDictionary.itemMatches(repairMaterial, new ItemStack(ModItems.PURE_INGOT), false);
		//boolean base = super.getIsRepairable(stack, repairMaterial);
    	
		return !isEnchantedBook && isDivineIngot;
	}
    
    ///Weather related
    public float GetTemperatureHere(EntityPlayer player)
	{
		BlockPos pos = player.getPosition();
		World world = player.getEntityWorld();
		Biome biome = world.getBiomeForCoordsBody(pos);
		return biome.getTemperature(pos);
	}
	
	public boolean CanSnowHere(EntityPlayer player)
	{
		return (GetTemperatureHere(player) < 0.15f);
	}
	
	public boolean CanRainHere(EntityPlayer player)
	{
		return (GetTemperatureHere(player) > 0.15f) && (GetTemperatureHere(player) < 0.95f);
	}
	
	public boolean IsSnowingHere(EntityPlayer player)
	{
		WorldInfo worldInfo = player.world.getWorldInfo();
		boolean raining = worldInfo.isRaining();
		return CanSnowHere(player) && raining;
	}
	
	public boolean IsRainingHere(EntityPlayer player)
	{
		WorldInfo worldInfo = player.world.getWorldInfo();
		boolean raining = worldInfo.isRaining();
		return !CanSnowHere(player) && raining;
	}
}
