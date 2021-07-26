package steef23.improvedstorage.client.model.block;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
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
		if (!event.getMap().location().equals(Sheets.CHEST_SHEET))
		{
			return;
		}
		
		event.addSprite(TEXTURE);
	}
}