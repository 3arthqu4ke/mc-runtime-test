package me.earth.mc_runtime_test.mixin;

import me.earth.mc_runtime_test.McRuntimeTest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow @Final private static Logger LOGGER;

    @Shadow private IntegratedServer integratedServer;
    @Shadow public GuiScreen currentScreen;
    @Shadow public EntityPlayerSP player;
    @Shadow public WorldClient world;

    @Shadow
    volatile boolean running;
    @Unique
    private boolean mcRuntimeTest$startedLoadingSPWorld = false;

    @Shadow public abstract void launchIntegratedServer(String par1, String par2, WorldSettings par3);

    @Shadow public abstract void displayGuiScreen(GuiScreen par1);

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void displayGuiScreenHook(GuiScreen guiScreenIn, CallbackInfo ci) {
        if (!McRuntimeTest.screenHook()) {
            return;
        }

        if (guiScreenIn instanceof GuiErrorScreen) {
            running = false;
            throw new RuntimeException("Error Screen " + guiScreenIn);
        } else if (guiScreenIn instanceof GuiGameOver && player != null) {
            player.respawnPlayer();
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void tickHook(CallbackInfo ci) {
        if (!McRuntimeTest.tickHook()) {
            return;
        }

        if (currentScreen instanceof GuiMainMenu) {
            if (!mcRuntimeTest$startedLoadingSPWorld) {
                mc_runtime_test$loadSinglePlayerWorld();
                mcRuntimeTest$startedLoadingSPWorld = true;
            }
        } else {
            LOGGER.info("Waiting for overlay to disappear...");
        }

        if (player != null && world != null) {
            if (currentScreen == null) {
                if (!world.getChunk(((int) player.posX) >> 4, ((int) player.posZ) >> 4).isEmpty()) {
                    if (player.ticksExisted < 100) {
                        LOGGER.info("Waiting " + (100 - player.ticksExisted) + " ticks before testing...");
                    } else {
                        LOGGER.info("Test successful!");
                        running = false;
                    }
                } else {
                    LOGGER.info("Players chunk not yet loaded, " + player + ": cores: " + Runtime.getRuntime().availableProcessors()
                            + ", server running: " + (integratedServer == null ? "null" : integratedServer.isServerRunning()));
                }
            } else {
                LOGGER.info("Screen not yet null: " + currentScreen);
            }
        } else {
            LOGGER.info("Waiting for player to load...");
        }
    }

    @Unique
    private void mc_runtime_test$loadSinglePlayerWorld() {
        displayGuiScreen(null);
        long seed = (new Random()).nextLong();
        WorldSettings worldsettings = new WorldSettings(seed, GameType.SURVIVAL, true, false, WorldType.DEFAULT);
        launchIntegratedServer("new_world", "New World", worldsettings);
    }

}
