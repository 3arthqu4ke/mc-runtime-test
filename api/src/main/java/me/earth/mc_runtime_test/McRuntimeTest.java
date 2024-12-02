package me.earth.mc_runtime_test;

/**
 * A controller class outside of our Mixin.
 * To modify the behaviour of McRuntimeTest you can hook into the methods of this class via a Mixin.
 */
public class McRuntimeTest {
    /**
     * Whether to run Minecrafts game tests or not.
     */
    public static final boolean RUN_GAME_TESTS = Boolean.parseBoolean(System.getProperty("McRuntimeGameTest", "true"));
    /**
     * Whether to fail if an optional gametest fails or not.
     */
    public static final boolean GAME_TESTS_FAIL_ON_OPTIONAL = Boolean.parseBoolean(System.getProperty("McRuntimeGameTestFailOnOptional", "true"));
    /**
     * Fails if less game tests than this get found.
     */
    public static final int MIN_GAME_TESTS_TO_FIND = Integer.parseInt(System.getProperty("McRuntimeGameTestMinExpectedGameTests", "0"));
    /**
     * TODO: Boots directly into a world without going through the world creation screens.
     */
    public static final boolean FAST_WORLD = Boolean.parseBoolean(System.getProperty("McRuntimeGameTestFastWorld", "false"));
    /**
     * Close the CreateWorldScreen if player and world have been loaded. Prevents flaky 1.19-1.19.3 tests that get stuck on the CreateWorldScreen.
     */
    public static final boolean CLOSE_CREATE_WORLD_SCREEN = Boolean.parseBoolean(System.getProperty("McRuntimeGameCloseCreateWorldScreen", "true"));
    /**
     * Same as {@link #CLOSE_CREATE_WORLD_SCREEN} but for any screen displayed after world and player have been loaded.
     */
    public static final boolean CLOSE_ANY_SCREEN = Boolean.parseBoolean(System.getProperty("McRuntimeGameTestCloseAnyScreen", "false"));

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
