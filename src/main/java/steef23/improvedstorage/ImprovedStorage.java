package steef23.improvedstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSContainers;
import steef23.improvedstorage.core.init.IMPSEntities;
import steef23.improvedstorage.core.init.IMPSItems;
import steef23.improvedstorage.core.init.IMPSTileEntities;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("improvedstorage")
public class ImprovedStorage
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "improvedstorage";
    public static final ItemGroup IMPS_ITEMGROUP = new IMPSItemGroup("impstab");

    public ImprovedStorage() 
    {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		IMPSItems.ITEMS.register(bus);
    	IMPSBlocks.BLOCKS.register(bus);
    	IMPSTileEntities.TILE_ENTITY_TYPES.register(bus);
    	IMPSEntities.ENTITY_TYPES.register(bus);
    	IMPSContainers.CONTAINER_TYPES.register(bus);
    	
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static class IMPSItemGroup extends ItemGroup
    {	
    	private IMPSItemGroup(String label)
    	{
    		super(label);
    	}
    	
    	@Override
    	public ItemStack createIcon() 
    	{
    		return new ItemStack(Blocks.CHEST);
    	}
    }
}
