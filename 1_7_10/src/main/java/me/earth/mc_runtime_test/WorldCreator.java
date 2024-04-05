package me.earth.mc_runtime_test;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;

/**
 * Because unimined 1.7.10 produces weird classes like {@link net.minecraft.world.WorldSettings$GameType}, which do not work properly.
 */
public class WorldCreator extends GuiCreateWorld {
    public WorldCreator(GuiScreen guiScreen) {
        super(guiScreen);
    }

    public void createNewWorld() {
        actionPerformed(new GuiButton(0, 0, 0, "")); // <- id 0, loads world
    }

}
