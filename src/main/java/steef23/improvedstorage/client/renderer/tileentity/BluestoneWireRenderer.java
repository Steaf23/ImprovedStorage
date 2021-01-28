package steef23.improvedstorage.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity.WireItem;

@OnlyIn(Dist.CLIENT)
public class BluestoneWireRenderer extends TileEntityRenderer<BluestoneWireTileEntity>
{
	public BluestoneWireRenderer(TileEntityRendererDispatcher rendererDispatcherIn) 
	{
		super(rendererDispatcherIn);
	}

	@Override
	public void render(BluestoneWireTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) 
	{
		for (Object wireItem : tileEntityIn.items) 
		{
			ItemStack stack = ((WireItem)wireItem).getItemStack();
			if (!stack.isEmpty()) {
				matrixStackIn.push();
				matrixStackIn.translate(0.5D, 1.5D, 0.5D);
				renderItem(stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
				matrixStackIn.pop();
			}
		}
	}
	
	private void renderItem(ItemStack stack, float PartialTicks, MatrixStack matrixStackIn, 
			IRenderTypeBuffer bufferIn, int combinedLightIn)
	{
		Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
	}
}
