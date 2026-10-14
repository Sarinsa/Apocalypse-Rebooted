package com.toast.apocalypse.client;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public final class ItemModelProps {
    
    // Property IDs
    public static final ResourceLocation MOON_PHASE_PROPERTY = Apocalypse.rl( "moon_phase" );
    
    
    /** Called from {@link ClientRegister#onClientSetup(FMLClientSetupEvent)} to register Apocalypse's item model property functions. */
    static void register() {
        // Lunar clock
        register( ApocalypseObjects.Items.LUNAR_CLOCK, MOON_PHASE_PROPERTY, ( ctx ) -> {
            final Entity entity = ctx.livingOrItemEntity();
            // noinspection resource
            return entity == null ? 0.0F : (float) entity.level().getMoonPhase();
        } );
    }
    
    /**
     * Registers an item-specific property function for the given item registry object.
     *
     * @param regObj       The registry object whose item to register a property for.
     * @param id           The ID of the property function being registered.
     * @param propFunction The property function to register.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<? extends Item> regObj, ResourceLocation id, PropertyContext.Function propFunction ) {
        ItemProperties.register( regObj.get(), id, ( itemStack, level, living, seed )
                -> propFunction.call( new PropertyContext( itemStack, level, living, seed ) ) );
    }
    
    /**
     * A record that holds the needed context for an {@link net.minecraft.client.renderer.item.ItemPropertyFunction}
     * as well as some helper methods.
     */
    @SuppressWarnings( "deprecation" )
    private record PropertyContext(ItemStack itemStack, @Nullable ClientLevel level, LivingEntity livingEntity,
                                   int seed) {
        /**
         * @return {@link #livingEntity} if it is not null,
         * and the entity representation of {@link #itemStack} otherwise.
         */
        @Nullable
        Entity livingOrItemEntity() {
            return livingEntity == null ? itemStack.getEntityRepresentation() : livingEntity;
        }
        
        /** A function that operates on an {@link PropertyContext} instance and returns a float value. */
        private interface Function {
            float call( PropertyContext ctx );
        }
    }
    
    // No instantiation
    private ItemModelProps() { }
}
