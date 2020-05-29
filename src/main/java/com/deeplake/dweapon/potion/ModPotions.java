package com.deeplake.dweapon.potion;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.item.weapon.DDeathSword;
import com.deeplake.dweapon.item.weapon.DMonkBeads;
import com.deeplake.dweapon.item.weapon.DWeaponSwordBase;
import com.deeplake.dweapon.potion.*;
import com.deeplake.dweapon.util.Reference;
import com.deeplake.dweapon.util.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.deeplake.dweapon.util.NBTStrDef.IDLGeneral.ServerAABB;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModPotions {
    public static Potion DEADLY;
    public static Potion ZEN_HEART;
    public static Potion SPACE_AFF;
    public static Potion SNOW_MED;
    public static Potion SNOW_PROTECT;
    public static Potion SAGE_BOON;
    public static Potion KING_BOON;
    public static Potion DEATH_BOOM;
    public static Potion EROSION;

    @Nullable
    private static Potion getRegisteredMobEffect(String id)
    {
        Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(id));

        if (potion == null)
        {
            throw new IllegalStateException("Invalid MobEffect requested: " + id);
        }
        else
        {
            return potion;
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt)
    {
        DWeapons.Log("registering potion");
        DEADLY = new PotionDeadly(false, 0x333333, "deadly", 0);
        ZEN_HEART = new PotionZenHeart(false, 0xcccc00, "zen_heart", 1);
        SPACE_AFF = new PotionSpaceAffinity(false, 0x0000cc, "space_aff_1", 2);

        SNOW_MED = new PotionSnowMeditation(false, 0xeeeeff, "snow_meditation", 3);
        SNOW_PROTECT = new PotionSnowProtection(false, 0xffffff, "snow_protection", 4);

        SAGE_BOON = new PotionSageBoon(false, 0xFFFF11,"sage_boon", 5);
        KING_BOON = new PotionKingBoon(false, 0xFFFF11,"king_boon", 6);

        DEATH_BOOM = new PotionDeathBoom(true, 0xff6666, "death_boom", 7);

        EROSION = new PotionErosion(true, 0x22ff33, "erosion", 8);

        evt.getRegistry().register(DEADLY);
        evt.getRegistry().register(ZEN_HEART);
        evt.getRegistry().register(SPACE_AFF);
        evt.getRegistry().register(SNOW_MED);
        evt.getRegistry().register(SNOW_PROTECT);

        evt.getRegistry().register(KING_BOON);
        evt.getRegistry().register(SAGE_BOON);

        evt.getRegistry().register(DEATH_BOOM);

        //REGISTRY.register(1, new ResourceLocation("speed"), (new Potion(false, 8171462))
        // .setPotionName("effect.moveSpeed")
        // .setIconIndex(0, 0)
        // .registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2).setBeneficial());
    }

    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent evt) {
        World world = evt.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = evt.getEntityLiving();

        //Zen heart
        Entity trueSource = evt.getSource().getTrueSource();
        if (trueSource instanceof EntityLivingBase){
            EntityLivingBase sourceCreature = (EntityLivingBase)trueSource;
            if (sourceCreature.isEntityUndead())
            {
                PotionEffect curBuff = hurtOne.getActivePotionEffect(ZEN_HEART);
                if (curBuff != null) {
                    if (!world.isRemote) {
                        evt.setCanceled(true);
                    } else {
                        //TODO:create some particle effect
                    }
                }
            }

            //Apply damage multiplier
            Collection<PotionEffect> activePotionEffectsAttacker = sourceCreature.getActivePotionEffects();
            for (int i = 0; i < activePotionEffectsAttacker.size(); i++) {
                PotionEffect buff = (PotionEffect)activePotionEffectsAttacker.toArray()[i];
                if (buff.getPotion() instanceof BasePotion)
                {
                    BasePotion modBuff = (BasePotion)buff.getPotion();
                    if (!world.isRemote)
                    {
                        evt.setAmount((1 + modBuff.getAttackMultiplier(buff.getAmplifier())) * evt.getAmount());
                    }
                }
            }
        }

        if (evt.isCanceled())
        {
            return;
        }

        //Base Damage Reduction
        Collection<PotionEffect> activePotionEffects = hurtOne.getActivePotionEffects();
        for (int i = 0; i < activePotionEffects.size(); i++) {
            PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
            if (buff.getPotion() instanceof BasePotion)
            {
                BasePotion modBuff = (BasePotion)buff.getPotion();
                if (!world.isRemote)
                {
                    float reduceRatio = modBuff.getDamageReductionMultiplier(buff.getAmplifier());
                    evt.setAmount((1 - reduceRatio) * evt.getAmount());
                }
            }
        }

        //onHit effect
        for (int i = 0; i < activePotionEffects.size(); i++) {
            PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
            if (buff.getPotion() instanceof BasePotion)
            {
                BasePotion modBuff = (BasePotion)buff.getPotion();
                if (world.isRemote)
                {
                    modBuff.playOnHitEffect(hurtOne, evt.getAmount());
                }
            }
        }

    }

//  * At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.<br>
//  * Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.<br>
    @SubscribeEvent
    public static void onCreatureDamaged(LivingDamageEvent evt) {
        World world = evt.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = evt.getEntityLiving();
        if (!world.isRemote) {
//            Entity trueSource = evt.getSource().getTrueSource();
//            if (trueSource instanceof EntityLivingBase){
//                EntityLivingBase sourceCreature = (EntityLivingBase)trueSource;
//                if (sourceCreature.isEntityUndead())
//                {
//                    PotionEffect curBuff = hurtOne.getActivePotionEffect(ZEN_HEART);
//                    if (curBuff != null) {
//                        evt.setCanceled(true);
//                    }
//                }
//            }
        } else {
            //TODO:create some particle effect
        }
    }

    @SubscribeEvent
    public static void onCreatureKB(LivingKnockBackEvent evt) {
        World world = evt.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = evt.getEntityLiving();
        if (!world.isRemote) {
            //Handle Space aff
            if (hurtOne.getActivePotionEffect(SPACE_AFF) != null || hurtOne.getActivePotionEffect(SNOW_MED) != null){
                evt.setCanceled(true);
                return;
            }

            //Handle virtue and undead
            Entity trueSource = evt.getOriginalAttacker();
            if (trueSource instanceof EntityLivingBase){
                EntityLivingBase sourceCreature = (EntityLivingBase)trueSource;
                if (sourceCreature.isEntityUndead())
                {
                    PotionEffect curBuff = hurtOne.getActivePotionEffect(ZEN_HEART);
                    if (curBuff != null) {
                        PotionEffect sourceBuff = sourceCreature.getActivePotionEffect(ZEN_HEART);
                        if (sourceBuff == null) {//prevent dead loop
                            sourceCreature.knockBack(hurtOne, evt.getStrength(), -evt.getRatioX(), -evt.getRatioZ());
                        }
                        evt.setCanceled(true);
                    }
                }
            }

            //KB Reduction
            if (evt.isCanceled())
            {
                return;
            }

            Collection<PotionEffect> activePotionEffects = hurtOne.getActivePotionEffects();
            for (int i = 0; i < activePotionEffects.size(); i++) {
                PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
                if (buff.getPotion() instanceof BasePotion)
                {
                    BasePotion modBuff = (BasePotion)buff.getPotion();
                    if (!world.isRemote)
                    {
                        float reduceRatio = modBuff.getKBResistanceMultiplier(buff.getAmplifier());
                        evt.setStrength((1 - reduceRatio) * evt.getStrength());
                    }
                }
            }
        } else {
            //TODO:create some particle effect
        }
    }

//    @SubscribeEvent
//    public static void onCreatureDie(LivingDeathEvent event) {
//        World world = event.getEntity().getEntityWorld();
//        EntityLivingBase dieOne = event.getEntityLiving();
//        Vec3d pos = event.getEntity().getPositionEyes(0);
//
//        if (!world.isRemote) {
//            if (dieOne.getActivePotionEffect(DEATH_BOOM) != null)
//            {
//                world.createExplosion(dieOne, dieOne.posX, dieOne.posY, dieOne.posZ, 4,true);
//            }
//        } else {
//            //currently do nothing
//        }
//    }
}
