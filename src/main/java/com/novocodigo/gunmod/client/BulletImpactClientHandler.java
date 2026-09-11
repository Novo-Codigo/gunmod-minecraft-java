package com.novocodigo.gunmod.client;

import com.novocodigo.gunmod.network.BulletImpactPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public final class BulletImpactClientHandler {
    private BulletImpactClientHandler() {}

    public static void handle(BulletImpactPayload payload, CustomPayloadEvent.Context context) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            context.setPacketHandled(true);
            return;
        }

        int stateId = payload.blockStateId();

        if (stateId == -1) {
            for (int i = 0; i < 5; i++) {
                level.addParticle(
                        ParticleTypes.CRIT,
                        payload.x(), payload.y(), payload.z(),
                        (Math.random() - 0.5) * 0.2,
                        Math.random() * 0.2,
                        (Math.random() - 0.5) * 0.2
                );
            }
        } else {
            BlockState state = Block.stateById(stateId);
            if (!state.isAir()) {
                for (int i = 0; i < 4; i++) {
                    level.addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, state),
                            payload.x(), payload.y(), payload.z(),
                            (Math.random() - 0.5) * 0.15,
                            0.08,
                            (Math.random() - 0.5) * 0.15
                    );
                }
            }
        }

        context.setPacketHandled(true);
    }
}
