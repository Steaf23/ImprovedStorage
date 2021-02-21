package steef23.improvedstorage.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.common.tileentity.AbstractItemPipeTileEntity.PipeItem;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity;

@OnlyIn(Dist.CLIENT)
public class BluestoneWireRenderer extends TileEntityRenderer<BluestoneWireTileEntity>
{
	public BluestoneWireRenderer(TileEntityRendererDispatcher rendererDispatcherIn) 
	{
		super(rendererDispatcherIn);
	}

	@Override
	public void render(BluestoneWireTileEntity wireTE, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) 
	{
		matrixStackIn.push();
		
		if (wireTE.getRenderDebug())
		{
			renderDebugOverlay(wireTE, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
		}
		
		for (PipeItem item : wireTE.items)
		{
			if (item.isValid())
			{
				renderPipeItem(item, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
				
				this.renderTargetOverlay(item, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
			}
		}
		
		matrixStackIn.pop();
	}
	
	private void renderPipeItem(PipeItem item, float partialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		Vector3d sourcePos = new Vector3d(item.getSource().toVector3f().getX() / 2 + 0.5, 
				  						  item.getSource().toVector3f().getY(), 
				  						  item.getSource().toVector3f().getZ() / 2 + 0.5);
		Vector3d targetPos = new Vector3d(item.getTarget().toVector3f().getX() / 2 + 0.5, 
										  item.getTarget().toVector3f().getY(), 
										  item.getTarget().toVector3f().getZ() / 2 + 0.5);
		Vector3d middlePos = new Vector3d(0.5D, 0.0D, 0.5D);
		
		matrixStackIn.push();
		
		double speed = (double)BluestoneWireTileEntity.SPEED;
		int ticks = item.getTicksInPipe();
		Vector3d lerpPos = Vector3d.ZERO;
		if (ticks < speed / 2)
		{
			lerpPos = new Vector3d(MathHelper.lerp((ticks + partialTicks) / (speed / 2), sourcePos.x, middlePos.x), 
								   0.2D, 
								   MathHelper.lerp((ticks + partialTicks) / (speed / 2), sourcePos.z, middlePos.z));
		}
		else
		{
			lerpPos = new Vector3d(MathHelper.lerp((ticks + partialTicks - (speed / 2)) / (speed / 2), middlePos.x, targetPos.x), 
					   			   0.2D, 
					   			   MathHelper.lerp((ticks + partialTicks - (speed / 2)) / (speed / 2), middlePos.z, targetPos.z));
		}
		matrixStackIn.translate(lerpPos.x, lerpPos.y, lerpPos.z);
		
		matrixStackIn.scale(0.7f, 0.7f, 0.7f);
		Minecraft.getInstance().getItemRenderer().renderItem(item.getItemStack(), TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.pop();
	}
	
	private void renderDebugOverlay(BluestoneWireTileEntity wireTE, float partialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		matrixStackIn.push();
		matrixStackIn.translate(0.5D, 0.0D, 0.5D);
		for (Direction d: Direction.values())
		{
			ItemStack stack; 
			if (wireTE.isSideConnected(d))
			{
				stack = new ItemStack(Items.GREEN_CONCRETE);
			}
			else
			{
				stack = new ItemStack(Items.RED_CONCRETE);
			}
			this.renderFacedOverlay(d, stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
		}
		matrixStackIn.pop();
	}
	
	private void renderTargetOverlay(PipeItem item, float PartialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		matrixStackIn.push();
		matrixStackIn.translate(0.5D, 0.0D, 0.5D);
		this.renderFacedOverlay(item.getSource(), new ItemStack(Items.ORANGE_CONCRETE), PartialTicks, matrixStackIn, bufferIn, combinedLightIn);
		this.renderFacedOverlay(item.getTarget(), new ItemStack(Items.LIME_CONCRETE), PartialTicks, matrixStackIn, bufferIn, combinedLightIn);
		matrixStackIn.pop();
	}
	
	private void renderFacedOverlay(Direction face, ItemStack stack, float PartialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		matrixStackIn.push();
		matrixStackIn.translate(face.toVector3f().getX() / 2, face.toVector3f().getY(), face.toVector3f().getZ() / 2);
		matrixStackIn.scale(.5f, .5f, .5f);
		Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.pop();
	}
}
