package com.gunner76th.createrealisticlighting.client;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;
import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.client.render.LampFlickerRenderer;
import com.gunner76th.createrealisticlighting.registry.ModBlockEntities;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import com.gunner76th.createrealisticlighting.registry.ModMenus;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
    modid = CreateRealisticLighting.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class ClientSetup {
  @SubscribeEvent
  public static void registerFlickerModels(ModelEvent.RegisterAdditional event) {
    String[] models = {
      "industrial_fixture_gray_4_flicker_base",
      "industrial_fixture_gray_4_flicker_bulb_lit",
      "industrial_fixture_gray_4_flicker_bulb_off",
      "industrial_fixture_white_4_flicker_base",
      "industrial_fixture_white_4_flicker_bulb_lit",
      "industrial_fixture_white_4_flicker_bulb_off",
      "industrial_fixture_orange_4_flicker_base",
      "industrial_fixture_orange_4_flicker_bulb_lit",
      "industrial_fixture_orange_4_flicker_bulb_off",
      "industrial_fixture_magenta_4_flicker_base",
      "industrial_fixture_magenta_4_flicker_bulb_lit",
      "industrial_fixture_magenta_4_flicker_bulb_off",
      "industrial_fixture_light_blue_4_flicker_base",
      "industrial_fixture_light_blue_4_flicker_bulb_lit",
      "industrial_fixture_light_blue_4_flicker_bulb_off",
      "industrial_fixture_yellow_4_flicker_base",
      "industrial_fixture_yellow_4_flicker_bulb_lit",
      "industrial_fixture_yellow_4_flicker_bulb_off",
      "industrial_fixture_lime_4_flicker_base",
      "industrial_fixture_lime_4_flicker_bulb_lit",
      "industrial_fixture_lime_4_flicker_bulb_off",
      "industrial_fixture_pink_4_flicker_base",
      "industrial_fixture_pink_4_flicker_bulb_lit",
      "industrial_fixture_pink_4_flicker_bulb_off",
      "industrial_fixture_light_gray_4_flicker_base",
      "industrial_fixture_light_gray_4_flicker_bulb_lit",
      "industrial_fixture_light_gray_4_flicker_bulb_off",
      "industrial_fixture_cyan_4_flicker_base",
      "industrial_fixture_cyan_4_flicker_bulb_lit",
      "industrial_fixture_cyan_4_flicker_bulb_off",
      "industrial_fixture_purple_4_flicker_base",
      "industrial_fixture_purple_4_flicker_bulb_lit",
      "industrial_fixture_purple_4_flicker_bulb_off",
      "industrial_fixture_blue_4_flicker_base",
      "industrial_fixture_blue_4_flicker_bulb_lit",
      "industrial_fixture_blue_4_flicker_bulb_off",
      "industrial_fixture_brown_4_flicker_base",
      "industrial_fixture_brown_4_flicker_bulb_lit",
      "industrial_fixture_brown_4_flicker_bulb_off",
      "industrial_fixture_green_4_flicker_base",
      "industrial_fixture_green_4_flicker_bulb_lit",
      "industrial_fixture_green_4_flicker_bulb_off",
      "industrial_fixture_red_4_flicker_base",
      "industrial_fixture_red_4_flicker_bulb_lit",
      "industrial_fixture_red_4_flicker_bulb_off",
      "industrial_fixture_black_4_flicker_base",
      "industrial_fixture_black_4_flicker_bulb_lit",
      "industrial_fixture_black_4_flicker_bulb_off",
      "industrial_fixture_gray_2_flicker_base",
      "industrial_fixture_gray_2_flicker_bulb_lit",
      "industrial_fixture_gray_2_flicker_bulb_off",
      "square_ceiling_fixture_white_2_flicker_base",
      "square_ceiling_fixture_white_2_flicker_diffuser_lit",
      "square_ceiling_fixture_white_2_flicker_diffuser_off",
      "square_ceiling_fixture_orange_2_flicker_base",
      "square_ceiling_fixture_orange_2_flicker_diffuser_lit",
      "square_ceiling_fixture_orange_2_flicker_diffuser_off",
      "square_ceiling_fixture_magenta_2_flicker_base",
      "square_ceiling_fixture_magenta_2_flicker_diffuser_lit",
      "square_ceiling_fixture_magenta_2_flicker_diffuser_off",
      "square_ceiling_fixture_light_blue_2_flicker_base",
      "square_ceiling_fixture_light_blue_2_flicker_diffuser_lit",
      "square_ceiling_fixture_light_blue_2_flicker_diffuser_off",
      "square_ceiling_fixture_yellow_2_flicker_base",
      "square_ceiling_fixture_yellow_2_flicker_diffuser_lit",
      "square_ceiling_fixture_yellow_2_flicker_diffuser_off",
      "square_ceiling_fixture_lime_2_flicker_base",
      "square_ceiling_fixture_lime_2_flicker_diffuser_lit",
      "square_ceiling_fixture_lime_2_flicker_diffuser_off",
      "square_ceiling_fixture_pink_2_flicker_base",
      "square_ceiling_fixture_pink_2_flicker_diffuser_lit",
      "square_ceiling_fixture_pink_2_flicker_diffuser_off",
      "square_ceiling_fixture_light_gray_2_flicker_base",
      "square_ceiling_fixture_light_gray_2_flicker_diffuser_lit",
      "square_ceiling_fixture_light_gray_2_flicker_diffuser_off",
      "square_ceiling_fixture_cyan_2_flicker_base",
      "square_ceiling_fixture_cyan_2_flicker_diffuser_lit",
      "square_ceiling_fixture_cyan_2_flicker_diffuser_off",
      "square_ceiling_fixture_purple_2_flicker_base",
      "square_ceiling_fixture_purple_2_flicker_diffuser_lit",
      "square_ceiling_fixture_purple_2_flicker_diffuser_off",
      "square_ceiling_fixture_blue_2_flicker_base",
      "square_ceiling_fixture_blue_2_flicker_diffuser_lit",
      "square_ceiling_fixture_blue_2_flicker_diffuser_off",
      "square_ceiling_fixture_brown_2_flicker_base",
      "square_ceiling_fixture_brown_2_flicker_diffuser_lit",
      "square_ceiling_fixture_brown_2_flicker_diffuser_off",
      "square_ceiling_fixture_green_2_flicker_base",
      "square_ceiling_fixture_green_2_flicker_diffuser_lit",
      "square_ceiling_fixture_green_2_flicker_diffuser_off",
      "square_ceiling_fixture_red_2_flicker_base",
      "square_ceiling_fixture_red_2_flicker_diffuser_lit",
      "square_ceiling_fixture_red_2_flicker_diffuser_off",
      "square_ceiling_fixture_black_2_flicker_base",
      "square_ceiling_fixture_black_2_flicker_diffuser_lit",
      "square_ceiling_fixture_black_2_flicker_diffuser_off",
      "wall_sconce_white_2_flicker_base",
      "wall_sconce_white_2_flicker_bulb_lit",
      "wall_sconce_white_2_flicker_bulb_off",
      "wall_sconce_orange_2_flicker_base",
      "wall_sconce_orange_2_flicker_bulb_lit",
      "wall_sconce_orange_2_flicker_bulb_off",
      "wall_sconce_magenta_2_flicker_base",
      "wall_sconce_magenta_2_flicker_bulb_lit",
      "wall_sconce_magenta_2_flicker_bulb_off",
      "wall_sconce_light_blue_2_flicker_base",
      "wall_sconce_light_blue_2_flicker_bulb_lit",
      "wall_sconce_light_blue_2_flicker_bulb_off",
      "wall_sconce_yellow_2_flicker_base",
      "wall_sconce_yellow_2_flicker_bulb_lit",
      "wall_sconce_yellow_2_flicker_bulb_off",
      "wall_sconce_lime_2_flicker_base",
      "wall_sconce_lime_2_flicker_bulb_lit",
      "wall_sconce_lime_2_flicker_bulb_off",
      "wall_sconce_pink_2_flicker_base",
      "wall_sconce_pink_2_flicker_bulb_lit",
      "wall_sconce_pink_2_flicker_bulb_off",
      "wall_sconce_light_gray_2_flicker_base",
      "wall_sconce_light_gray_2_flicker_bulb_lit",
      "wall_sconce_light_gray_2_flicker_bulb_off",
      "wall_sconce_cyan_2_flicker_base",
      "wall_sconce_cyan_2_flicker_bulb_lit",
      "wall_sconce_cyan_2_flicker_bulb_off",
      "wall_sconce_purple_2_flicker_base",
      "wall_sconce_purple_2_flicker_bulb_lit",
      "wall_sconce_purple_2_flicker_bulb_off",
      "wall_sconce_blue_2_flicker_base",
      "wall_sconce_blue_2_flicker_bulb_lit",
      "wall_sconce_blue_2_flicker_bulb_off",
      "wall_sconce_brown_2_flicker_base",
      "wall_sconce_brown_2_flicker_bulb_lit",
      "wall_sconce_brown_2_flicker_bulb_off",
      "wall_sconce_green_2_flicker_base",
      "wall_sconce_green_2_flicker_bulb_lit",
      "wall_sconce_green_2_flicker_bulb_off",
      "wall_sconce_red_2_flicker_base",
      "wall_sconce_red_2_flicker_bulb_lit",
      "wall_sconce_red_2_flicker_bulb_off",
      "wall_sconce_black_2_flicker_base",
      "wall_sconce_black_2_flicker_bulb_lit",
      "wall_sconce_black_2_flicker_bulb_off",
      "wall_sconce_gray_2_flicker_base",
      "wall_sconce_gray_2_flicker_bulb_lit",
      "wall_sconce_gray_2_flicker_bulb_off",
            "wall_sconce_gray_4_flicker_base",
            "wall_sconce_gray_4_flicker_bulb_lit",
            "wall_sconce_gray_4_flicker_bulb_off",
      "square_ceiling_fixture_gray_2_flicker_base",
      "square_ceiling_fixture_gray_2_flicker_diffuser_lit",
      "square_ceiling_fixture_gray_2_flicker_diffuser_off",
      "square_ceiling_fixture_gray_4_flicker_base",
      "square_ceiling_fixture_gray_4_flicker_diffuser_lit",
      "square_ceiling_fixture_gray_4_flicker_diffuser_off",
      "square_ceiling_fixture_white_4_flicker_base",
      "square_ceiling_fixture_white_4_flicker_diffuser_lit",
      "square_ceiling_fixture_white_4_flicker_diffuser_off",
      "square_ceiling_fixture_orange_4_flicker_base",
      "square_ceiling_fixture_orange_4_flicker_diffuser_lit",
      "square_ceiling_fixture_orange_4_flicker_diffuser_off",
      "square_ceiling_fixture_magenta_4_flicker_base",
      "square_ceiling_fixture_magenta_4_flicker_diffuser_lit",
      "square_ceiling_fixture_magenta_4_flicker_diffuser_off",
      "square_ceiling_fixture_light_blue_4_flicker_base",
      "square_ceiling_fixture_light_blue_4_flicker_diffuser_lit",
      "square_ceiling_fixture_light_blue_4_flicker_diffuser_off",
      "square_ceiling_fixture_yellow_4_flicker_base",
      "square_ceiling_fixture_yellow_4_flicker_diffuser_lit",
      "square_ceiling_fixture_yellow_4_flicker_diffuser_off",
      "square_ceiling_fixture_lime_4_flicker_base",
      "square_ceiling_fixture_lime_4_flicker_diffuser_lit",
      "square_ceiling_fixture_lime_4_flicker_diffuser_off",
      "square_ceiling_fixture_pink_4_flicker_base",
      "square_ceiling_fixture_pink_4_flicker_diffuser_lit",
      "square_ceiling_fixture_pink_4_flicker_diffuser_off",
      "square_ceiling_fixture_light_gray_4_flicker_base",
      "square_ceiling_fixture_light_gray_4_flicker_diffuser_lit",
      "square_ceiling_fixture_light_gray_4_flicker_diffuser_off",
      "square_ceiling_fixture_cyan_4_flicker_base",
      "square_ceiling_fixture_cyan_4_flicker_diffuser_lit",
      "square_ceiling_fixture_cyan_4_flicker_diffuser_off",
      "square_ceiling_fixture_purple_4_flicker_base",
      "square_ceiling_fixture_purple_4_flicker_diffuser_lit",
      "square_ceiling_fixture_purple_4_flicker_diffuser_off",
      "square_ceiling_fixture_blue_4_flicker_base",
      "square_ceiling_fixture_blue_4_flicker_diffuser_lit",
      "square_ceiling_fixture_blue_4_flicker_diffuser_off",
      "square_ceiling_fixture_brown_4_flicker_base",
      "square_ceiling_fixture_brown_4_flicker_diffuser_lit",
      "square_ceiling_fixture_brown_4_flicker_diffuser_off",
      "square_ceiling_fixture_green_4_flicker_base",
      "square_ceiling_fixture_green_4_flicker_diffuser_lit",
      "square_ceiling_fixture_green_4_flicker_diffuser_off",
      "square_ceiling_fixture_red_4_flicker_base",
      "square_ceiling_fixture_red_4_flicker_diffuser_lit",
      "square_ceiling_fixture_red_4_flicker_diffuser_off",
      "square_ceiling_fixture_black_4_flicker_base",
      "square_ceiling_fixture_black_4_flicker_diffuser_lit",
      "square_ceiling_fixture_black_4_flicker_diffuser_off",
      "industrial_fixture_gray_3_flicker_base",
      "industrial_fixture_red_3_flicker_base",
      "industrial_fixture_white_3_flicker_base",
      "industrial_fixture_orange_3_flicker_base",
      "industrial_fixture_magenta_3_flicker_base",
      "industrial_fixture_light_blue_3_flicker_base",
      "industrial_fixture_yellow_3_flicker_base",
      "industrial_fixture_lime_3_flicker_base",
      "industrial_fixture_pink_3_flicker_base",
      "industrial_fixture_light_gray_3_flicker_base",
      "industrial_fixture_cyan_3_flicker_base",
      "industrial_fixture_purple_3_flicker_base",
      "industrial_fixture_blue_3_flicker_base",
      "industrial_fixture_brown_3_flicker_base",
      "industrial_fixture_green_3_flicker_base",
      "industrial_fixture_black_3_flicker_base",
      "industrial_fixture_gray_3_flicker_bulb_lit",
      "industrial_fixture_gray_3_flicker_bulb_off",
      "square_ceiling_fixture_gray_3_flicker_base",
      "square_ceiling_fixture_white_3_flicker_base",
      "square_ceiling_fixture_orange_3_flicker_base",
      "square_ceiling_fixture_magenta_3_flicker_base",
      "square_ceiling_fixture_light_blue_3_flicker_base",
      "square_ceiling_fixture_yellow_3_flicker_base",
      "square_ceiling_fixture_lime_3_flicker_base",
      "square_ceiling_fixture_pink_3_flicker_base",
      "square_ceiling_fixture_light_gray_3_flicker_base",
      "square_ceiling_fixture_cyan_3_flicker_base",
      "square_ceiling_fixture_purple_3_flicker_base",
      "square_ceiling_fixture_blue_3_flicker_base",
      "square_ceiling_fixture_brown_3_flicker_base",
      "square_ceiling_fixture_green_3_flicker_base",
      "square_ceiling_fixture_red_3_flicker_base",
      "square_ceiling_fixture_black_3_flicker_base",
      "square_ceiling_fixture_gray_3_flicker_diffuser_lit",
      "square_ceiling_fixture_gray_3_flicker_diffuser_off",
      "wall_sconce_gray_3_flicker_base",
      "wall_sconce_white_3_flicker_base",
      "wall_sconce_orange_3_flicker_base",
      "wall_sconce_magenta_3_flicker_base",
      "wall_sconce_light_blue_3_flicker_base",
      "wall_sconce_yellow_3_flicker_base",
      "wall_sconce_lime_3_flicker_base",
      "wall_sconce_pink_3_flicker_base",
      "wall_sconce_light_gray_3_flicker_base",
      "wall_sconce_cyan_3_flicker_base",
      "wall_sconce_purple_3_flicker_base",
      "wall_sconce_blue_3_flicker_base",
      "wall_sconce_brown_3_flicker_base",
      "wall_sconce_green_3_flicker_base",
      "wall_sconce_red_3_flicker_base",
      "wall_sconce_black_3_flicker_base",
      "wall_sconce_gray_3_flicker_bulb_lit",
      "wall_sconce_gray_3_flicker_bulb_off"
    };

    String[] fluorescentColors = {
      "white", "orange", "magenta", "light_blue",
      "yellow", "lime", "pink", "light_gray",
      "cyan", "purple", "blue", "brown",
      "green", "red", "black"
    };

    for (String color : fluorescentColors) {
      for (String part : new String[] {"base", "bulb_lit", "bulb_off"}) {
        ResourceLocation id =
            ResourceLocation.fromNamespaceAndPath(
                "create_realistic_lighting",
                "block/industrial_fixture_" + color + "_2_flicker_" + part);

        event.register(ModelResourceLocation.standalone(id));
      }
    }
    for (String model : models) {
      ResourceLocation id =
          ResourceLocation.fromNamespaceAndPath("create_realistic_lighting", "block/" + model);

      event.register(ModelResourceLocation.standalone(id));
    }
  }

  private ClientSetup() {}

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(ModMenus.LAMP.get(), LampScreen::new);
  }

  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.LAMP_BASE.get(), LampFlickerRenderer::new);
  }

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(
        () -> {
          ResourceLocation colorProperty =
              ResourceLocation.fromNamespaceAndPath(
                  CreateRealisticLighting.MOD_ID, "fixture_color");

          net.minecraft.client.renderer.item.ItemPropertyFunction colorFunction =
              (stack, level, entity, seed) -> {
                BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);

                DyeColor color =
                    properties == null ? null : properties.get(IndustrialFixtureBlock.COLOR);

                return color == null ? DyeColor.GRAY.getId() : color.getId();
              };

          ItemProperties.register(
              ModItems.INDUSTRIAL_FIXTURE_ITEM.get(), colorProperty, colorFunction);

          ItemProperties.register(
              ModItems.SQUARE_CEILING_FIXTURE_ITEM.get(), colorProperty, colorFunction);

          ItemProperties.register(ModItems.WALL_SCONCE_ITEM.get(), colorProperty, colorFunction);
        });
  }
}
