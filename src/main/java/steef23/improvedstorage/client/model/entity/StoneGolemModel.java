package steef23.improvedstorage.client.model.entity;
// Forge model conversion from 1.16 to 1.17 by Steven (Steaf23), program outline loosely based on https://github.com/Globox1997/ModelConverter
// Generate all required imports yourself
// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class StoneGolemModel extends EntityModel<StoneGolem>
{
	private final ModelPart stone_golem;
	private final ModelPart head;
	private final ModelPart leg_l;
	private final ModelPart arm_r;
	private final ModelPart arm_l;
	private final ModelPart leg_r;

	public StoneGolemModel(ModelPart model) {
		this.stone_golem = model;
		this.head = model.getChild("head");
		this.leg_l = model.getChild("leg_l");
		this.arm_r = model.getChild("arm_r");
		this.arm_l = model.getChild("arm_l");
		this.leg_r = model.getChild("leg_r");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("stone_golem", CubeListBuilder.create()
						.texOffs(0, 20).addBox(-7.0f, -18.0f, -6.0f, 14.0f, 14.0f, 12.0f),
				PartPose.offsetAndRotation(0.0f, 24.0f, 0.0f, 0.0f, 0.0f, 0.0f));

		partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-2.0f, -3.0f, -13.0f, 4.0f, 6.0f, 2.0f, new CubeDeformation(0.01f))
						.texOffs(0, 0).addBox(-7.0f, -8.0f, -11.0f, 14.0f, 8.0f, 12.0f, new CubeDeformation(0.01f)),
				PartPose.offsetAndRotation(0.0f, 6.0f, 5.0f, 0.0f, 0.0f, 0.0f));

		partDefinition.addOrReplaceChild("leg_l", CubeListBuilder.create()
						.texOffs(16, 46).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 4.0f, 4.0f),
				PartPose.offsetAndRotation(4.0f, 20.0f, 0.0f, 0.0f, 0.0f, 0.0f));

		partDefinition.addOrReplaceChild("arm_r", CubeListBuilder.create()
						.texOffs(0, 46).addBox(-2.0f, -1.0f, -3.0f, 2.0f, 18.0f, 6.0f).mirror(),
				PartPose.offsetAndRotation(-7.0f, 7.0f, 0.0f, 0.0f, 0.0f, 0.0f));

		partDefinition.addOrReplaceChild("arm_l", CubeListBuilder.create()
						.texOffs(0, 46).addBox(0.0f, -1.0f, -3.0f, 2.0f, 18.0f, 6.0f),
				PartPose.offsetAndRotation(7.0f, 7.0f, 0.0f, 0.0f, 0.0f, 0.0f));

		partDefinition.addOrReplaceChild("leg_r", CubeListBuilder.create()
						.texOffs(16, 46).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 4.0f, 4.0f).mirror(),
				PartPose.offsetAndRotation(-4.0f, 20.0f, 0.0f, 0.0f, 0.0f, 0.0f));

		return LayerDefinition.create(meshDefinition, 64, 128);
	}

	@Override
	public void setupAnim(StoneGolem entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.leg_r.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
		this.leg_l.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
		this.arm_l.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
		this.arm_r.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
		this.leg_r.yRot = 0.0F;
		this.leg_l.yRot = 0.0F;
	}

	@Override
	public void prepareMobModel(StoneGolem entity, float p_102615_, float p_102616_, float partialTicks)
	{
		this.head.xRot = -(float)(entity.GetOpenNess(partialTicks) * (Math.PI / 2.0F) * 0.6);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		stone_golem.render(poseStack, buffer, packedLight, packedOverlay);
	}
}
