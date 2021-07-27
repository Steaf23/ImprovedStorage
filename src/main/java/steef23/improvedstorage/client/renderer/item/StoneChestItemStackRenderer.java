package steef23.improvedstorage.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlocks;

public class StoneChestItemStackRenderer extends BlockEntityWithoutLevelRenderer
{

	public StoneChestItemStackRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet)
	{
		super(renderDispatcher, modelSet);
	}

	public StoneChestItemStackRenderer()
	{
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

  	@Override
  	public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
  	{
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(new StoneChestBlockEntity(BlockPos.ZERO, IMPSBlocks.STONE_CHEST.get().defaultBlockState()), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
  	}
}
