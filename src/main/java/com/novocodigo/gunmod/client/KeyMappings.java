package com.novocodigo.gunmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public final class KeyMappings {
    private KeyMappings() {}

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("gunmod", "category")
    );

    public static final KeyMapping REALOAD_KEY = new KeyMapping(
            "key.gunmod.reload",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY,
            0
    );
}
