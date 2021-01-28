package steef23.improvedstorage.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.client.renderer.tileentity.model.StoneChestTextureModel;
import steef23.improvedstorage.common.block.StoneChestBlock;
import steef23.improvedstorage.common.tileentity.StoneChestTileEntity;
import steef23.improvedstorage.core.init.IMPSBlocks;

@OnlyIn(Dist.CLIENT)
public class StoneChestRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T>
{
	private final ModelRenderer chestBase;
	private final ModelRenderer chestLid;
	private final ModelRenderer chestHandle;
	
	public StoneChestRenderer(TileEntityRendererDispatcher rendererDispatcherIn) 
	{
		super(rendererDispatcherIn);
		
		this.chestBase = new ModelRenderer(64, 64, 0, 19);
		this.chestBase.addBox(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f, 0.0f);
		this.chestLid = new ModelRenderer(64, 64, 0, 0);
		this.chestLid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.chestLid.rotationPointY = 9.0F;
		this.chestLid.rotationPointZ = 1.0F;
		this.chestHandle = new ModelRenderer(64, 64, 0, 0);
		this.chestHandle.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.chestHandle.rotationPointY = 8.0F;
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combinedOverlayIn)
	{
		World world = tileEntityIn.getWorld();
		boolean worldExists = world != null;
		BlockState blockState = worldExists ? tileEntityIn.getBlockState() : IMPSBlocks.STONE_CHEST.get().getDefaultState().with(StoneChestBlock.FACING, Direction.SOUTH);
		Block block = blockState.getBlock();
		
		if (block instanceof StoneChestBlock)
		{
			StoneChestBlock stoneChestBlock = (StoneChestBlock)block;
			
			matrixStackIn.push();
			float horizontalAngle = blockState.get(StoneChestBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
	        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-horizontalAngle));
	        matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
	        
	        TileEntityMerger.ICallbackWrapper<? extends StoneChestTileEntity> iCallbackWrapper;
	        if (worldExists)
	        {
	        	iCallbackWrapper = stoneChestBlock.getWrapper(blockState, world, tileEntityIn.getPos(), true);
	        }
	        else
	        {
	        	iCallbackWrapper = TileEntityMerger.ICallback::func_225537_b_;
	        }
	        
	        float f1 = iCallbackWrapper.apply(StoneChestBlock.getLid((IChestLid)tileEntityIn)).get(partialTicks);
	        f1 = 1.0F - f1;
	        f1 = 1.0F - f1 * f1 * f1;
	        int i = iCallbackWrapper.apply(new DualBrightnessCallback<>()).applyAsInt(combinedLightIn);
	    
	        RenderMaterial material = new RenderMaterial(Atlases.CHEST_ATLAS, StoneChestTextureModel.TEXTURE);
	        IVertexBuilder iVertexBuilder = material.getBuffer(bufferIn, RenderType::getEntityCutout);
	        
	        this.chestLid.rotateAngleX = -(f1 * ((float)Math.PI / 2F));
	        this.chestHandle.rotateAngleX = this.chestLid.rotateAngleX;
	        this.chestBase.render(matrixStackIn, iVertexBuilder, i, combinedOverlayIn);
	        this.chestLid.render(matrixStackIn, iVertexBuilder, i, combinedOverlayIn);
	        this.chestHandle.render(matrixStackIn, iVertexBuilder, i, combinedOverlayIn);
			matrixStackIn.pop();
		}
	}	
}