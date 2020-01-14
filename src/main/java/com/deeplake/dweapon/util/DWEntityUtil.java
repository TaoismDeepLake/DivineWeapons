package com.deeplake.dweapon.util;

import com.deeplake.dweapon.init.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
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

    public static void TryRemoveGivenBuff(EntityLivingBase livingBase, Potion potion)
    {
        //washes away debuff
        Collection<PotionEffect> activePotionEffects = livingBase.getActivePotionEffects();
        for (int i = 0; i < activePotionEffects.size(); i++) {
            PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
            if (buff.getPotion() == potion){
                livingBase.removePotionEffect(buff.getPotion());
                return;
            }
            else
            {

            }
        }
    }
}
