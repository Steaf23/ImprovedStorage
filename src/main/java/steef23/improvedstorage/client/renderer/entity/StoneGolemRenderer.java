package steef23.improvedstorage.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.renderer.entity.model.StoneGolemModel;
import steef23.improvedstorage.common.entity.StoneGolemEntity;

public class StoneGolemRenderer extends MobRenderer<StoneGolemEntity, StoneGolemModel<StoneGolemEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(ImprovedStorage.MOD_ID, "textures/entity/stone_golem_entity.png");
	
	public StoneGolemRenderer(EntityRendererManager renderManagerIn)
	{
		super(renderManagerIn, new StoneGolemModel<StoneGolemEntity>(), 0.5f);
	}
	
	@Override
	public ResourceLocation getEntityTexture(StoneGolemEntity entity) {
		return TEXTURE;
	}
}