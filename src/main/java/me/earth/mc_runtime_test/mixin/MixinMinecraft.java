package me.earth.mc_runtime_test.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public ClientLevel level;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHook(CallbackInfo ci) {
        LOGGER.info("Test running!");
        if (player != null && level != null) {
            LOGGER.info("Test successful!");
            assertTrue(true);
            System.exit(0);
        }
    }

}
