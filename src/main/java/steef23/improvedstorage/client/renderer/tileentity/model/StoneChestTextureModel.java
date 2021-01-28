package steef23.improvedstorage.client.renderer.tileentity.model;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import steef23.improvedstorage.ImprovedStorage;

@Mod.EventBusSubscriber(modid=ImprovedStorage.MOD_ID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
public class StoneChestTextureModel 
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImprovedStorage.MOD_ID, "entity/stone_chest");
	
	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event)
	{
		if (!event.getMap().getTextureLocation().equals(Atlases.CHEST_ATLAS))
		{
			return;
		}
		
		event.addSprite(TEXTURE);
	}
}