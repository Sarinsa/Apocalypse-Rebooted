package com.toast.apocalypse.api;

import com.toast.apocalypse.api.register.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

/**
 * Represents a trap type that can be crafted and used by the Dynamic Trap block.
 * Create an implementation of this and register it to the {@link ModRegistries#TRAP_ACTIONS_REGISTRY}
 * with a deferred register.
 */
public abstract class BaseTrapAction {
    
    private String descriptionId;
    
    public BaseTrapAction() {
    
    }
    
    /**
     * The logic this trap runs when activated.<p></p>
     *
     * @param pos          The block position of the Dynamic Trap.
     * @param direction    The facing of the Dynamic Trap block.
     * @param areaOfEffect An AABB representing the area of effect.
     */
    public abstract void execute( Level level, BlockPos pos, Direction direction, AABB areaOfEffect );
    
    /**
     * Used in {@link com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity} to
     * calculate the AAB / AoE to be used in {@link #execute(Level, BlockPos, Direction, AABB)}
     */
    public abstract int getEffectRadius();
    
    /**
     * @return A ResourceLocation pointing to this trap type's icon.
     * Icon must be 16x16 currently.
     */
    @Nonnull
    public abstract ResourceLocation iconLocation();
    
    /**
     * @return A translation key for a description that describes what this trap type does when activated.
     * Used when hovering over a ready trap-type in the Dynamic Trap GUI.
     */
    public abstract String getDescriptionKey();
    
    
    @SuppressWarnings( "ConstantConditions" )
    public final String getNameTranslationKey( BaseTrapAction trapAction ) {
        if( descriptionId == null ) {
            ResourceLocation id = ModRegistries.TRAP_ACTIONS_REGISTRY.get().getKey( trapAction );
            descriptionId = "apocalypse.trap_type." + id.getNamespace() + "." + id.getPath() + ".name";
        }
        return descriptionId;
    }
}
