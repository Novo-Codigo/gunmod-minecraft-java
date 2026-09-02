package com.novocodigo.gunmod.event;

import com.novocodigo.gunmod.GunMod;
import com.novocodigo.gunmod.item.GunItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunEventHandler {
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof GunItem) {
            event.setUseBlock(Result.DENY);
            event.setUseItem(Result.DENY);
        }
    }
}
