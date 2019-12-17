package com.deeplake.dweapon.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import java.util.Collection;

public class DWEntityUtil {
    public static void TryRemoveDebuff(EntityLivingBase livingBase)
    {
        //washes away debuff
        Collection<PotionEffect> activePotionEffects = livingBase.getActivePotionEffects();
        for (int i = 0; i < activePotionEffects.size(); i++) {
            PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
            if (buff.getPotion().isBadEffect()){
                livingBase.removePotionEffect(buff.getPotion());
            }
            else
            {

            }
        }
    }
}
