package com.toast.apocalypse.api;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

/**
 * Represents a trap that can be crafted in and used by the Dynamic Trap block.
 * <br><br>
 * To register a new trap type, use the deferred register which is obtainable via {@link ApocalypseObjects#TRAP_ACTIONS_REGISTRY}.
 */
public abstract class AbstractTrap {
    
    /** The translation key for this trap type's display name. */
    private String descriptionId;
    
    
    /**
     * Called when this trap is activated by a Dynamic Trap block.
     *
     * @param pos          The block position of the Dynamic Trap.
     * @param direction    The facing of the Dynamic Trap.
     * @param areaOfEffect An AABB representing the area of effect.
     */
    public abstract void execute( Level level, BlockPos pos, Direction direction, AABB areaOfEffect );
    
    /**
     * @return The radius of this trap type. Used to calculate an AABB that represents
     * the area of effect for this trap type.
     */
    public abstract int getEffectRadius();
    
    /**
     * @return A resource location pointing to this trap type's GUI icon.
     * The only texture size currently supported is 16x16.
     */
    @Nonnull
    public abstract ResourceLocation getIcon();
    
    /**
     * @return A translation key for a description that describes what this trap type does when activated.
     * Used when hovering over a ready trap-type in the Dynamic Trap GUI.
     */
    public abstract String getDescriptionKey();
    
    /** @return The translation key for this trap type's display name. Creates the key if it does not already exist. */
    public final String getTranslationKey() {
        if( descriptionId == null ) {
            final ResourceLocation id = ApocalypseObjects.TRAP_ACTIONS_REGISTRY.get().getKey( this );
            
            if( id == null ) descriptionId = "missingno";
            else descriptionId = "apocalypse.trap_type." + id.getNamespace() + "." + id.getPath() + ".name";
        }
        return descriptionId;
    }
}
