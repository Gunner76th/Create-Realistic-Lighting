package com.gunner76th.createrealisticlighting;

import com.gunner76th.createrealisticlighting.config.ClientConfig;
import com.gunner76th.createrealisticlighting.config.EnergyConfig;
import com.gunner76th.createrealisticlighting.registry.ModBlockEntities;
import com.gunner76th.createrealisticlighting.registry.ModBlocks;
import com.gunner76th.createrealisticlighting.registry.ModCreativeTabs;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import com.gunner76th.createrealisticlighting.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(RealisticLighting.MOD_ID)
public final class RealisticLighting {

  public static final String MOD_ID = "create_realistic_lighting";

  public RealisticLighting(IEventBus modEventBus, ModContainer modContainer) {
    ModBlocks.BLOCKS.register(modEventBus);
    ModItems.ITEMS.register(modEventBus);
    ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
    ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
    ModMenus.MENUS.register(modEventBus);

    modContainer.registerConfig(ModConfig.Type.COMMON, EnergyConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
  }
}
