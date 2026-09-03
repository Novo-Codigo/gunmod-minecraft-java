package com.novocodigo.gunmod.init;

import com.novocodigo.gunmod.item.ammo.Ammo762Item;
import com.novocodigo.gunmod.item.guns.AK47Item;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ItemInit {
    public static final String MODID = "gunmod";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> AK47 = ITEMS.register(
            "ak47",
            () -> new AK47Item(new Item.Properties()
                    .setId(ITEMS.key("ak47"))
            )
    );

    public static final RegistryObject<Item> AMMO762 = ITEMS.register(
            "ammo762",
            () -> new Ammo762Item(new Item.Properties()
                    .setId(ITEMS.key("ammo762"))
            )
    );
}