package me.earth.mc_runtime_test.tweaker;

import io.github.impactdevelopment.simpletweaker.SimpleTweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.tools.obfuscation.mcp.ObfuscationServiceMCP;

import java.io.IOException;

@SuppressWarnings("unused")
public class McRuntimeTestTweaker extends SimpleTweaker {
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectIntoClassLoader(classLoader);
        MixinBootstrap.init();

        String obfCtx = ObfuscationServiceMCP.NOTCH;
        try {
            if (classLoader.getClassBytes(
                "net.minecraftforge.common.ForgeHooks") != null) {
                obfCtx = ObfuscationServiceMCP.SEARGE;
            }
        } catch (IOException ignored) { }

        MixinEnvironment.getDefaultEnvironment()
                        .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment()
                        .setObfuscationContext(obfCtx);

        Mixins.addConfiguration("mc_runtime_test.mixins.json");
    }

}
