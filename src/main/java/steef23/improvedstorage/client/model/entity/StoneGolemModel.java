package steef23.improvedstorage.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class StoneGolemModel<T extends StoneGolem> extends EntityModel<T>
{
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public StoneGolemModel(ModelPart model)
	{

		this.body = model;
		this.head = model.getChild("head");
		this.rightArm = model.getChild("right_arm");
		this.leftArm = model.getChild("left_arm");
		this.rightLeg = model.getChild("right_leg");
		this.leftLeg = model.getChild("left_leg");

//		this.stoneGolemBody.setRotationPoint(0.0f, 24.0f, 0.0f);
//		this.stoneGolemBody.addBox(-7.0f, -18.0f, -6.0f, 14, 14, 12, 0.0f, false);
//
//		this.stoneGolemHead = new ModelRenderer(this, 0, 0);
//		this.stoneGolemHead.setRotationPoint(0.0f, -18.0f, 6.0f);
//		this.stoneGolemHead.addBox(-7.0f, -8.0f, -12.0f, 14, 8, 12, 0.0f, false);
//		this.stoneGolemHead.addBox(-2.0f, -3.0f, -14.0f, 4, 6, 2, 0.0f, false);
//		this.stoneGolemBody.addChild(stoneGolemHead);
//
//		this.stoneGolemLeftArm = new ModelRenderer(this, 0, 46);
//		this.stoneGolemLeftArm.setRotationPoint(7.0f, -18.0f, 0.0f);
//		this.stoneGolemLeftArm.addBox(0.0f, 0.0f, -3.0f, 2, 18, 6, 0.0f, false);
//		this.stoneGolemBody.addChild(stoneGolemLeftArm);
//		this.stoneGolemRightArm = new ModelRenderer(this, 0, 70);
//		this.stoneGolemRightArm.setRotationPoint(-7.0f, -18.0f, 0.0f);
//		this.stoneGolemRightArm.addBox(-2.0f, 0.0f, -3.0f, 2, 18, 6, 0.0f, false);
//		this.stoneGolemBody.addChild(stoneGolemRightArm);
//
//		this.stoneGolemLeftLeg = new ModelRenderer(this, 16, 46);
//		this.stoneGolemLeftLeg.setRotationPoint(4.0f, -4.0f, 0.0f);
//		this.stoneGolemLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4, 0.0f, false);
//		this.stoneGolemBody.addChild(stoneGolemLeftLeg);
//		this.stoneGolemRightLeg = new ModelRenderer(this, 16, 70);
//		this.stoneGolemRightLeg.setRotationPoint(-4.0f, -4.0f, 0.0f);
//		this.stoneGolemRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4, 0.0f, false);
//		this.stoneGolemBody.addChild(stoneGolemRightLeg);
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();
		partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-7.0f, -8.0f, -12.0f, 14, 8, 12),
				PartPose.offset(14, 12, 0.0f));
		return LayerDefinition.create(meshDefinition, 64, 128);
	}

	// TODO: Create a way to open up the head part when interacting with a player
	@Override
	public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) 
	{
	      this.leftLeg.xRot = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.rightLeg.xRot = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.leftArm.xRot = 0.75f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
	      this.rightArm.xRot = -0.75f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;


	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
					   float red, float green, float blue, float alpha)
	{
		body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	private float triangleWave(float limbSwing, float f2)
	{
		return (Math.abs(limbSwing % f2 - f2 * 0.5f) - f2 * 0.25f) / (f2 * 0.25f);
	}

}
