package com.deeplake.dweapon.util.NBTStrDef;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
}
