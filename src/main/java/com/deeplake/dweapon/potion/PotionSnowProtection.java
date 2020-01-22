package com.deeplake.dweapon.potion;

import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class PotionSnowProtection extends BasePotion {

    public static final float hpRecoverPerTick = 1/60f;

    public PotionSnowProtection(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);
        damageReductionRatioBase = 0.5f;
        damageReductionRatioPerLevel = 0.1f;

        //attackIncreaseRatioBase = 0.5f;
        //attackIncreaseRatioPerLevel = 1.0f;
    }



    @Override
    public void performEffect(@Nonnull EntityLivingBase living, int amplified) {
        if (living.getHealth() < living.getMaxHealth())
        {
            living.heal((amplified + 1) * hpRecoverPerTick);
        }
    }
}
