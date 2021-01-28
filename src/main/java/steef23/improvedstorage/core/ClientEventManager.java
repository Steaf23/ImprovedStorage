package steef23.improvedstorage.core;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.gui.StoneChestScreen;
import steef23.improvedstorage.client.gui.StoneGolemScreen;
import steef23.improvedstorage.client.renderer.entity.StoneGolemRenderer;
import steef23.improvedstorage.client.renderer.tileentity.BluestoneWireRenderer;
import steef23.improvedstorage.client.renderer.tileentity.StoneChestRenderer;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSContainers;
import steef23.improvedstorage.core.init.IMPSEntities;
import steef23.improvedstorage.core.init.IMPSTileEntities;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventManager
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{
		//register screens
		ScreenManager.registerFactory(IMPSContainers.STONE_CHEST.get(), StoneChestScreen::new);
		ScreenManager.registerFactory(IMPSContainers.GOLEM_CONTAINER.get(), StoneGolemScreen::new);
		
		//register entities
		RenderingRegistry.registerEntityRenderingHandler(IMPSEntities.STONE_GOLEM_ENTITY.get(), StoneGolemRenderer::new);
		
		//set rendertypes
		RenderTypeLookup.setRenderLayer(IMPSBlocks.BLUESTONE_WIRE.get(), RenderType.getCutout());
		
		//bind TERs
		ClientRegistry.bindTileEntityRenderer(IMPSTileEntities.STONE_CHEST.get(), StoneChestRenderer::new);
		ClientRegistry.bindTileEntityRenderer(IMPSTileEntities.BLUESTONE_WIRE.get(), BluestoneWireRenderer::new);
	}
}
