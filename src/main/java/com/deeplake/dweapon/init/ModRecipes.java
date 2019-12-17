package com.deeplake.dweapon.init;

import com.deeplake.dweapon.recipe.special.EarthEnhance;
import com.deeplake.dweapon.recipe.special.MarkHate;
import com.deeplake.dweapon.recipe.special.NameRevealByBook;
import com.deeplake.dweapon.recipe.special.PearlSocket;
import com.deeplake.dweapon.recipe.special.SkyEnhance;
import com.deeplake.dweapon.recipe.special.StarterManual;
import com.deeplake.dweapon.util.Reference;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModRecipes {
	
	
	public static void Init() {
		//Only smelting recipes
		//only need to add here
		GameRegistry.addSmelting(ModItems.PURE_INGOT,
				new ItemStack(ModItems.WEAPON_PEARL),  
				0.1f);
	
		GameRegistry.addSmelting(ModItems.WEAPON_PEARL,
				new ItemStack(ModItems.PURE_INGOT),
				0.1f);
		
		GameRegistry.addSmelting(ModBlocks.DIVINE_ORE,
				new ItemStack(ModItems.DIVINE_INGOT),  
				6f);

		GameRegistry.addSmelting(ModBlocks.PURE_ORE,
				new ItemStack(ModItems.PURE_INGOT),
				3f);
		
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
		IForgeRegistry<IRecipe> r = evt.getRegistry();
		r.register(new PearlSocket().setRegistryName(new ResourceLocation(Reference.MOD_ID, "sword_socket_pearl")));
		
		//IForgeRegistry<IRecipe> r_eh = evt.getRegistry();
		//r.register(new EarthEnhance().setRegistryName(new ResourceLocation(Reference.MOD_ID, "sword_earth_enhance")));
		
		//IForgeRegistry<IRecipe> r_sky = evt.getRegistry();
		//r.register(new SkyEnhance().setRegistryName(new ResourceLocation(Reference.MOD_ID, "sword_sky_enhance")));
		
		//IForgeRegistry<IRecipe> r_book = evt.getRegistry();
		r.register(new NameRevealByBook().setRegistryName(new ResourceLocation(Reference.MOD_ID, "sword_name_reveal_by_book")));
		
		//IForgeRegistry<IRecipe> r_starter_book = evt.getRegistry();
		r.register(new StarterManual().setRegistryName(new ResourceLocation(Reference.MOD_ID, "starter_manual_recipe")));
		
		//IForgeRegistry<IRecipe> r_hate = evt.getRegistry();
		r.register(new MarkHate().setRegistryName(new ResourceLocation(Reference.MOD_ID, "sword_mark_hate")));
	}
}
