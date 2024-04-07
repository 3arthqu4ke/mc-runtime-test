package me.earth.mc_runtime_test;

/**
 * A controller class outside of our Mixin.
 * To modify the behaviour of McRuntimeTest you can hook into the methods of this class via a Mixin.
 */
public class McRuntimeTest {
    /**
     * Our Hook in Minecrafts setScreen/displayGuiScreen.
     * If an error screen is displayed we make Minecraft exit.
     * If a DeathScreen is displayed we respawn the player.
     * You can hook into this method with a Mixin and return {@code false} to add your own custom behaviour.
     *
     * @return {@code true} if this hook should be used.
     */
    public static boolean screenHook() {
        return true;
    }

    /**
     * Our Hook in Minecrafts ticks that run every 50ms.
     * Here we join a SinglePlayer world and wait for the chunk around the player to load.
     * Then we wait an additional amount of ticks until we quit the game.
     * You can hook into this method with a Mixin and return {@code false} to add your own custom behaviour.
     *
     * @return {@code true} if this hook should be used.
     */
    public static boolean tickHook() {
        return true;
    }

}
