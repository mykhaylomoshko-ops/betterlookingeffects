package com.mykhaylo.betterlookingeffects;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EffectCustomizerClient implements ClientModInitializer {
    public static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        EffectConfig.load();

        // Default: Right Shift. Rebind any time in Options > Controls > Key Binds.
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.betterlookingeffects.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("betterlookingeffects", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new EffectScreen());
            }
        });
    }
}
