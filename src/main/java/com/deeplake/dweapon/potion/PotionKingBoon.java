package com.deeplake.dweapon.potion;

public class PotionKingBoon extends BasePotion {

    public PotionKingBoon(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);

        //damageReductionRatioBase = 0.2f;
        //damageReductionRatioPerLevel = 0.2f;

        attackIncreaseRatioBase = 0.2f;
        attackIncreaseRatioPerLevel = 0.8f;

        //hpRecoverPerTick = 1/60f;
        //hpRecoverPerLevel = 1/5f - 1/60f;

        knockbackResistanceRatio = 0.5f;
        knockbackResistanceRatioPerLevel = 0.4f;
    }

}
