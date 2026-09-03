package com.novocodigo.gunmod.item.ammo;

import com.novocodigo.gunmod.core.ModTags;
import com.novocodigo.gunmod.item.AmmoItem;
import net.minecraft.world.item.Item;

public class Ammo762Item extends AmmoItem {
    public static final int DEFAULT_STACK_LIMIT = 99;

    public Ammo762Item(Item.Properties properties) {
        super(properties, new AmmoProperties()
                .maxStackSize(DEFAULT_STACK_LIMIT)
                .compatibleGuns(ModTags.Items.ACCEPTS_762_AMMO));
    }
}
