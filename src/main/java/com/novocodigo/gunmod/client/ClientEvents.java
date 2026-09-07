package com.novocodigo.gunmod.client;

import com.novocodigo.gunmod.GunMod;
import com.novocodigo.gunmod.client.gui.GunCrosshairLayer;
import com.novocodigo.gunmod.item.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.REALOAD_KEY);
    }

    @SubscribeEvent
    public static void onAddGuiOverlayLayers(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().addConditionTo(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                ForgeLayeredDraw.CROSSHAIR,
                () -> {
                    Minecraft mine = Minecraft.getInstance();
                    return mine.player == null || !(mine.player.getMainHandItem().getItem() instanceof GunItem);
                }
        );

        event.getLayeredDraw().addAbove(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "gun_crosshair"),
                ForgeLayeredDraw.CROSSHAIR,
                new GunCrosshairLayer()
        );
    }
}
