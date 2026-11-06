package com.gunner76th.createrealisticlighting.config;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/**
 * Electrical configuration for Create: Realistic Lighting.
 *
 * All bulb types share one nominal operating voltage.
 * Configuration changes require a restart.
 */
@EventBusSubscriber(modid = CreateRealisticLighting.MOD_ID)
public final class EnergyConfig {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.DoubleValue NOMINAL_VOLTAGE;

    private static volatile double nominalVoltage = 120.0;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("electrical");

        NOMINAL_VOLTAGE = builder
                .comment(
                        "Nominal operating voltage shared by all bulb types.",
                        "Default: 120 V. Range: 60-480 V.",
                        "Restart the game or dedicated server to apply changes."
                )
                .defineInRange("nominalVoltage", 120.0, 60.0, 480.0);

        builder.pop();

        SPEC = builder.build();
    }

    private EnergyConfig() {
    }

    @SubscribeEvent
    public static void serverStarting(ServerAboutToStartEvent event) {
        nominalVoltage = NOMINAL_VOLTAGE.get();
    }

    public static double nominalVoltage() {
        return nominalVoltage;
    }
}
