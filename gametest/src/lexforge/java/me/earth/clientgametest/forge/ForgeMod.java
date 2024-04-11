package me.earth.clientgametest.forge;

import me.earth.clientgametest.GameTests;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod("clientgametest")
public class ForgeMod {
    @Deprecated
    public ForgeMod() {
        // Do not do this, we have to :(
        GameTestRegistry.register(GameTests.class);
    }

}
