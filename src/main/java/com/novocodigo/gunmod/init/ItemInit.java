package com.novocodigo.gunmod.init;

import com.novocodigo.gunmod.item.guns.AK47Item;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final String MODID = "gunmod";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> AK47 = ITEMS.register(
            "ak47",
            () -> new AK47Item(new Item.Properties()
                    .setId(ITEMS.key("ak47"))
            )
    );
}