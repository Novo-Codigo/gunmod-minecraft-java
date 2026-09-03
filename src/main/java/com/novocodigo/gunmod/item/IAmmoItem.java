package com.novocodigo.gunmod.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IAmmoItem {
    boolean isCompatibleWith(ItemStack gunStack);
    TagKey<Item> getCompatibleGunsTag();
}
