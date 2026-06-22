package com.snackpirate.joy_of_tinkering.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import slimeknights.tconstruct.tools.entity.ToolProjectile;

public class ModifiableBulletRenderer<T extends Projectile & ToolProjectile> extends EntityRenderer<T> {
	private final ItemRenderer itemRenderer;
	public ModifiableBulletRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.75f, 0.75f, 0.75f);
			matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90));
			matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) - 45));
			matrixStackIn.translate(-0.03125, -0.09375, 0);
			this.itemRenderer.renderStatic(entity.getDisplayTool(), ItemDisplayContext.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, entity.level(), entity.getId());
			matrixStackIn.popPose();
			super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T pEntity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
