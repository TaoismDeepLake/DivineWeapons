package com.deeplake.dweapon.potion;

import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class PotionSageBoon extends BasePotion {

    public PotionSageBoon(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);

        damageReductionRatioBase = 0.2f;
        damageReductionRatioPerLevel = 0.2f;

        //attackIncreaseRatioBase = 0.5f;
        //attackIncreaseRatioPerLevel = 1.0f;

        hpRecoverPerTick = 1/60f;
        hpRecoverPerLevel = 1/5f - 1/60f;
    }

}
