package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;

public class ECNUBrewingRecipe {

    public static void init(IEventBus modBus) {
        register();
    }

    public static void register() {
        addPotionRecipe(Items.POTION, Potions.AWKWARD, ECNUItems.MOSQUITO_MOUTH.get(), Items.POTION, ECNUPotions.ITCHING.get());

        addPotionUpgradeRecipes(Items.POTION, ECNUPotions.ITCHING.get(), ECNUPotions.STRONG_ITCHING.get(), ECNUPotions.LONGER_ITCHING.get());
        addPotionUpgradeRecipes(Items.SPLASH_POTION, ECNUPotions.ITCHING.get(), ECNUPotions.STRONG_ITCHING.get(), ECNUPotions.LONGER_ITCHING.get());
        addPotionUpgradeRecipes(Items.LINGERING_POTION, ECNUPotions.ITCHING.get(), ECNUPotions.STRONG_ITCHING.get(), ECNUPotions.LONGER_ITCHING.get());

        addPotionRecipe(Items.POTION, ECNUPotions.ITCHING.get(), Items.GUNPOWDER, Items.SPLASH_POTION, ECNUPotions.ITCHING.get());
        addPotionRecipe(Items.POTION, ECNUPotions.STRONG_ITCHING.get(), Items.GUNPOWDER, Items.SPLASH_POTION, ECNUPotions.STRONG_ITCHING.get());
        addPotionRecipe(Items.POTION, ECNUPotions.LONGER_ITCHING.get(), Items.GUNPOWDER, Items.SPLASH_POTION, ECNUPotions.LONGER_ITCHING.get());

        addPotionRecipe(Items.SPLASH_POTION, ECNUPotions.ITCHING.get(), Items.DRAGON_BREATH, Items.LINGERING_POTION, ECNUPotions.ITCHING.get());
        addPotionRecipe(Items.SPLASH_POTION, ECNUPotions.STRONG_ITCHING.get(), Items.DRAGON_BREATH, Items.LINGERING_POTION, ECNUPotions.STRONG_ITCHING.get());
        addPotionRecipe(Items.SPLASH_POTION, ECNUPotions.LONGER_ITCHING.get(), Items.DRAGON_BREATH, Items.LINGERING_POTION, ECNUPotions.LONGER_ITCHING.get());
    }

    private static void addPotionUpgradeRecipes(Item bottleItem, Potion inputPotion, Potion strongPotion, Potion longerPotion) {
        addPotionRecipe(bottleItem, inputPotion, Items.GLOWSTONE_DUST, bottleItem, strongPotion);
        addPotionRecipe(bottleItem, inputPotion, Items.REDSTONE, bottleItem, longerPotion);
    }

    private static void addPotionRecipe(Item inputItem, Potion inputPotion, Item ingredient, Item outputItem, Potion outputPotion) {
        BrewingRecipeRegistry.addRecipe(new net.minecraftforge.common.brewing.IBrewingRecipe() {
            @Override
            public boolean isInput(net.minecraft.world.item.ItemStack input) {
                return input.getItem() == inputItem && PotionUtils.getPotion(input) == inputPotion;
            }

            @Override
            public boolean isIngredient(net.minecraft.world.item.ItemStack ingredientStack) {
                return ingredientStack.getItem() == ingredient;
            }

            @Override
            public net.minecraft.world.item.ItemStack getOutput(net.minecraft.world.item.ItemStack input, net.minecraft.world.item.ItemStack ingredientStack) {
                if (!isInput(input) || !isIngredient(ingredientStack)) {
                    return net.minecraft.world.item.ItemStack.EMPTY;
                }

                return PotionUtils.setPotion(new net.minecraft.world.item.ItemStack(outputItem), outputPotion);
            }
        });
    }
}
