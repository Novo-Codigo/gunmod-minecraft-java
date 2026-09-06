package com.novocodigo.gunmod.client;

import com.novocodigo.gunmod.GunMod;
import com.novocodigo.gunmod.item.GunItem;
import com.novocodigo.gunmod.network.ReloadPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientInputHandler {
    public ClientInputHandler() {}

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mine = Minecraft.getInstance();

        if (mine.player == null || mine.screen != null) return;

        if (KeyMappings.REALOAD_KEY.consumeClick()) {
            if (mine.player.getMainHandItem().getItem() instanceof GunItem) {
                ServerboundCustomPayloadPacket packet = new ServerboundCustomPayloadPacket(new ReloadPayload());

                PacketDistributor.SERVER.noArg().send(packet);
            }
        }
    }
}
