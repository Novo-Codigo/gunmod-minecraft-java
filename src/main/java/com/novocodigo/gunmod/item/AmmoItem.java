package com.novocodigo.gunmod.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public abstract class AmmoItem extends Item implements IAmmoItem {
    public static class AmmoProperties {
        private int maxStackSize = 99;
        private TagKey<Item> compatibleGunsTag;

        public AmmoProperties maxStackSize(int maxStackSize) {
            if (maxStackSize <= 0) {
                throw new IllegalArgumentException("Stack size must be greater than 0.");
            }

            this.maxStackSize = maxStackSize;
            return this;
        }

        public AmmoProperties compatibleGuns(TagKey<Item> tag) {
            this.compatibleGunsTag = Objects.requireNonNull(tag, "Compatible weapon tags cannot be null.");
            return this;
        }
    }

    private final TagKey<Item> compatibleGunsTag;

    public AmmoItem(Item.Properties properties, AmmoProperties ammoProps) {
        super(properties.stacksTo(ammoProps.maxStackSize));
        this.compatibleGunsTag = ammoProps.compatibleGunsTag;
    }

    @Override
    public boolean isCompatibleWith(ItemStack gunStack) {
        if (this.compatibleGunsTag == null || gunStack == null || gunStack.isEmpty()) {
            return false;
        }

        return gunStack.is(this.compatibleGunsTag);
    }

    @Override
    public TagKey<Item> getCompatibleGunsTag() {
        return this.compatibleGunsTag;
    }
}
