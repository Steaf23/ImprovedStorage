package steef23.improvedstorage;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import steef23.improvedstorage.common.world.item.crafting.FiringRecipe;
import steef23.improvedstorage.core.init.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("improvedstorage")
public class ImprovedStorage
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "improvedstorage";
    public static final IMPSCreativeModeTab IMPS_CREATIVE_MODE_TAB = new IMPSCreativeModeTab("impstab");
    public static final RecipeType<FiringRecipe> FIRING_RECIPE_TYPE = RecipeType.register(MOD_ID + ":firing");

    public ImprovedStorage()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        IMPSItems.ITEMS.register(bus);
        IMPSBlocks.BLOCKS.register(bus);
        IMPSBlockEntities.BLOCK_ENTITY_TYPES.register(bus);
        IMPSMenus.MENU_TYPES.register(bus);
        IMPSEntities.ENTITY_TYPES.register(bus);
        IMPSRecipes.RECIPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class IMPSCreativeModeTab extends CreativeModeTab
    {
        private IMPSCreativeModeTab(String label)
        {
            super(label);
        }

        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(IMPSBlocks.BLUESTONE_TABLE.get());
        }
    }
}
