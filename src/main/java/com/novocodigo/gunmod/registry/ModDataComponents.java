package com.novocodigo.gunmod.registry;

import com.novocodigo.gunmod.GunMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, GunMod.MOD_ID);

    public static final RegistryObject<DataComponentType<Integer>> AMMO_COUNT =
            DATA_COMPONENT_TYPES.register("ammo_count", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT)
                            .build()
            );
}
