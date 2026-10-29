
package com.gunner76th.createrealisticlighting.client;

import com.gunner76th.createrealisticlighting.RealisticLighting;
import com.gunner76th.createrealisticlighting.config.ClientConfig;

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
