package steef23.improvedstorage.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class StoneGolemModel extends EntityModel<StoneGolem>
{
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public StoneGolemModel(ModelPart model)
	{
		this.body = model;
		this.head = model.getChild("head");
		this.rightArm = model.getChild("right_arm");
		this.leftArm = model.getChild("left_arm");
		this.rightLeg = model.getChild("right_leg");
		this.leftLeg = model.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-2.0f, -3.0f, -13.0f, 4.0f, 6.0f, 2.0f, new CubeDeformation(0.01f))
						.texOffs(0, 0)
						.addBox(-7.0f, -8.0f, -11.0f, 14.0f, 8.0f, 12.0f, new CubeDeformation(0.01f)),
				PartPose.offset(0.0f, 6.0f, 5.0f));

		partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
						.texOffs(0, 20)
						.addBox(-7.0F, -18.0F, -6.0F, 14.0F, 14.0F, 12.0F),
				PartPose.offset(0.0f, 24.0f, 0.0f));

		partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
						.texOffs(0, 70)
						.addBox(-2.0f, -1.0f, -3.0f, 2.0F, 18.0F, 6.0F),
				PartPose.offset(-7.0f, 5.0f, 0.0f));

		partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
						.texOffs(0, 46)
						.addBox(0.0f, -1.0f, -3.0f, 2.0F, 18.0F, 6.0F)
						.mirror(),
				PartPose.offset(7.0f, 5.0f, 0.0f));

		partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
						.texOffs(16, 70)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F),
				PartPose.offset(-4.0f, 20.0f, 0.0f));

		partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
						.texOffs(16, 46)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F)
						.mirror(),
				PartPose.offset(4.0f, 20.0f, 0.0f));

		return LayerDefinition.create(meshDefinition, 64, 128);
	}

	// TODO: Create a way to open up the head part when interacting with a player
	@Override
	public void setupAnim(StoneGolem entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
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
