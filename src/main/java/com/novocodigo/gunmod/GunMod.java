package com.novocodigo.gunmod;

import com.novocodigo.gunmod.init.ItemInit;
import com.novocodigo.gunmod.registry.ModDataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GunMod.MOD_ID)
public class GunMod {
    public static final String MOD_ID = "gunmod";
    public GunMod(FMLJavaModLoadingContext context) {
        BusGroup modBusGroup = context.getModBusGroup();
        ModDataComponents.DATA_COMPONENT_TYPES.register(modBusGroup);
        ItemInit.ITEMS.register(modBusGroup);

        BuildCreativeModeTabContentsEvent.BUS.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        var tabKey = event.getTabKey();

        if (tabKey == CreativeModeTabs.COMBAT) {
            event.accept(ItemInit.AK47);
            event.accept(ItemInit.AMMO762);
        }
    }
}
