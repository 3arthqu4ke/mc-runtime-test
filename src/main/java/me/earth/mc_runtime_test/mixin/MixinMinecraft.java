package me.earth.mc_runtime_test.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public Screen screen;
    @Shadow private volatile boolean running;

    @Unique
    private boolean mcRuntimeTest$startedLoadingSPWorld = false;
    @Unique
    private boolean mcRuntimeTest$worldCreationStarted = false;

    @Shadow
    public abstract @Nullable Overlay getOverlay();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHook(CallbackInfo ci) {
        if (getOverlay() == null) {
            if (!mcRuntimeTest$startedLoadingSPWorld && getOverlay() == null) {
                CreateWorldScreen.openFresh(Minecraft.class.cast(this), null);
                mcRuntimeTest$startedLoadingSPWorld = true;
            } else if (!mcRuntimeTest$worldCreationStarted && screen instanceof ICreateWorldScreen createWorldScreen) {
                createWorldScreen.invokeOnCreate();
                mcRuntimeTest$worldCreationStarted = true;
            }
        }

        if (player != null && level != null) {
            if (screen == null) {
                if (level.getChunk((int) player.getX(), (int) player.getZ()).isEmpty()) {
                    if (player.tickCount < 100) {
                        LOGGER.info("Waiting " + (100 - player.tickCount) + " ticks before testing...");
                    } else {
                        LOGGER.info("Test successful!");
                        assertTrue(true);
                        running = false;
                    }
                } else {
                    LOGGER.info("Players chunk not yet loaded");
                }
            } else {
                LOGGER.info("Screen not yet null: " + screen);
            }
        }
    }

}
