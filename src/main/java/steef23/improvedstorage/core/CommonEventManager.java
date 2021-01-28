package steef23.improvedstorage.core;

import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.entity.StoneGolemEntity;
import steef23.improvedstorage.core.init.IMPSEntities;

@Mod.EventBusSubscriber(modid = ImprovedStorage.MOD_ID, bus = Bus.MOD)
public class CommonEventManager
{
	//setup events
	
	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> {
			GlobalEntityTypeAttributes.put(IMPSEntities.STONE_GOLEM_ENTITY.get(), StoneGolemEntity.registerAttributes().create());
		});
	}
}
