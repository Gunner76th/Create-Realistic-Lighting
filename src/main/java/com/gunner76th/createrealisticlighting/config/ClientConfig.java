package com.gunner76th.createrealisticlighting.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue
            ENABLE_BULB_FLICKERING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("accessibility");

        ENABLE_BULB_FLICKERING = builder
                .comment("Enable simulated bulb flickering. Restart Minecraft after changing this setting.")
                .define("enableBulbFlickering", true);

        builder.pop();

        SPEC = builder.build();
    }

    private ClientConfig() {
    }
}

