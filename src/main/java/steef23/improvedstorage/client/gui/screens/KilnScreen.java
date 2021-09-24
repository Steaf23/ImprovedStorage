package steef23.improvedstorage.client.gui.screens;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import steef23.improvedstorage.common.world.inventory.KilnMenu;

public class KilnScreen extends AbstractFurnaceScreen<KilnMenu>
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

    public KilnScreen(KilnMenu menu, Inventory inventory, Component title)
    {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title, BACKGROUND_TEXTURE);
    }
}
