
package com.example.realisticlighting.client;

import com.example.realisticlighting.RealisticLighting;
import com.example.realisticlighting.config.ClientConfig;

import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(
        modid = RealisticLighting.MOD_ID,
        value = Dist.CLIENT
)
public final class PhotosensitivityStartupHandler {

    private PhotosensitivityStartupHandler() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {

        if (!(event.getNewScreen() instanceof TitleScreen)) {
            return;
        }

        if (ClientConfig.PHOTOSENSITIVITY_WARNING_ACKNOWLEDGED.get()) {
            return;
        }

        event.setNewScreen(
                new PhotosensitivityWarningScreen(event.getNewScreen())
        );
    }
}
