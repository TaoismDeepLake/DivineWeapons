package com.deeplake.dweapon.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PotionErosion extends BasePotion {
    public PotionErosion(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn, name, icon);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase living, int amplified) {
        if (living.getHealth() < living.getMaxHealth())
        {
            ItemStack stack = living.getHeldItemMainhand();

            DamageItemInSlot(EntityEquipmentSlot.CHEST, living, amplified);
            DamageItemInSlot(EntityEquipmentSlot.FEET, living, amplified);
            DamageItemInSlot(EntityEquipmentSlot.HEAD, living, amplified);
            DamageItemInSlot(EntityEquipmentSlot.LEGS, living, amplified);
            DamageItemInSlot(EntityEquipmentSlot.MAINHAND, living, amplified);
            DamageItemInSlot(EntityEquipmentSlot.OFFHAND, living, amplified);
        }
    }

    public void DamageItemInSlot(EntityEquipmentSlot slot, EntityLivingBase livingBase, int amount)
    {
        ItemStack stack = livingBase.getItemStackFromSlot(slot);
        stack.damageItem(1, livingBase);
    }

}
