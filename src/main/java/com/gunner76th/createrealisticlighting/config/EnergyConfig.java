package com.gunner76th.createrealisticlighting.config;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/**
 * Electrical configuration for Create: Realistic Lighting.
 *
 * Configuration changes require a restart.
 */
@EventBusSubscriber(modid = CreateRealisticLighting.MOD_ID)
public final class EnergyConfig {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.DoubleValue NOMINAL_VOLTAGE;
    private static final ModConfigSpec.DoubleValue INCANDESCENT_WATTS;
    private static final ModConfigSpec.DoubleValue FLUORESCENT_WATTS;
    private static final ModConfigSpec.DoubleValue LED_WATTS;
    private static final ModConfigSpec.DoubleValue GROW_WATTS;

    private static volatile double nominalVoltage = 120.0;
    private static volatile double incandescentWatts = 60.0;
    private static volatile double fluorescentWatts = 30.0;
    private static volatile double ledWatts = 15.0;
    private static volatile double growWatts = 45.0;

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

        builder.comment(
                "Nominal electrical consumption for each bulb type in watts.",
                "These settings affect electrical load only, not brightness or lifespan.",
                "Restart the game or dedicated server to apply changes."
        ).push("bulbWattage");

        INCANDESCENT_WATTS = builder
                .defineInRange("incandescentWatts", 60.0, 1.0, 1000.0);

        FLUORESCENT_WATTS = builder
                .defineInRange("fluorescentWatts", 30.0, 1.0, 1000.0);

        LED_WATTS = builder
                .defineInRange("ledWatts", 15.0, 1.0, 1000.0);

        GROW_WATTS = builder
                .defineInRange("growWatts", 45.0, 1.0, 1000.0);

        builder.pop();
        builder.pop();

        SPEC = builder.build();
    }

    private EnergyConfig() {
    }

    @SubscribeEvent
    public static void serverStarting(ServerAboutToStartEvent event) {
        nominalVoltage = NOMINAL_VOLTAGE.get();
        incandescentWatts = INCANDESCENT_WATTS.get();
        fluorescentWatts = FLUORESCENT_WATTS.get();
        ledWatts = LED_WATTS.get();
        growWatts = GROW_WATTS.get();
    }

    public static double nominalVoltage() {
        return nominalVoltage;
    }

    public static double incandescentWatts() {
        return incandescentWatts;
    }

    public static double fluorescentWatts() {
        return fluorescentWatts;
    }

    public static double ledWatts() {
        return ledWatts;
    }

    public static double growWatts() {
        return growWatts;
    }
}
