package me.earth.clientgametest.neoforge;

import me.earth.clientgametest.GameTests;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.neoforged.fml.common.Mod;

@Mod("clientgametest")
public class NeoForgeMod {
    @Deprecated
    public NeoForgeMod() {
        // Do not do this, we have to :(
        GameTestRegistry.register(GameTests.class);
    }

}
