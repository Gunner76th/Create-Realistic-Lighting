
package com.example.realisticlighting.client;

import com.example.realisticlighting.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class PhotosensitivityWarningScreen extends Screen {

    private final Screen returnScreen;

    public PhotosensitivityWarningScreen(Screen returnScreen) {
        super(Component.literal("Photosensitivity Warning"));
        this.returnScreen = returnScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 250;

        addRenderableWidget(
                Button.builder(
                        Component.literal("I Understand - Continue"),
                        button -> finish(true)
                ).bounds(
                        centerX - buttonWidth / 2,
                        centerY + 42,
                        buttonWidth,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Disable Flickering and Continue"),
                        button -> finish(false)
                ).bounds(
                        centerX - buttonWidth / 2,
                        centerY + 68,
                        buttonWidth,
                        20
                ).build()
        );
    }

    private void finish(boolean enableFlickering) {
        ClientConfig.ENABLE_BULB_FLICKERING.set(enableFlickering);
        ClientConfig.PHOTOSENSITIVITY_WARNING_ACKNOWLEDGED.set(true);

        ClientConfig.SPEC.save();

        Minecraft.getInstance().setScreen(returnScreen);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
		
		
        graphics.fill(
                0, 0,
                this.width, this.height,
                0xFF101018
        );
		
		super.render(graphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        graphics.drawCenteredString(
                this.font,
                Component.literal("PHOTOSENSITIVITY WARNING"),
                centerX,
                centerY - 78,
                0xFFFFAA55
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "This mod can simulate flickering electric lights."
                ),
                centerX,
                centerY - 48,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "Flashing or flickering lights may trigger seizures"
                ),
                centerX,
                centerY - 32,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "or other symptoms in photosensitive individuals."
                ),
                centerX,
                centerY - 20,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "You can disable simulated bulb flickering below."
                ),
                centerX,
                centerY + 4,
                0xFFCCCCCC
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "This setting can be changed later in the client TOML."
                ),
                centerX,
                centerY + 18,
                0xFFCCCCCC
        );

        
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
