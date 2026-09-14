package com.toast.apocalypse.common.util;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;

public class ItemStackUtils {
    
    /** Firework item NBT keys. */
    public interface FireworkNbt {
        String FIREWORKS = "Fireworks";
        String FLIGHT = "Flight";
        String EXPLOSIONS = "Explosions";
        
        String EXPLOSION = "Explosion";
        String COLORS = "Colors";
        String TYPE = "Type";
    }
    
    
    /**
     * @param random            The random number generator to use.
     * @param minFlightDuration The minimum flight duration (ticks). Must be non-negative.
     * @param maxFlightDuration The maximum flight duration (ticks).
     * @return A firework item stack with randomized properties.
     */
    // TODO maybe randomize the explosion pattern as well?
    public static ItemStack getRandomFirework( RandomSource random, int minFlightDuration, int maxFlightDuration ) {
        Objects.requireNonNull( random );
        if( minFlightDuration < 0 )
            throw new IllegalArgumentException( "Min flight duration cannot be less than zero!" );
        if( minFlightDuration > maxFlightDuration )
            throw new IllegalArgumentException( "Min flight duration value cannot be greater than max!" );
        
        final ItemStack rocketStack = new ItemStack( Items.FIREWORK_ROCKET );
        final ItemStack starStack = new ItemStack( Items.FIREWORK_STAR );
        final DyeColor dye = Util.getRandom( DyeColor.values(), random );
        final CompoundTag explosionTag = starStack.getOrCreateTagElement( FireworkNbt.EXPLOSION );
        final CompoundTag fireworksTag = rocketStack.getOrCreateTagElement( FireworkNbt.FIREWORKS );
        final CompoundTag starExplosionTag = starStack.getTagElement( FireworkNbt.EXPLOSION );
        
        explosionTag.putIntArray( FireworkNbt.COLORS, List.of( dye.getFireworkColor() ) );
        explosionTag.putByte( FireworkNbt.TYPE, (byte) FireworkRocketItem.Shape.BURST.getId() );
        ListTag explosionsListTag = new ListTag();
        
        if( starExplosionTag != null ) {
            explosionsListTag.add( starExplosionTag );
        }
        final int flightDuration = minFlightDuration == maxFlightDuration
                ? minFlightDuration
                : minFlightDuration + random.nextInt( maxFlightDuration - minFlightDuration );
        fireworksTag.putInt( FireworkNbt.FLIGHT, flightDuration );
        
        if( !explosionsListTag.isEmpty() ) {
            fireworksTag.put( FireworkNbt.EXPLOSIONS, explosionsListTag );
        }
        return rocketStack;
    }
}
