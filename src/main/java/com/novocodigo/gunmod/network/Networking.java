package com.novocodigo.gunmod.network;

import com.novocodigo.gunmod.GunMod;
import com.novocodigo.gunmod.client.BulletImpactClientHandler;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.SimpleChannel;

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
                .flow(PacketFlow.CLIENTBOUND)
                    .addMain(BulletImpactPayload.class, BulletImpactPayload.STREAM_CODEC, BulletImpactClientHandler::handle)
                .build();
    }
}
