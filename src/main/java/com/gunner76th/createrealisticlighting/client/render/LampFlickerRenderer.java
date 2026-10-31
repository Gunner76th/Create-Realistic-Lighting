package com.gunner76th.createrealisticlighting.client.render;

import com.gunner76th.createrealisticlighting.blockentity.LampBaseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public final class LampFlickerRenderer implements BlockEntityRenderer<LampBaseBlockEntity> {

  public LampFlickerRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      LampBaseBlockEntity lamp,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay) {
    // Reserved for future client-side bulb rendering.
  }
}