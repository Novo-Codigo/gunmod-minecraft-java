package com.novocodigo.gunmod.network;

import com.novocodigo.gunmod.GunMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@NullMarked
public record BulletImpactPayload(double x, double y, double z, int blockStateId) implements CustomPacketPayload {
        public static final Type<BulletImpactPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "bullet_impact"));

        public static final StreamCodec<RegistryFriendlyByteBuf, BulletImpactPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, BulletImpactPayload::x,
                ByteBufCodecs.DOUBLE, BulletImpactPayload::y,
                ByteBufCodecs.DOUBLE, BulletImpactPayload::z,
                ByteBufCodecs.VAR_INT, BulletImpactPayload::blockStateId,
                BulletImpactPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
}
