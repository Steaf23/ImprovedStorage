package steef23.improvedstorage.core;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.entity.StoneGolem;
import steef23.improvedstorage.core.init.IMPSEntities;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD)
public class CommonEventManager
{
	//setup events

	@SubscribeEvent
	public static void createEntityAttributes(EntityAttributeCreationEvent event)
	{
		event.put(IMPSEntities.STONE_GOLEM.get(), StoneGolem.createAttributes().build());
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event)
	{

	}
}
