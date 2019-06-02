package com.deeplake.dweapon.init;


import com.deeplake.dweapon.DWeapons;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ModAchivements {

	  private static final String ADVANCEMENT_STORY_ROOT = "minecraft:story/root";
	  private static final String ADVANCEMENT_STONE_PICK = "minecraft:story/upgrade_tools";
	  private static final String ADVANCEMENT_IRON_PICK = "minecraft:story/iron_tools";
	  public static final String ADVANCEMENT_SHOOT_ARROW = "minecraft:adventure/shoot_arrow";

	  @SubscribeEvent
	  public void onCraft(PlayerEvent.ItemCraftedEvent event) {
	    if(event.player == null ||
	       event.player instanceof FakePlayer  ||
	       !(event.player instanceof EntityPlayerMP) ||
	       event.crafting.isEmpty()) {
	      return;
	    }
	    
	    DWeapons.LogWarning("EVENT: onCraft");
	    
	    EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
	    Item item = event.crafting.getItem();
	    if(item instanceof ItemBlock && ((ItemBlock) item).getBlock() == Blocks.CRAFTING_TABLE) {
	      grantAdvancement(playerMP, ADVANCEMENT_STORY_ROOT);
	    }
	    // fire vanilla pickaxe crafting when crafting tinkers picks (hammers also count for completeness sake)
	    
	  }

	  @SubscribeEvent
	  public void onDamageEntity(LivingHurtEvent event) {
	    DamageSource source = event.getSource();
	    if(source.isProjectile()
	       && !(source.getTrueSource() instanceof FakePlayer)
	       && source.getTrueSource() instanceof EntityPlayerMP
	       && source.getImmediateSource() instanceof EntityArrow) {
	        grantAdvancement((EntityPlayerMP) source.getTrueSource(), ADVANCEMENT_SHOOT_ARROW);
	    }
	  }

	  private void grantAdvancement(EntityPlayerMP playerMP, String advancementResource) {
	    Advancement advancement = playerMP.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(advancementResource));
	    if(advancement != null) {
	      AdvancementProgress advancementProgress = playerMP.getAdvancements().getProgress(advancement);
	      if(!advancementProgress.isDone()) {
	        // we use playerAdvancements.grantCriterion instead of progress.grantCriterion for the visibility stuff and toasts
	        advancementProgress.getRemaningCriteria().forEach(criterion -> playerMP.getAdvancements().grantCriterion(advancement, criterion));
	      }
	    }
	  }

}
