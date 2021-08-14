package steef23.improvedstorage.common.world.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.core.init.IMPSMenus;

public class KilnMenu extends AbstractFurnaceMenu
{
    public KilnMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf byteBuf)
    {
        super(IMPSMenus.KILN.get(), ImprovedStorage.FIRING_RECIPE_TYPE, RecipeBookType.FURNACE, windowId, playerInventory);
    }

    public KilnMenu(final int windowId, final Inventory playerInventory, final Container container, final ContainerData containerData)
    {
        super(IMPSMenus.KILN.get(), ImprovedStorage.FIRING_RECIPE_TYPE, RecipeBookType.FURNACE, windowId, playerInventory, container, containerData);
    }
}
