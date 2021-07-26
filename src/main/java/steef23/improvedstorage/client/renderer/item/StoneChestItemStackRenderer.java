package steef23.improvedstorage.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class StoneChestItemStackRenderer<T extends BlockEntity> extends BlockEntityWithoutLevelRenderer
{

//	private final Supplier<T> tileEntity;

	public StoneChestItemStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_)
	{
		super(p_172550_, p_172551_);
	}

//  	public StoneChestItemStackRenderer(Supplier<T> te)
//  	{
//		super();
//		this.tileEntity = te;
//  	}

//  	@Override
//  	public void renderItem(ItemStack itemStackIn, IF transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
//  	{
//  		super.renderItem(this.tileEntity.get(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
//  	}
}
