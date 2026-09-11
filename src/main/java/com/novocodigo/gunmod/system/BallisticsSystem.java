package com.novocodigo.gunmod.system;

import com.novocodigo.gunmod.network.BulletImpactPayload;
import com.novocodigo.gunmod.network.Networking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class BallisticsSystem {
    private static final double STEP_SIZE = 0.5;
    private static final double HITBOX_INFLATION = 0.15;
    private static final double VISUAL_RADIUS = 64.0;
    private static final double VISUAL_RADIUS_SQ = VISUAL_RADIUS * VISUAL_RADIUS;

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

        int stateId = Block.getId(level.getBlockState(blockHit.getBlockPos()));
        boolean hitBlock = blockHit.getType() == HitResult.Type.BLOCK;
        Vec3 actualEndPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : theoreticalEndPos;
        double actualDistance = startPos.distanceTo(actualEndPos);

        if (actualDistance < 0.05) {
            if (hitBlock) notifyClientImpact(level, actualEndPos, stateId);
            return;
        }

        AABB trajectoryBox = new AABB(startPos, actualEndPos).inflate(1.0);
        List<Entity> potentialTargets = level.getEntities(shooter, trajectoryBox,
                e -> e instanceof LivingEntity && e.isPickable() && e.isAlive());

        if (potentialTargets.isEmpty()) {
            if (hitBlock) notifyClientImpact(level, actualEndPos, stateId);
            return;
        }

        if (potentialTargets.size() > 1) potentialTargets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(startPos)));

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
                        notifyClientImpact(level, hitPos.get(), -1);
                        return;
                    }
                }
            }

            currX = nextX;
            currY = nextY;
            currZ = nextZ;
            distanceCovered += STEP_SIZE;
        }

        if (hitBlock) notifyClientImpact(level, actualEndPos, stateId);
    }

    private static void notifyClientImpact(ServerLevel level, Vec3 pos, int blockStateId) {
        Networking.CHANNEL.send(
                new BulletImpactPayload(pos.x, pos.y, pos.z, blockStateId),
                PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(
                        null,
                        pos.x, pos.y, pos.z,
                        VISUAL_RADIUS_SQ,
                        level.dimension()
                ))
        );
    }
}