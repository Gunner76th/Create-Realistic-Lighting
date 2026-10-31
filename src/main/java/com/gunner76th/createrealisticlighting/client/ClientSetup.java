package com.gunner76th.createrealisticlighting.client;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;
import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import com.gunner76th.createrealisticlighting.registry.ModMenus;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.BlockItemStateProperties;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
        modid = CreateRealisticLighting.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.LAMP.get(), LampScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ResourceLocation colorProperty =
                    ResourceLocation.fromNamespaceAndPath(
                            CreateRealisticLighting.MOD_ID,
                            "fixture_color"
                    );

            net.minecraft.client.renderer.item.ItemPropertyFunction colorFunction =
                    (stack, level, entity, seed) -> {
                        BlockItemStateProperties properties =
                                stack.get(DataComponents.BLOCK_STATE);

                        DyeColor color = properties == null
                                ? null
                                : properties.get(IndustrialFixtureBlock.COLOR);

                        return color == null
                                ? DyeColor.GRAY.getId()
                                : color.getId();
                    };

            ItemProperties.register(
                    ModItems.INDUSTRIAL_FIXTURE_ITEM.get(),
                    colorProperty,
                    colorFunction
            );

            ItemProperties.register(
                    ModItems.SQUARE_CEILING_FIXTURE_ITEM.get(),
                    colorProperty,
                    colorFunction
            );

            ItemProperties.register(
                    ModItems.WALL_SCONCE_ITEM.get(),
                    colorProperty,
                    colorFunction
            );
        });
    }
}