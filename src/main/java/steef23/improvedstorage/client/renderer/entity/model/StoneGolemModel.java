package steef23.improvedstorage.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import steef23.improvedstorage.common.entity.StoneGolemEntity;

public class StoneGolemModel<T extends StoneGolemEntity> extends EntityModel<T>  
{
	private final ModelRenderer stoneGolemBody;
	private final ModelRenderer stoneGolemHead;
	private final ModelRenderer stoneGolemLeftArm;
	private final ModelRenderer stoneGolemRightArm;
	private final ModelRenderer stoneGolemLeftLeg;
	private final ModelRenderer stoneGolemRightLeg;

	public StoneGolemModel()
	{
		this.textureWidth = 64;
		this.textureHeight = 128;

		this.stoneGolemBody = new ModelRenderer(this, 0, 20);
		this.stoneGolemBody.setRotationPoint(0.0f, 24.0f, 0.0f);
		this.stoneGolemBody.addBox(-7.0f, -18.0f, -6.0f, 14, 14, 12, 0.0f, false);

		this.stoneGolemHead = new ModelRenderer(this, 0, 0);
		this.stoneGolemHead.setRotationPoint(0.0f, -18.0f, 6.0f);
		this.stoneGolemHead.addBox(-7.0f, -8.0f, -12.0f, 14, 8, 12, 0.0f, false);
		this.stoneGolemHead.addBox(-2.0f, -3.0f, -14.0f, 4, 6, 2, 0.0f, false);
		this.stoneGolemBody.addChild(stoneGolemHead);

		this.stoneGolemLeftArm = new ModelRenderer(this, 0, 46);
		this.stoneGolemLeftArm.setRotationPoint(7.0f, -18.0f, 0.0f);
		this.stoneGolemLeftArm.addBox(0.0f, 0.0f, -3.0f, 2, 18, 6, 0.0f, false);
		this.stoneGolemBody.addChild(stoneGolemLeftArm);
		this.stoneGolemRightArm = new ModelRenderer(this, 0, 70);
		this.stoneGolemRightArm.setRotationPoint(-7.0f, -18.0f, 0.0f);
		this.stoneGolemRightArm.addBox(-2.0f, 0.0f, -3.0f, 2, 18, 6, 0.0f, false);
		this.stoneGolemBody.addChild(stoneGolemRightArm);
		
		this.stoneGolemLeftLeg = new ModelRenderer(this, 16, 46);
		this.stoneGolemLeftLeg.setRotationPoint(4.0f, -4.0f, 0.0f);
		this.stoneGolemLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4, 0.0f, false);
		this.stoneGolemBody.addChild(stoneGolemLeftLeg);
		this.stoneGolemRightLeg = new ModelRenderer(this, 16, 70);
		this.stoneGolemRightLeg.setRotationPoint(-4.0f, -4.0f, 0.0f);
		this.stoneGolemRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4, 0.0f, false);
		this.stoneGolemBody.addChild(stoneGolemRightLeg);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) 
	{
	      this.stoneGolemLeftLeg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.stoneGolemRightLeg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.stoneGolemLeftArm.rotateAngleX = 0.75f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.stoneGolemRightArm.rotateAngleX = -0.75f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;


	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) 
	{
		stoneGolemBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	private float triangleWave(float limbSwing, float f2) 
	{
		return (Math.abs(limbSwing % f2 - f2 * 0.5f) - f2 * 0.25f) / (f2 * 0.25f);
	}
}
