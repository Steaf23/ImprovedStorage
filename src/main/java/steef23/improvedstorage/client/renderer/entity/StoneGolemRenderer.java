package steef23.improvedstorage.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.client.model.entity.StoneGolemModel;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class StoneGolemRenderer extends MobRenderer<StoneGolem, StoneGolemModel>
{
	private static final ResourceLocation STONE_GOLEM = new ResourceLocation(ImprovedStorage.MOD_ID, "stone_golem");

	public static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation(STONE_GOLEM, "body");

	protected static final ResourceLocation STONE_GOLEM_LOCATION = new ResourceLocation(ImprovedStorage.MOD_ID, "textures/entity/stone_golem_entity.png");

	public StoneGolemRenderer(EntityRendererProvider.Context context)
	{
		super(context, new StoneGolemModel(context.bakeLayer(MAIN_LAYER)), 1.0f);
	}

	@Override
	public ResourceLocation getTextureLocation(StoneGolem entity) {
		return STONE_GOLEM_LOCATION;
	}

	private static class StoneGolemLayer extends RenderLayer<StoneGolem, StoneGolemModel>
	{
		private final StoneGolemModel model;
		private final float r;

		public StoneGolemLayer(StoneGolemRenderer renderer, StoneGolemModel model, float r)
		{
			super(renderer);
			this.model = model;
			this.r = r;
		}

		@Override
		public void render(PoseStack stack, MultiBufferSource bufferSource, int lightness, StoneGolem entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
		{
			VertexConsumer vertexConsumer = bufferSource.getBuffer(this.getParentModel().renderType(this.getTextureLocation(entity)));
			model.renderToBuffer(stack, vertexConsumer, lightness, OverlayTexture.NO_OVERLAY, r, 1.0f, 1.0f, 1.0f);
		}
	}
}