package com.novocodigo.gunmod.core;

import com.novocodigo.gunmod.GunMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    private ModTags() {}

    public static final class Items {
        private Items() {}

        public static final TagKey<Item> ACCEPTS_762_AMMO = tag("accepts_762_ammo");
        public static final TagKey<Item> AMMUNITION = tag("ammunition");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name)
            );
        }
    }
}
