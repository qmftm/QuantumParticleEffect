package com.qmftm.quantumparticleeffect.test;

import java.lang.reflect.Field;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

/** Opens Mod Menu, searches for this mod and selects it, so its icon and settings button can be seen. */
public class ModMenuClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> {
			try {
				Class<?> screenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
				client.gui.setScreen((Screen) screenClass.getConstructor(Screen.class).newInstance((Screen) null));
			} catch (ReflectiveOperationException e) {
				throw new AssertionError(e);
			}
		});
		context.waitTicks(20);
		context.runOnClient(client -> {
			try {
				Screen screen = client.gui.screen();
				Field searchBox = screen.getClass().getDeclaredField("searchBox");
				searchBox.setAccessible(true);
				((EditBox) searchBox.get(screen)).setValue("Quantum Particle");
			} catch (ReflectiveOperationException e) {
				throw new AssertionError(e);
			}
		});
		context.waitTicks(20);
		context.runOnClient(client -> {
			try {
				Screen screen = client.gui.screen();
				Field listField = screen.getClass().getDeclaredField("modList");
				listField.setAccessible(true);
				Object list = listField.get(screen);
				List<?> entries = (List<?>) list.getClass().getMethod("children").invoke(list);
				if (entries.isEmpty()) {
					throw new AssertionError("Mod Menu does not list the mod");
				}
				Object entry = entries.get(0);
				for (var method : list.getClass().getMethods()) {
					if (method.getName().equals("setSelected") && method.getParameterCount() == 1) {
						method.invoke(list, entry);
						break;
					}
				}
				screen.getClass().getMethod("updateSelectedEntry", Class.forName("com.terraformersmc.modmenu.gui.widget.entries.ModListEntry")).invoke(screen, entry);
			} catch (ReflectiveOperationException e) {
				throw new AssertionError(e);
			}
		});
		context.waitTicks(20);
		context.takeScreenshot("mod_menu");
		context.runOnClient(client -> client.gui.setScreen(null));
	}
}
