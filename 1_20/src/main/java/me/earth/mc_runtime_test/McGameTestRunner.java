package me.earth.mc_runtime_test;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Similar to running the "/test runall" command.
 */
public class McGameTestRunner {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Basically what happens in {@link TestCommand} when "runall" is used.
     * We just exit with an error code if a test fails.
     *
     * @param playerUUID the uuid of the player.
     * @param server the server to run the tests on.
     */
    public static MultipleTestTracker runGameTests(UUID playerUUID, MinecraftServer server) throws ExecutionException, InterruptedException {
        return server.submit(() -> {
            Player player = Objects.requireNonNull(server.getPlayerList().getPlayer(playerUUID));
            ServerLevel level = (ServerLevel) player.level();
            GameTestRunner.clearMarkers(level);
            Collection<TestFunction> testFunctions = GameTestRegistry.getAllTestFunctions();
            LOGGER.info("TestFunctions: " + testFunctions);
            GameTestRegistry.forgetFailedTests();

            BlockPos pos = createTestPositionAround(player, level);
            Rotation rotation = StructureUtils.getRotationForRotationSteps(0);
            Collection<GameTestInfo> tests = GameTestRunner.runTests(testFunctions, pos, rotation, level, GameTestTicker.SINGLETON, 8);
            MultipleTestTracker multipleTestTracker = new MultipleTestTracker(tests);
            multipleTestTracker.addFailureListener(gameTestInfo -> {
                LOGGER.error("Test failed: " + gameTestInfo);
                if (gameTestInfo.getError() != null) {
                    LOGGER.error(String.valueOf(gameTestInfo), gameTestInfo.getError());
                }

                if (!gameTestInfo.isOptional() || McRuntimeTest.GAME_TESTS_FAIL_ON_OPTIONAL) {
                    System.exit(-1);
                }
            });

            return multipleTestTracker;
        }).get();
    }

    private static BlockPos createTestPositionAround(Player player, ServerLevel level) {
        BlockPos blockPos = player.getOnPos();
        int y = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY();
        return new BlockPos(blockPos.getX(), y + 1, blockPos.getZ() + 3);
    }

}
