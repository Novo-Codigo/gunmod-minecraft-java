package com.novocodigo.gunmod.init;

import com.novocodigo.gunmod.GunMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GunMod.MOD_ID);
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> AK47 = ITEMS.register(
            "ak47",
            () -> new Item(new Item.Properties()
                    .setId(ITEMS.key("ak47"))
                    .stacksTo(1)
            )
    );
}