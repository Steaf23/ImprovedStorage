package steef23.improvedstorage.core;

import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.RenderShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.http.conn.routing.RouteInfo;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.gui.screens.StoneChestScreen;
import steef23.improvedstorage.client.gui.screens.StoneGolemScreen;
import steef23.improvedstorage.client.model.entity.StoneGolemModel;
import steef23.improvedstorage.client.renderer.blockentity.StoneChestRenderer;
import steef23.improvedstorage.client.renderer.entity.StoneGolemRenderer;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSEntities;
import steef23.improvedstorage.core.init.IMPSMenus;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventManager
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{
		//register screens
		MenuScreens.register(IMPSMenus.STONE_CHEST.get(), StoneChestScreen::new);
		MenuScreens.register(IMPSMenus.STONE_GOLEM.get(), StoneGolemScreen::new);

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
