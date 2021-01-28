package steef23.improvedstorage.core.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.renderer.item.StoneChestItemStackRenderer;
import steef23.improvedstorage.common.tileentity.StoneChestTileEntity;

public class IMPSItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<Item> BLUESTONE_BLEND = ITEMS.register("bluestone_blend", () -> 
		new BlockNamedItem(IMPSBlocks.BLUESTONE_WIRE.get(), new BlockNamedItem.Properties().group(ImprovedStorage.IMPS_ITEMGROUP)));
	
	public static final RegistryObject<Item> BLUESTONE_INGOT = ITEMS.register("bluestone_ingot", () ->
		new Item(new Item.Properties().group(ImprovedStorage.IMPS_ITEMGROUP)));
	
	//BlockItems
	@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD)
	public static class BlockItemRegistry
	{
		@SubscribeEvent
		public static void onRegisterItems(final RegistryEvent.Register<Item> event)
		{
			final IForgeRegistry<Item> registry = event.getRegistry();
	    	
	    	IMPSBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> 
	    		block.getRegistryName() != IMPSBlocks.BLUESTONE_WIRE.get().getRegistryName()
	    		&& block.getRegistryName() != IMPSBlocks.STONE_CHEST.get().getRegistryName()
	    		).forEach(block -> {
	    			final Item.Properties properties = new Item.Properties().group(ImprovedStorage.IMPS_ITEMGROUP);
	    			final BlockItem blockItem = new BlockItem(block, properties);
	    			blockItem.setRegistryName(block.getRegistryName());
	    			registry.register(blockItem);
	    		});
	    	
	    	final Block block = IMPSBlocks.STONE_CHEST.get();
	    	final Item.Properties stoneChestProp = new Item.Properties()
	    			.group(ImprovedStorage.IMPS_ITEMGROUP)
	    			.setISTER(() -> () -> new StoneChestItemStackRenderer<TileEntity>(() -> new StoneChestTileEntity()));
	    	final BlockItem stoneChest = new BlockItem(IMPSBlocks.STONE_CHEST.get(), stoneChestProp);
	    	stoneChest.setRegistryName(block.getRegistryName());
	    	registry.register(stoneChest);
	    	
	    	ImprovedStorage.LOGGER.debug("Registered BlockItems!");
		}
	}
}
