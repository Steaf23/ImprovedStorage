package steef23.improvedstorage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.client.model.block.StoneChestTextureModel;
import steef23.improvedstorage.common.world.level.block.StoneChestBlock;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlocks;

public class StoneChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T>
{
	private final ModelPart chestBase;
	private final ModelPart chestLid;
	private final ModelPart chestHandle;
	
	public StoneChestRenderer(BlockEntityRendererProvider.Context renderContext) 
	{
		ModelPart chest = renderContext.bakeLayer(ModelLayers.CHEST);
		this.chestBase = chest.getChild("bottom");
		this.chestLid = chest.getChild("lid");
		this.chestHandle = chest.getChild("lock");
	}

	@Override
	public void render(T blockEntity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
					   int combinedLightIn, int combinedOverlayIn)
	{
		Level world = blockEntity.getLevel();
		boolean worldExists = world != null;
		BlockState blockState = worldExists ? blockEntity.getBlockState() : IMPSBlocks.STONE_CHEST.get().defaultBlockState().setValue(StoneChestBlock.FACING, Direction.SOUTH);
		Block block = blockState.getBlock();
		
		if (block instanceof StoneChestBlock stoneChestBlock)
		{

			matrixStackIn.pushPose();
			float horizontalAngle = blockState.getValue(StoneChestBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
	        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-horizontalAngle));
	        matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
	        
	        DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> neighborCombineResult;
	        if (worldExists)
	        {
	        	neighborCombineResult = stoneChestBlock.combine(blockState, world, blockEntity.getBlockPos(), true);
	        }
	        else
	        {
	        	neighborCombineResult = DoubleBlockCombiner.Combiner::acceptNone;
	        }

	        float f1 = neighborCombineResult.apply(ChestBlock.opennessCombiner(blockEntity)).get(partialTicks);
	        f1 = 1.0F - f1;
	        f1 = 1.0F - f1 * f1 * f1;

	        Material material = new Material(Sheets.CHEST_SHEET, StoneChestTextureModel.TEXTURE);
	        VertexConsumer iVertexBuilder = material.buffer(bufferIn, RenderType::entityCutout);
	        // TODO: fix lidAngle by fixing callbackwrapper shit
	        float lidAngle = blockEntity.getOpenNess(partialTicks);
	        this.chestLid.xRot = -(f1 * ((float)Math.PI / 2F));
	        this.chestHandle.xRot = this.chestLid.xRot;
	        this.chestBase.render(matrixStackIn, iVertexBuilder, combinedLightIn, combinedOverlayIn);
	        this.chestLid.render(matrixStackIn, iVertexBuilder, combinedLightIn, combinedOverlayIn);
	        this.chestHandle.render(matrixStackIn, iVertexBuilder, combinedLightIn, combinedOverlayIn);
			matrixStackIn.popPose();
		}
	}	
}