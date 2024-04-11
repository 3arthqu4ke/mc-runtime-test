package me.earth.clientgametest.mixin;

import net.minecraft.gametest.framework.GameTestRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameTestRegistry.class)
public class MixinGameTestRegistry {
    /**
     * ONLY USE IF YOU KNOW WHAT YOU ARE DOING!
     * This is here just because this mod has been made to support Neoforge, Fabric and Lexforge.
     * Lexforge and Neoforge prefix the name of the Structure template in a weird way.
     *
     * @return an unprefixed name of the gametest template to use.
     */
    @ModifyArg(
        method = "turnMethodIntoTestFunction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/gametest/framework/TestFunction;<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/level/block/Rotation;IJZIILjava/util/function/Consumer;)V"),
        index = 2)
    private static String turnMethodIntoTestFunctionHook(String string) {
        if (string.contains("clientgametest")) {
            return "clientgametest:empty";
        }

        return string;
    }

}
