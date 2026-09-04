package com.novocodigo.gunmod.item;

import com.novocodigo.gunmod.registry.ModDataComponents;
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
        private int maxAmmo = 20;
        private TagKey<Item> requiredAmmoTag;

        public GunProperties cooldownTicks(int ticks) {
            this.cooldownTicks = ticks;
            return this;
        }

        public GunProperties cooldownSeconds(float seconds) {
            this.cooldownTicks = Math.round(seconds * 20.0f);
            return this;
        }

        public GunProperties maxAmmo(int maxAmmo) {
            this.maxAmmo = maxAmmo;
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
    protected final int maxAmmo;
    protected final TagKey<Item> requiredAmmoTag;

    public GunItem(Item.Properties properties, GunProperties gunProps) {
        super(properties.stacksTo(1).component(ModDataComponents.AMMO_COUNT.get(), gunProps.maxAmmo));
        this.cooldownTicks = gunProps.cooldownTicks;
        this.baseDamage = gunProps.baseDamage;
        this.range = gunProps.range;
        this.maxAmmo = gunProps.maxAmmo;
        this.requiredAmmoTag = gunProps.requiredAmmoTag;
    }

    public int getAmmo(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.AMMO_COUNT.get(), 0);
    }

    public void setAmmo(ItemStack stack, int ammo) {
        stack.set(ModDataComponents.AMMO_COUNT.get(), Math.clamp(ammo, 0, this.maxAmmo));
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

        int currentAmmo = getAmmo(stack);

        if (currentAmmo <= 0) {
            boolean reloaded = tryReload(player, stack);

            if (reloaded) {
                player.getCooldowns().addCooldown(stack, this.cooldownTicks);
            }

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

            if (!player.getAbilities().instabuild) {
                setAmmo(stack, currentAmmo - 1);
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

    public boolean tryReload(Player player, ItemStack stack) {
        int currentAmmo = getAmmo(stack);
        int neededAmmo = this.maxAmmo - currentAmmo;

        if (neededAmmo <= 0) return false;

        if (player.getAbilities().instabuild) {
            setAmmo(stack, this.maxAmmo);
            return true;
        }

        int ammoDrawn = consumeAmmoFromInventory(player, neededAmmo);

        if (ammoDrawn > 0) {
            setAmmo(stack, currentAmmo + ammoDrawn);
            return true;
        }

        return false;
    }

    private int consumeAmmoFromInventory(Player player, int amountNeeded) {
        if (amountNeeded <= 0) return 0;

        Predicate<ItemStack> isAmmo = s -> s.is(this.requiredAmmoTag);
        int gathered = 0;

        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
        gathered += extractAmmoFromStack(offhand, isAmmo, amountNeeded - gathered);

        if (gathered >= amountNeeded) return gathered;

        var inventory = player.getInventory();
        int size = inventory.getContainerSize();

        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getItem(i);
            gathered += extractAmmoFromStack(stack, isAmmo, amountNeeded - gathered);

            if (gathered >= amountNeeded) break;
        }

        return gathered;
    }

    private int extractAmmoFromStack(ItemStack stack, Predicate<ItemStack> isAmmo, int remainingNeeded) {
        if (stack.isEmpty() || remainingNeeded <= 0 || !isAmmo.test(stack)) return 0;

        int take = Math.min(stack.getCount(), remainingNeeded);
        stack.shrink(take);
        return take;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getAmmo(stack) < this.maxAmmo;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * (float) getAmmo(stack) / (float) this.maxAmmo);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFFF55;
    }

    public TagKey<Item> getRequiredAmmoTag() {
        return this.requiredAmmoTag;
    }
}
