package me.earth.clientgametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class GameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = "clientgametest:empty")
    public void dummyGameTest(GameTestHelper context) {
        LOGGER.info("Hi from dummy GameTest!");
        context.setBlock(0, 2, 0, Blocks.OBSIDIAN);
        context.succeedWhen(() -> context.assertBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.OBSIDIAN, "Expect block to be obsidian"));
    }

}
