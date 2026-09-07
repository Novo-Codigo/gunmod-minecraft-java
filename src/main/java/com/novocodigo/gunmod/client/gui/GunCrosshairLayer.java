package com.novocodigo.gunmod.client.gui;
import com.novocodigo.gunmod.item.GunItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeLayer;

@SuppressWarnings("unused")
public class GunCrosshairLayer implements ForgeLayer {
    @Override
    public void render(GuiGraphics gg, DeltaTracker dt) {
        Minecraft mine = Minecraft.getInstance();

        if (mine.player == null || !mine.options.getCameraType().isFirstPerson()) return;

        ItemStack mainHand = mine.player.getMainHandItem();

        if (mainHand.getItem() instanceof GunItem gun) {
            gg.pose().pushMatrix();

            int textureSize = 16;
            int x = (gg.guiWidth() - textureSize) / 2;
            int y = (gg.guiHeight() - textureSize) / 2;

            gg.blit(
                    gun.getCrosshairTexture(),
                    x, y,
                    x + textureSize, y + textureSize,
                    0.0f, 1.0f,
                    0.0f, 1.0f
            );

            gg.pose().popMatrix();
        }
    }
}
