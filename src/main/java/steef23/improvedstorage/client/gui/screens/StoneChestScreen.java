package steef23.improvedstorage.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.inventory.StoneChestMenu;

@OnlyIn(Dist.CLIENT)
public class StoneChestScreen extends AbstractContainerScreen<StoneChestMenu>
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ImprovedStorage.MOD_ID, "textures/gui/custom_chest.png");
	
	public StoneChestScreen(StoneChestMenu menu, Inventory inv, Component titleIn)
	{
		super(menu, inv, titleIn);
		this.passEvents = false;
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 183;
	}
	
	@Override
	public void render(PoseStack stack, final int mouseX, final int mouseY, final float partialTicks)
	{
		this.renderBackground(stack);
		super.render(stack, mouseX, mouseY, partialTicks);
		this.renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(stack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
}
