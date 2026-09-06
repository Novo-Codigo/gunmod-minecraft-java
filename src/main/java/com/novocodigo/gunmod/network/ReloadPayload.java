package com.novocodigo.gunmod.network;

import com.novocodigo.gunmod.GunMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@NullMarked
public record ReloadPayload() implements CustomPacketPayload {
    public static final Type<ReloadPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "reload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadPayload> STREAM_CODEC =
            StreamCodec.unit(new ReloadPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
