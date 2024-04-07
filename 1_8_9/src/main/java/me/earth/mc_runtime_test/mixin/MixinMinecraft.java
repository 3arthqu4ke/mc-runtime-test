package me.earth.mc_runtime_test.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.integrated.IntegratedServer;
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
    @Shadow @Final private static Logger logger;

    @Shadow private IntegratedServer theIntegratedServer;
    @Shadow public GuiScreen currentScreen;
    @Shadow public EntityPlayerSP thePlayer;
    @Shadow public WorldClient theWorld;

    @Shadow
    volatile boolean running;
    @Unique
    private boolean mcRuntimeTest$startedLoadingSPWorld = false;

    @Shadow public abstract void launchIntegratedServer(String par1, String par2, WorldSettings par3);

    @Shadow public abstract void displayGuiScreen(GuiScreen par1);

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void displayGuiScreenHook(GuiScreen guiScreenIn, CallbackInfo ci) {
        if (guiScreenIn instanceof GuiErrorScreen) {
            running = false;
            throw new RuntimeException("Error Screen " + guiScreenIn);
        } else if (guiScreenIn instanceof GuiGameOver && thePlayer != null) {
            thePlayer.respawnPlayer();
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void tickHook(CallbackInfo ci) {
        if (currentScreen instanceof GuiMainMenu) {
            if (!mcRuntimeTest$startedLoadingSPWorld) {
                mc_runtime_test$loadSinglePlayerWorld();
                mcRuntimeTest$startedLoadingSPWorld = true;
            }
        } else {
            logger.info("Waiting for overlay to disappear...");
        }

        if (thePlayer != null && theWorld != null) {
            if (currentScreen == null) {
                if (!theWorld.getChunkFromChunkCoords(((int) thePlayer.posX) >> 4, ((int) thePlayer.posZ) >> 4).isEmpty()) {
                    if (thePlayer.ticksExisted < 100) {
                        logger.info("Waiting " + (100 - thePlayer.ticksExisted) + " ticks before testing...");
                    } else {
                        logger.info("Test successful!");
                        running = false;
                    }
                } else {
                    logger.info("Players chunk not yet loaded, " + thePlayer + ": cores: " + Runtime.getRuntime().availableProcessors()
                            + ", server running: " + (theIntegratedServer == null ? "null" : theIntegratedServer.isServerRunning()));
                }
            } else {
                logger.info("Screen not yet null: " + currentScreen);
            }
        } else {
            logger.info("Waiting for player to load...");
        }
    }

    @Unique
    private void mc_runtime_test$loadSinglePlayerWorld() {
        displayGuiScreen(null);
        long seed = (new Random()).nextLong();
        WorldSettings worldsettings = new WorldSettings(seed, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT);
        launchIntegratedServer("new_world", "New World", worldsettings);
    }

}
