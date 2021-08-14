package steef23.improvedstorage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.common.world.level.block.BluestoneWireBlock;
import steef23.improvedstorage.common.world.level.block.entity.AbstractItemPipeBlockEntity;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneWireBlockEntity;

public class BluestoneWireRenderer<T extends BluestoneWireBlockEntity> implements BlockEntityRenderer<T>
{
	public BluestoneWireRenderer(BlockEntityRendererProvider.Context renderContext)
	{

	}

	@Override
	public void render(BluestoneWireBlockEntity wireTE, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		matrixStackIn.pushPose();

		if (wireTE.getRenderDebug())
		{
			renderDebugOverlay(wireTE, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		}

		for (AbstractItemPipeBlockEntity.PipeItem item : wireTE.items)
		{
			if (item.isValid())
			{
				renderPipeItem(item, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

				this.renderTargetOverlay(item, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			}
		}

		matrixStackIn.popPose();
	}
	
	private void renderPipeItem(AbstractItemPipeBlockEntity.PipeItem item, float partialTicks, PoseStack matrixStackIn,
								MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		Vector3d sourcePos = new Vector3d(item.getSource().getStepX() / 2.0 + 0.5,
				  						  item.getSource().getStepY(),
				  						  item.getSource().getStepZ() / 2.0 + 0.5);
		Vector3d targetPos = new Vector3d(item.getTarget().getStepX() / 2.0 + 0.5,
										  item.getTarget().getStepY(),
										  item.getTarget().getStepZ() / 2.0 + 0.5);
		Vector3d middlePos = new Vector3d(0.5D, 0.0D, 0.5D);
		
		matrixStackIn.pushPose();
		
		double speed = BluestoneWireBlockEntity.SPEED;
		int ticks = item.getTicksInPipe();
		Vector3d lerpPos;
		if (ticks < speed / 2)
		{
			lerpPos = new Vector3d(Mth.lerp((ticks + partialTicks) / (speed / 2), sourcePos.x, middlePos.x),
								   0.2D, 
								   Mth.lerp((ticks + partialTicks) / (speed / 2), sourcePos.z, middlePos.z));
		}
		else
		{
			double currentPosition = (ticks + partialTicks - (speed / 2)) / (speed / 2);

			lerpPos = new Vector3d(Mth.lerp(currentPosition, middlePos.x, targetPos.x),
					   			   0.2D, 
					   			   Mth.lerp(currentPosition, middlePos.z, targetPos.z));
		}
		matrixStackIn.translate(lerpPos.x, lerpPos.y, lerpPos.z);
		
		matrixStackIn.scale(0.7f, 0.7f, 0.7f);
		Minecraft.getInstance().getItemRenderer().renderStatic(item.getItemStack(), ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
		matrixStackIn.popPose();
	}
	
	private void renderDebugOverlay(BluestoneWireBlockEntity wireTE, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		BluestoneWireBlock block = (BluestoneWireBlock)wireTE.getBlockState().getBlock();
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5D, 0.0D, 0.5D);
		for (Direction d: Direction.values())
		{
			ItemStack stack;
			switch (BluestoneWireBlock.getSideValue(d, wireTE.getBlockState()))
			{
				case SIDE, UP -> stack = new ItemStack(Items.GREEN_CONCRETE);
				case NONE -> stack = new ItemStack(Items.BARRIER);
				case END -> stack = new ItemStack(Items.BEDROCK);
				default -> stack = ItemStack.EMPTY;
			}

			this.renderFacedOverlay(d, stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		}
		matrixStackIn.popPose();
	}
	
	private void renderTargetOverlay(AbstractItemPipeBlockEntity.PipeItem item, float PartialTicks, PoseStack matrixStackIn,
									 MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5D, 0.0D, 0.5D);
		this.renderFacedOverlay(item.getSource(), new ItemStack(Items.ORANGE_CONCRETE), PartialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		this.renderFacedOverlay(item.getTarget(), new ItemStack(Items.LIME_CONCRETE), PartialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
	}
	
	private void renderFacedOverlay(Direction face, ItemStack stack, float PartialTicks, PoseStack matrixStackIn,
									MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		matrixStackIn.pushPose();
		matrixStackIn.translate(face.getStepX() / 2.0, face.getStepY(), face.getStepZ() / 2.0);
		matrixStackIn.scale(.5f, .5f, .5f);
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
		matrixStackIn.popPose();
	}
}
