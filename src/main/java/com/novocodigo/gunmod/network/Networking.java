package com.novocodigo.gunmod.network;

import com.novocodigo.gunmod.GunMod;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.SimpleChannel;

@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Networking {
    public static final int PROTOCOL_VERSION = 1;

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();

    private Networking() {}

    public static void register() {
        CHANNEL.protocol(NetworkProtocol.PLAY)
                .flow(PacketFlow.SERVERBOUND)
                .addMain(ReloadPayload.class, ReloadPayload.STREAM_CODEC, ReloadServerHandler::handle)
                .build();
    }
}
