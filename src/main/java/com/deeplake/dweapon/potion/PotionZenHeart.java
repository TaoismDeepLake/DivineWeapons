package com.deeplake.dweapon.potion;

import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class PotionZenHeart extends BasePotion {


    public PotionZenHeart(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase living, int amplified) {
        //do nothing
    }
}
