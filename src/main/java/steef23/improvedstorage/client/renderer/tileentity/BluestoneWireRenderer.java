package steef23.improvedstorage.client.renderer.tileentity;

import java.util.ArrayList;

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
		
		ArrayList<PipeItem> items = wireTE.items;
		for (int i = 0; i < items.size(); i++) 
		{
			ItemStack stack = items.get(i).getItemStack();
			if (!stack.isEmpty()) 
			{

				Vector3d vec = new Vector3d((double)i / items.size(), 0.5D, (double)i / items.size());
				matrixStackIn.translate(vec.getX(), vec.getY(), vec.getZ());
				renderItem(stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn);

			}
		}				
		matrixStackIn.pop();
	}
	
	private void renderItem(ItemStack stack, float PartialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
	}
	
	private void renderDebugOverlay(BluestoneWireTileEntity wireTE, float PartialTicks, MatrixStack matrixStackIn, 
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
			matrixStackIn.push();
			matrixStackIn.translate(d.toVector3f().getX() / 2, d.toVector3f().getY(), d.toVector3f().getZ() / 2);
			matrixStackIn.scale(.5f, .5f, .5f);
			this.renderItem(stack, PartialTicks, matrixStackIn, bufferIn, combinedLightIn);
			matrixStackIn.pop();
		}
		matrixStackIn.pop();
	}
}
