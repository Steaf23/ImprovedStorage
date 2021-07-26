package steef23.improvedstorage.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.model.entity.StoneGolemModel;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class StoneGolemRenderer extends MobRenderer<StoneGolem, StoneGolemModel<StoneGolem>>
{
	protected static final ResourceLocation STONE_GOLEM_LOCATION = new ResourceLocation(ImprovedStorage.MOD_ID, "textures/entity/stone_golem_entity.png");
	
	public StoneGolemRenderer(EntityRendererProvider.Context renderManagerIn, StoneGolemModel<StoneGolem> model)
	{
		super(renderManagerIn, model, 0.5f);
	}
	
	@Override
	public ResourceLocation getTextureLocation(StoneGolem entity) {
		return STONE_GOLEM_LOCATION;
	}
}