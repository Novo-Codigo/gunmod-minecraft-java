package com.novocodigo.gunmod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class GunItem extends Item {
    public static class GunProperties {
        private int cooldownTicks = 6;
        private float baseDamage = 8.0f;
        private double range = 64.0;

        public GunProperties cooldownTicks(int ticks) {
            this.cooldownTicks = ticks;
            return this;
        }

        public GunProperties cooldownSeconds(float seconds) {
            this.cooldownTicks = Math.round(seconds * 20.0f);
            return this;
        }

        public GunProperties damage(float damage) {
            this.baseDamage = damage;
            return this;
        }

        public GunProperties range(double range) {
            this.range = range;
            return this;
        }
    }

    protected final int cooldownTicks;
    protected final float baseDamage;
    protected final double range;

    public GunItem(Item.Properties properties, GunProperties gunProps) {
        super(properties.stacksTo(1));
        this.cooldownTicks = gunProps.cooldownTicks;
        this.baseDamage = gunProps.baseDamage;
        this.range = gunProps.range;
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return false;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            executeFire(level, player, stack);
        }

        player.getCooldowns().addCooldown(stack, this.cooldownTicks);

        return InteractionResult.PASS;
    }

    protected abstract void executeFire(Level level, Player shooter, ItemStack weapon);
}
