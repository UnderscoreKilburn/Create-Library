package com.petrolpark.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {
    
    public static void openScreen(Screen screen) {
		Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(screen));
	};
};
