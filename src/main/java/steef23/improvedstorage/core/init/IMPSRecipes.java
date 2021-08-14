package steef23.improvedstorage.core.init;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.item.crafting.FiringRecipe;
import steef23.improvedstorage.common.world.item.crafting.FiringSerializer;

import javax.annotation.Nullable;
import java.rmi.registry.Registry;

public class IMPSRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImprovedStorage.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> FIRING = RECIPES.register("firing", FiringSerializer::new);
}
