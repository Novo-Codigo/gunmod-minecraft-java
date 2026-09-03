package com.novocodigo.gunmod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.Predicate;

@NullMarked
public abstract class GunItem extends Item {
    public static class GunProperties {
        private int cooldownTicks = 6;
        private float baseDamage = 8.0f;
        private double range = 64.0;
        private TagKey<Item> requiredAmmoTag;

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

        public GunProperties requiredAmmo(TagKey<Item> ammoTag) {
            this.requiredAmmoTag = Objects.requireNonNull(ammoTag, "Ammunition tag cannot be null.");
            return this;
        }
    }

    protected final int cooldownTicks;
    protected final float baseDamage;
    protected final double range;
    protected final TagKey<Item> requiredAmmoTag;

    public GunItem(Item.Properties properties, GunProperties gunProps) {
        super(properties.stacksTo(1));
        this.cooldownTicks = gunProps.cooldownTicks;
        this.baseDamage = gunProps.baseDamage;
        this.range = gunProps.range;
        this.requiredAmmoTag = gunProps.requiredAmmoTag;
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

        boolean isCreative = player.getAbilities().instabuild;
        ItemStack ammoStack = findAmmo(player);

        if (!isCreative && ammoStack.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (!isCreative) {
                ammoStack.shrink(1);
            }

            executeFire(level, player, stack);
        }

        player.getCooldowns().addCooldown(stack, this.cooldownTicks);

        return InteractionResult.PASS;
    }

    protected abstract void executeFire(Level level, Player shooter, ItemStack weapon);

    protected ItemStack findAmmo(Player player) {
        Predicate<ItemStack> isAmmo = stack -> stack.is(this.requiredAmmoTag);

        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (isAmmo.test(offhandStack)) {
            return offhandStack;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (isAmmo.test(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public TagKey<Item> getRequiredAmmoTag() {
        return this.requiredAmmoTag;
    }
}
