package steef23.improvedstorage.core;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.model.entity.StoneGolemModel;
import steef23.improvedstorage.client.renderer.blockentity.StoneChestRenderer;
import steef23.improvedstorage.client.renderer.entity.StoneGolemRenderer;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSEntities;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventManager
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{
		//register screens

		//set rendertypes
//		RenderLayer.setRenderLayer(IMPSBlocks.BLUESTONE_WIRE.get(), RenderType.cutout());

		// bind TERs
		BlockEntityRenderers.register(IMPSBlockEntities.STONE_CHEST.get(), StoneChestRenderer::new);

	}

	@SubscribeEvent
	public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(IMPSEntities.STONE_GOLEM.get(), StoneGolemRenderer::new);
	}

	@SubscribeEvent
	public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(StoneGolemRenderer.MAIN_LAYER, StoneGolemModel::createBodyLayer);
	}

}
