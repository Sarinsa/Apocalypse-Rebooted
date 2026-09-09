package com.toast.apocalypse.common.core.difficulty;

import fathertoast.crust.api.config.common.value.ITooltipEnum;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public enum ReductionType implements ITooltipEnum {
    
    NONE( "none",
            "No difficulty reduction when the player dies." ),
    
    LEVEL( "level",
            "Difficulty is reduced by the number of levels specified by 'reduction_levels' upon death." ),
    
    PERCENTAGE( "percentage",
            "Difficulty is reduced by the percentage specified by 'reduction_percentage' upon death." ),
    
    BOTH( "both",
            "Difficulty is reduced by the number of levels specified by 'reduction_levels', " +
                    "and then by the percentage specified by 'reduction_percentage' upon death." );
    
    
    final String name;
    final Component tooltip;
    
    ReductionType( String name, String tooltip ) {
        this.name = name;
        this.tooltip = Component.literal( tooltip );
    }
    
    //    @Override
    //    public String getSerializedName() {
    //        return name;
    //    }
    
    @Nullable
    public static ReductionType getByName( String name ) {
        for( ReductionType reductionType : values() ) {
            if( reductionType.name.equals( name ) )
                return reductionType;
        }
        return null;
    }
    
    @Override
    @Nullable
    public Component getTooltip() { return tooltip; }
}