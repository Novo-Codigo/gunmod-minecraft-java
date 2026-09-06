package com.novocodigo.gunmod.network;

import com.novocodigo.gunmod.item.GunItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

@SuppressWarnings("unused")
public final class ReloadServerHandler {
    public ReloadServerHandler() {}

    public static void handle(ReloadPayload payload, CustomPayloadEvent.Context context) {
        if (context.getSender() instanceof ServerPlayer player) {
            ItemStack mainHand = player.getMainHandItem();

            if (mainHand.getItem() instanceof GunItem gun) {
                gun.tryReload(player, mainHand);
            }
        }
    }
}
