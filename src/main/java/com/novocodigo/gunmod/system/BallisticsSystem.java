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

        boolean hitBlock = blockHit.getType() == HitResult.Type.BLOCK;
        Vec3 actualEndPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : theoreticalEndPos;
        double actualDistance = startPos.distanceTo(actualEndPos);

        if (actualDistance < 0.05) {
            if (hitBlock) spawnImpactParticles(level, actualEndPos);

            return;
        }

        AABB trajectoryBox = new AABB(startPos, actualEndPos).inflate(1.0);
        List<Entity> potentialTargets = level.getEntities(shooter, trajectoryBox,
                e -> e instanceof LivingEntity && e.isPickable() && e.isAlive());

        if (potentialTargets.isEmpty()) {
            if (hitBlock) spawnImpactParticles(level, actualEndPos);
            return;
        }

        final double dirX = lookVec.x;
        final double dirY = lookVec.y;
        final double dirZ = lookVec.z;

        final double stepX = dirX * STEP_SIZE;
        final double stepY = dirY * STEP_SIZE;
        final double stepZ = dirZ * STEP_SIZE;

        double currX = startPos.x;
        double currY = startPos.y;
        double currZ = startPos.z;

        double distanceCovered = 0.0;

        while (distanceCovered < actualDistance) {
            double nextX = currX + stepX;
            double nextY = currY + stepY;
            double nextZ = currZ + stepZ;

            if (distanceCovered + STEP_SIZE > actualDistance) {
                nextX = actualEndPos.x;
                nextY = actualEndPos.y;
                nextZ = actualEndPos.z;
            }

            double minX = (Math.min(currX, nextX)) - HITBOX_INFLATION;
            double minY = (Math.min(currY, nextY)) - HITBOX_INFLATION;
            double minZ = (Math.min(currZ, nextZ)) - HITBOX_INFLATION;
            double maxX = (Math.max(currX, nextX)) + HITBOX_INFLATION;
            double maxY = (Math.max(currY, nextY)) + HITBOX_INFLATION;
            double maxZ = (Math.max(currZ, nextZ)) + HITBOX_INFLATION;

            for (Entity target : potentialTargets) {
                AABB targetBox = target.getBoundingBox();

                if (targetBox.intersects(minX, minY, minZ, maxX, maxY, maxZ)) {
                    Vec3 stepStartVec = new Vec3(currX, currY, currZ);
                    Vec3 stepEndVec = new Vec3(nextX, nextY, nextZ);

                    Optional<Vec3> hitPos = targetBox
                            .inflate(HITBOX_INFLATION)
                            .clip(stepStartVec, stepEndVec);

                    if (hitPos.isPresent()) {
                        target.hurtServer(level, level.damageSources().playerAttack(shooter), damage);
                        spawnImpactParticles(level, hitPos.get());
                        return;
                    }
                }
            }

            currX = nextX;
            currY = nextY;
            currZ = nextZ;
            distanceCovered += STEP_SIZE;
        }

        if (hitBlock) spawnImpactParticles(level, actualEndPos);
    }

    private static void spawnImpactParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.CRIT,
                pos.x, pos.y, pos.z,
                5, 0.1, 0.1, 0.1, 0.05);
    }
}