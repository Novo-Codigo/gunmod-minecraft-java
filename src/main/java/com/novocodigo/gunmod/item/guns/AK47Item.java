package com.novocodigo.gunmod.item.guns;

import com.novocodigo.gunmod.core.ModTags;
import com.novocodigo.gunmod.item.GunItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AK47Item extends GunItem {
    public AK47Item(Item.Properties properties) {
        this(properties, new GunProperties()
                .cooldownSeconds(0.3f)
                .damage(10.0f)
                .range(48.0)
                .requiredAmmo(ModTags.Items.AMMO_762));
    }

    public AK47Item(Item.Properties properties, GunProperties gunProps) {
        super(properties, gunProps);
    }

    @Override
    protected void executeFire(Level level, Player shooter, ItemStack weapon) {
        level.playSound(
                null,
                shooter.getX(),
                shooter.getY(),
                shooter.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS,
                0.7f,
                1.8f
        );

        // Raycasting
    }
}
