package com.novocodigo.gunmod.system;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class BallisticsSystem {
    private static final double STEP_SIZE = 0.5;
    private static final double HITBOX_INFLATION = 0.15;

    private BallisticsSystem() {}

    public static void executeHitscan(ServerLevel level, ServerPlayer shooter, double maxRange, float damage) {
        Vec3 startPos = shooter.getEyePosition();
        Vec3 lookVec = shooter.getLookAngle();
        Vec3 theoreticalEndPos = startPos.add(lookVec.scale(maxRange));

        BlockHitResult blockHit = level.clip(new ClipContext(
                startPos, theoreticalEndPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                shooter
        ));

        Vec3 actualEndPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : theoreticalEndPos;
        double actualDistance = startPos.distanceTo(actualEndPos);

        AABB trajectoryBox = new AABB(startPos, actualEndPos).inflate(1.0);
        List<Entity> potentialTargets = level.getEntities(shooter, trajectoryBox,
                e -> e instanceof LivingEntity && e.isPickable() && e.isAlive());

        double distanceCovered = 0.0;
        Vec3 currentPos = startPos;

        while (distanceCovered < actualDistance) {
            Vec3 nextPos = currentPos.add(lookVec.scale(STEP_SIZE));

            if (distanceCovered + STEP_SIZE > actualDistance) {
                nextPos = actualEndPos;
            }

            AABB stepBox = new AABB(currentPos, nextPos).inflate(HITBOX_INFLATION);

            for (Entity target : potentialTargets) {
                if (stepBox.intersects(target.getBoundingBox())) {

                    Optional<Vec3> hitPos = target.getBoundingBox()
                            .inflate(HITBOX_INFLATION)
                            .clip(currentPos, nextPos);

                    if (hitPos.isPresent()) {
                        target.hurtServer(level, level.damageSources().playerAttack(shooter), damage);
                        spawnImpactParticles(level, hitPos.get());

                        return;
                    }
                }
            }

            currentPos = nextPos;
            distanceCovered += STEP_SIZE;
        }

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            spawnImpactParticles(level, actualEndPos);
        }
    }

    private static void spawnImpactParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.CRIT,
                pos.x, pos.y, pos.z,
                5, 0.1, 0.1, 0.1, 0.05);
    }
}