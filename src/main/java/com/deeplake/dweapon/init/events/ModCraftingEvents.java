package com.deeplake.dweapon.init.events;

import com.deeplake.dweapon.init.ModItems;
import com.deeplake.dweapon.item.weapon.IDWeaponEnhanceable;
import com.deeplake.dweapon.util.Reference;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModCraftingEvents {
//    @Cancelable
//    public class AnvilUpdateEvent extends Event
//    {
//        @Nonnull
//        private final ItemStack left;  // The left side of the input
//        @Nonnull
//        private final ItemStack right; // The right side of the input
//        private final String name;     // The name to set the item, if the user specified one.
//        @Nonnull
//        private ItemStack output;      // Set this to set the output stack
//        private int cost;              // The base cost, set this to change it if output != null
//        private int materialCost; // The number of items from the right slot to be consumed during the repair. Leave as 0 to consume the entire stack.
//
//        public AnvilUpdateEvent(@Nonnull ItemStack left, @Nonnull ItemStack right, String name, int cost)
//        {
//            this.left = left;
//            this.right = right;
//            this.output = ItemStack.EMPTY;
//            this.name = name;
//            this.setCost(cost);
//            this.setMaterialCost(0);
//        }
//
//        @Nonnull
//        public ItemStack getLeft() { return left; }
//        @Nonnull
//        public ItemStack getRight() { return right; }
//        public String getName() { return name; }
//        @Nonnull
//        public ItemStack getOutput() { return output; }
//        public void setOutput(@Nonnull ItemStack output) { this.output = output; }
//        public int getCost() { return cost; }
//        public void setCost(int cost) { this.cost = cost; }
//        public int getMaterialCost() { return materialCost; }
//        public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
//    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        checkLevelEnhance(event);
    }
    private static String earthName = ModItems.EARTH_CHARM.getUnlocalizedName();
    private static String skyName = ModItems.SKY_CHARM.getUnlocalizedName();
    public static void checkLevelEnhance(AnvilUpdateEvent event)
    {
        //DWeapons.LogWarning(String.format("Name is null? = %s", event.getName().isEmpty()));
        if (event.getLeft() != ItemStack.EMPTY && event.getRight() != ItemStack.EMPTY ) {
            ItemStack left = event.getLeft();

            if (left.getItem() instanceof IDWeaponEnhanceable){
                IDWeaponEnhanceable leftType = (IDWeaponEnhanceable) left.getItem();
                if (leftType.IsSky(left)){
                    //No modify can be done to sky weapons
                    //They can only be fixed
                    return;
                } else if (leftType.IsEarth(left)) {
                    if (event.getRight().getItem().getUnlocalizedName().equals(skyName)){
                        ItemStack swordResult = left.copy();
                        leftType.SetSky(swordResult);

                        event.setMaterialCost(1);
                        event.setCost(30);
                        event.setOutput(swordResult);
                    }
                } else {//man-made-level
                    if (event.getRight().getItem().getUnlocalizedName().equals(earthName)){
                        ItemStack swordResult = left.copy();
                        leftType.SetEarth(swordResult);

                        event.setMaterialCost(1);
                        event.setCost(8);
                        event.setOutput(swordResult);
                    }
                }
            }
        }
//        if(!stack.isEmpty()) {
//            if(stack.getItem() instanceof DWeaponSwordBase)
//            {
//                if (foundSword) {
//                    return false;//only one sword at a time
//                }
//                DWeaponSwordBase sword = (DWeaponSwordBase)stack.getItem();
//                foundSword = true;
//
//                if (sword.IsSky(stack) || sword.IsEarth(stack)) {
//                    //cannot set earth again
//                    return false;
//                }
//            }
//            else if (stack.getItem().getUnlocalizedName(stack).equals(pearlName))
//            {//found a pearl
//                if (foundEarth) {
//                    return false;//only one sword at a time
//                }
//                foundEarth = true;
//            }
//            else
//            {
//                return false; //Found an upgrader or other.
//            }
//        }
    }
}
