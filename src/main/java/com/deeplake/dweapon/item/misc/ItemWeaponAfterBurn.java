package com.deeplake.dweapon.item.misc;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.ItemBase;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.deeplake.dweapon.util.NBTStrDef.IDLGeneral.isWaterRelated;
import static net.minecraft.block.BlockCauldron.LEVEL;

public class ItemWeaponAfterBurn extends ItemBase {

    public ItemWeaponAfterBurn(String name) {
        super(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        EntityPlayer player = (EntityPlayer)entityIn;
        if (isSelected && worldIn.isRemote)
        {
            float angle = 6.28f * (player.getRNG().nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.FLAME, entityIn.posX + Math.cos(angle), entityIn.posY, entityIn.posZ + Math.sin(angle),
                    0,0,0);


        }else {
            if (isSelected && player.getRNG().nextFloat() < 0.05f)
            {
                player.setFire(1);
            }
        }
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState targetBlock = worldIn.getBlockState(pos);

        RayTraceResult raytraceresult = this.rayTrace(worldIn, player, true);
        BlockPos blockpos = raytraceresult.getBlockPos();
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Material material = iblockstate.getMaterial();

        if (material == Material.WATER && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
            player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
            ItemStack resultStack = new ItemStack(ModItems.SEALED_WEAPON, stack.getCount());
            player.setHeldItem(hand, resultStack);
            return EnumActionResult.SUCCESS;
        }

        if (isWaterRelated(targetBlock))
        {
            player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
            ItemStack resultStack = new ItemStack(ModItems.SEALED_WEAPON, stack.getCount());
            player.setHeldItem(hand, resultStack);
            return EnumActionResult.SUCCESS;
        }
        else {
            return EnumActionResult.PASS;
        }
    }
}
