package com.deeplake.dweapon.init;

import com.deeplake.dweapon.DWeapons;
import com.deeplake.dweapon.potion.BasePotion;
import com.deeplake.dweapon.util.Reference;
import net.minecraft.init.Bootstrap;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModPotions {
    public static Potion DEADLY;

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
        DWeapons.LogWarning("registering potion");
        DEADLY = new BasePotion(true, 0x333333, "deadly", 0);
        evt.getRegistry().register(DEADLY);
        //REGISTRY.register(1, new ResourceLocation("speed"), (new Potion(false, 8171462))
        // .setPotionName("effect.moveSpeed")
        // .setIconIndex(0, 0)
        // .registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2).setBeneficial());
    }
}
