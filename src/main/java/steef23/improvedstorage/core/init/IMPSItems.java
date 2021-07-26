package steef23.improvedstorage.core.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;

import java.util.Objects;

public class IMPSItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<Item> BLUESTONE_BLEND = ITEMS.register("bluestone_blend", () ->
		new ItemNameBlockItem(IMPSBlocks.BLUESTONE_WIRE.get(), new ItemNameBlockItem.Properties().tab(ImprovedStorage.IMPS_CREATIVE_MODE_TAB)));
	
	public static final RegistryObject<Item> BLUESTONE_INGOT = ITEMS.register("bluestone_ingot", () ->
		new Item(new Item.Properties().tab(ImprovedStorage.IMPS_CREATIVE_MODE_TAB)));
	
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
	    			final Item.Properties properties = new Item.Properties().tab(ImprovedStorage.IMPS_CREATIVE_MODE_TAB);
	    			final BlockItem blockItem = new BlockItem(block, properties);
	    			blockItem.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
	    			registry.register(blockItem);
	    		});
	    	
	    	final Block block = IMPSBlocks.STONE_CHEST.get();
	    	final Item.Properties stoneChestProp = new Item.Properties()
	    			.tab(ImprovedStorage.IMPS_CREATIVE_MODE_TAB);
//	    			.setISTER(() -> () -> new StoneChestItemStackRenderer<BlockEntity>(() -> new StoneChestBlockEntity()));
	    	final BlockItem stoneChest = new BlockItem(IMPSBlocks.STONE_CHEST.get(), stoneChestProp);
	    	stoneChest.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
	    	registry.register(stoneChest);
	    	
	    	ImprovedStorage.LOGGER.debug("Registered BlockItems!");
		}
	}
}
