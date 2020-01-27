package com.deeplake.dweapon.potion;

import com.deeplake.dweapon.DWeapons;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class PotionSnowMeditation extends BasePotion {

    public PotionSnowMeditation(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);
        damageReductionRatioBase = 0.2f;
        damageReductionRatioPerLevel = 0.1f;

        attackIncreaseRatioBase = -0.99f;
        attackIncreaseRatioPerLevel = 0.0f;

        hpRecoverPerTick = 1/60f;
        hpRecoverPerLevel = 1/60f;
    }

}
