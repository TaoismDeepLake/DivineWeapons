package com.deeplake.dweapon.potion;

import com.deeplake.dweapon.DWeapons;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class PotionSnowMeditation extends BasePotion {

    public static final float hpRecoverPerTick = 1/60f;

    public PotionSnowMeditation(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);
        damageReductionRatioBase = 0.2f;
        damageReductionRatioPerLevel = 0.1f;

        attackIncreaseRatioBase = -0.99f;
        attackIncreaseRatioPerLevel = 0.0f;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase living, int amplified) {
        //DWeapons.LogWarning("Heal: " + (amplified + 1) * hpRecoverPerTick);
        if (living.getHealth() < living.getMaxHealth())
        {
            living.heal((amplified + 1) * hpRecoverPerTick);
        }
    }
}
