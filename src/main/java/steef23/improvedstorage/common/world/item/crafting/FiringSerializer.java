package steef23.improvedstorage.common.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class FiringSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FiringRecipe>
{

    @Override
    public FiringRecipe fromJson(ResourceLocation location, JsonObject object)
    {
        String s = GsonHelper.getAsString(object, "group", "");
        JsonElement jsonelement = GsonHelper.isArrayNode(object, "ingredient") ? GsonHelper.getAsJsonArray(object, "ingredient") : GsonHelper.getAsJsonObject(object, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonelement);
        //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
        if (!object.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack;
        if (object.get("result").isJsonObject()) itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(object, "result"));
        else {
            String s1 = GsonHelper.getAsString(object, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
        }
        float f = GsonHelper.getAsFloat(object, "experience", 0.0F);
        int i = GsonHelper.getAsInt(object, "cookingtime", 100);
        return new FiringRecipe(location, s, ingredient, itemstack, f, i);
    }

    @Nullable
    @Override
    public FiringRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf byteBuf)
    {
        String s = byteBuf.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(byteBuf);
        ItemStack itemstack = byteBuf.readItem();
        float f = byteBuf.readFloat();
        int i = byteBuf.readVarInt();
        return new FiringRecipe(location, s, ingredient, itemstack, f, i);
    }

    @Override
    public void toNetwork(FriendlyByteBuf byteBuf, FiringRecipe recipe)
    {
        byteBuf.writeUtf(recipe.getGroup());
        recipe.getIngredients().get(0).toNetwork(byteBuf);
        byteBuf.writeItem(recipe.getResultItem());
        byteBuf.writeFloat(recipe.getExperience());
        byteBuf.writeVarInt(recipe.getCookingTime());
    }
}
