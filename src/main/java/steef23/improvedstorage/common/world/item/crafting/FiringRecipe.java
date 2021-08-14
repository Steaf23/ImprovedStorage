package steef23.improvedstorage.common.world.item.crafting;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSRecipes;

public class FiringRecipe extends AbstractCookingRecipe
{
    public FiringRecipe(ResourceLocation resourceLocation, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime)
    {
        super(ImprovedStorage.FIRING_RECIPE_TYPE, resourceLocation, group, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(IMPSBlocks.KILN.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return IMPSRecipes.FIRING.get();
    }
}
