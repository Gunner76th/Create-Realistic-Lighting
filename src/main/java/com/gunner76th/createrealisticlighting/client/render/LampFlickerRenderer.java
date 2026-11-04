package com.gunner76th.createrealisticlighting.client.render;

import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.block.LampBaseBlock;
import com.gunner76th.createrealisticlighting.block.SquareCeilingFixtureBlock;
import com.gunner76th.createrealisticlighting.blockentity.LampBaseBlockEntity;
import com.gunner76th.createrealisticlighting.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class LampFlickerRenderer implements BlockEntityRenderer<LampBaseBlockEntity> {
  private static final ModelResourceLocation LED_LIT_MODEL = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("create_realistic_lighting",
          "block/industrial_fixture_gray_3_flicker_bulb_lit"));

  private static final ModelResourceLocation LED_OFF_MODEL = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("create_realistic_lighting",
          "block/industrial_fixture_gray_3_flicker_bulb_off"));

  private static final ModelResourceLocation CEILING_LIT_MODEL = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("create_realistic_lighting",
          "block/square_ceiling_fixture_gray_3_flicker_diffuser_lit"));

  private static final ModelResourceLocation CEILING_OFF_MODEL = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("create_realistic_lighting",
          "block/square_ceiling_fixture_gray_3_flicker_diffuser_off"));

  private final BlockRenderDispatcher blockRenderer;

  // Stable, position-dependent pseudo-random values. No per-frame RNG or network state.
  private static long mix(long value) {
    value ^= value >>> 30;
    value *= 0xbf58476d1ce4e5b9L;
    value ^= value >>> 27;
    value *= 0x94d049bb133111ebL;
    return value ^ (value >>> 31);
  }

  private static double unit(long value) {
    return (mix(value) >>> 11) * 0x1.0p-53;
  }

  // Only swaps the rendered bulb model. Never changes electrical state or world light.
  private static boolean showLitBulb(LampBaseBlockEntity lamp, float partialTick) {
    if (!ClientConfig.ENABLE_BULB_FLICKERING.get()) return true;
    double voltage = lamp.getClientVisualVoltage();
    if (voltage >= 115.0) return true;

    double probability;
    int minDarkTicks;
    int maxDarkTicks;
    if (voltage >= 111.0) {
      probability = 0.055; // Average roughly one event per 7 seconds at 20 TPS.
      minDarkTicks = 2;
      maxDarkTicks = 5;
    } else if (voltage >= 106.0) {
      probability = 0.16;
      minDarkTicks = 2;
      maxDarkTicks = 8;
    } else {
      probability = 0.32;
      minDarkTicks = 3;
      maxDarkTicks = 12;
    }

    // Each eight-tick window is an independent chance for a dark pulse.
    // Inspect the preceding window too, since a pulse may cross its boundary.
    double time = lamp.getLevel().getGameTime() + partialTick;
    long window = (long) Math.floor(time / 8.0);
    long pos = lamp.getBlockPos().asLong();
    for (long candidate = window - 1; candidate <= window; candidate++) {
      long seed = mix(pos ^ (candidate * 0x9e3779b97f4a7c15L));
      if (unit(seed ^ 0x632be59bd9b4e019L) >= probability) continue;
      int startOffset = (int) (unit(seed ^ 0x8cb92ba72f3d8dd7L) * 8);
      int duration = minDarkTicks + (int) (unit(seed ^ 0x4f1bbcdc676dbb51L)
          * (maxDarkTicks - minDarkTicks + 1));
      double start = candidate * 8.0 + startOffset;
      if (time >= start && time < start + duration) return false;
    }
    return true;
  }

  public LampFlickerRenderer(BlockEntityRendererProvider.Context context) {
    this.blockRenderer = context.getBlockRenderDispatcher();
  }

  @Override
  public void render(LampBaseBlockEntity lamp, float partialTick, PoseStack poseStack,
      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    BlockState state = lamp.getBlockState();
    boolean industrial = state.getBlock() instanceof IndustrialFixtureBlock;
    boolean ceiling = state.getBlock() instanceof SquareCeilingFixtureBlock;
    // All ceiling and industrial fixture colors share their respective flicker models.
    if ((!industrial && !ceiling)
        || state.getValue(IndustrialFixtureBlock.BULB_KIND) != 3
        || !state.getValue(LampBaseBlock.LIT)) {
      return;
    }

    boolean lit = showLitBulb(lamp, partialTick);
    ModelResourceLocation modelId = ceiling
        ? (lit ? CEILING_LIT_MODEL : CEILING_OFF_MODEL)
        : (lit ? LED_LIT_MODEL : LED_OFF_MODEL);
    BakedModel bulb = Minecraft.getInstance().getModelManager().getModel(modelId);
    VertexConsumer vertices = bufferSource.getBuffer(RenderType.cutout());

    poseStack.pushPose();
    poseStack.translate(0.5, 0.5, 0.5);
    if (ceiling) {
      // Match square_ceiling_fixture.json: x/y rotations for all six faces.
      switch (state.getValue(IndustrialFixtureBlock.FACING)) {
        case UP -> { }
        case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
        case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
        case SOUTH -> {
          poseStack.mulPose(Axis.YP.rotationDegrees(180));
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        case EAST -> {
          poseStack.mulPose(Axis.YP.rotationDegrees(90));
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        case WEST -> {
          poseStack.mulPose(Axis.YP.rotationDegrees(270));
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
      }
    } else {
      switch (state.getValue(IndustrialFixtureBlock.FACING)) {
        case UP -> { }
        case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
        case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
        case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(270));
        case EAST -> {
          poseStack.mulPose(Axis.YP.rotationDegrees(90));
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        case WEST -> {
          poseStack.mulPose(Axis.YP.rotationDegrees(270));
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
      }
    }
    poseStack.translate(-0.5, -0.5, -0.5);
    blockRenderer.getModelRenderer().renderModel(poseStack.last(), vertices, state, bulb,
        1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
    poseStack.popPose();
  }
}
