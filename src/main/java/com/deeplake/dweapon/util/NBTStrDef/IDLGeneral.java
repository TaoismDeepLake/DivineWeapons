package com.deeplake.dweapon.util.NBTStrDef;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

import static net.minecraft.block.BlockCauldron.LEVEL;

public class IDLGeneral {
    //server side dont have this constructor.
    public static AxisAlignedBB ServerAABB(Vec3d from, Vec3d to)
    {
        return new AxisAlignedBB(from.x, from.y, from.z, to.x, to.y, to.z);
    }

    public static boolean EntityHasBuff(EntityLivingBase livingBase, Potion buff)
    {
        return livingBase.getActivePotionEffect(buff) != null;
    }

    public static int EntityBuffCounter(EntityLivingBase livingBase, Potion buff)
    {
        PotionEffect effect = livingBase.getActivePotionEffect(buff);
        return effect == null ? -1 : effect.getDuration();
    }

    public static void BroadCastByKey(String key) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentTranslation(key));
    }

    public static void SendMsgToPlayer(EntityPlayerMP playerMP, String key)
    {
        playerMP.sendMessage(new TextComponentTranslation(key));
    }

    public static boolean isWaterRelated(IBlockState blockState)
    {
        //todo
        Block block =  blockState.getBlock();
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER )
        {
            return true;
        }

        if (block == Blocks.CAULDRON)
        {
            int waterLv = blockState.getValue(LEVEL);
            return waterLv > 0;
            //playerIn.addStat(StatList.CAULDRON_USED);
        }

        return false;
    }

    public static void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag,
                                      boolean shiftToShowDesc, boolean isShiftPressed , boolean showGuaSocketDesc, String mainDesc) {

        boolean shiftPressed = !shiftToShowDesc || isShiftPressed;
        if (shiftPressed)
        {
            String desc = mainDesc;
            if (!desc.isEmpty())
            {
                tooltip.add(desc);
            }

//            if (showGuaSocketDesc)
//            {
//                int guaTotal = IDLSkillNBT.GetGuaEnhanceTotal(stack);
//                tooltip.add(I18n.format(GUA_TOTAL_SOCKET_DESC, IDLSkillNBT.GetGuaEnhanceTotal(stack)));
//                if (guaTotal > 0)
//                {
//                    tooltip.add(I18n.format("idealland.gua_enhance_list.desc", GetGuaEnhance(stack, 0),
//                            GetGuaEnhanceString(stack, 1),
//                            GetGuaEnhanceString(stack, 2),
//                            GetGuaEnhanceString(stack, 3),
//                            GetGuaEnhanceString(stack, 4),
//                            GetGuaEnhanceString(stack, 5),
//                            GetGuaEnhanceString(stack, 6),
//                            GetGuaEnhanceString(stack, 7)));
//                }
//
//                int freeSockets = IDLSkillNBT.GetGuaEnhanceFree(stack);
//                if (freeSockets > 0)
//                {
//                    tooltip.add(TextFormatting.AQUA + I18n.format(IDLNBTDef.GUA_FREE_SOCKET_DESC, freeSockets));
//                }
//                else {
//                    tooltip.add(TextFormatting.ITALIC + (TextFormatting.WHITE + I18n.format(IDLNBTDef.GUA_NO_FREE_SOCKET_DESC)));
//                }
//            }
        }
        else {
            tooltip.add(TextFormatting.AQUA +  I18n.format("idealland.shared.press_shift"));
        }
    }
}
