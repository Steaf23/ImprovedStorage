package steef23.improvedstorage.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import steef23.improvedstorage.ImprovedStorage;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventManager
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{

	}
}
