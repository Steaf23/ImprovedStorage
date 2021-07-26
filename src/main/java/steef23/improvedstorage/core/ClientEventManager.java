package steef23.improvedstorage.core;

import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmlclient.registry.RenderingRegistry;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.gui.screens.StoneChestScreen;
import steef23.improvedstorage.client.renderer.blockentity.StoneChestRenderer;
import steef23.improvedstorage.client.renderer.entity.StoneGolemRenderer;
import steef23.improvedstorage.common.world.entity.StoneGolem;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSEntities;
import steef23.improvedstorage.core.init.IMPSMenus;

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
}
