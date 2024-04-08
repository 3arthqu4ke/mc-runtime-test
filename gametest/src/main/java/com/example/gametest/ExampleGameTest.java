package com.example.gametest;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class ExampleGameTest implements FabricGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void dummyGameTest(TestContext context) {
        LOGGER.info("Hi from dummy GameTest!");
        context.setBlockState(0, 2, 0, Blocks.OBSIDIAN);
        context.addInstantFinalTask(() -> context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.OBSIDIAN, "Expect block to be obsidian"));
    }

}
