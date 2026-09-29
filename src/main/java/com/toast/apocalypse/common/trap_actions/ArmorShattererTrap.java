package com.toast.apocalypse.common.trap_actions;

import com.toast.apocalypse.api.trap.AbstractTrap;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.datagen.lang.TranslationKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ArmorShattererTrap extends AbstractTrap {
    
    private static final ResourceLocation ICON = Apocalypse.rl( "textures/trap_icons/equipment_break.png" );
    
    private static final TranslationKey DESCRIPTION = Apocalypse.tk( "apocalypse.trap_type.apocalypse.armor_shatterer.description",
            "Breaks the armor of anyone or anything standing nearby" );
    
    
    @Override
    public void execute( Level level, BlockPos pos, Direction facing, AABB areaOfEffect ) {
        List<LivingEntity> entities = level.getEntitiesOfClass( LivingEntity.class, areaOfEffect );
        if( entities.isEmpty() ) return;
        
        for( LivingEntity livingEntity : entities ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                if( slot.getType() == EquipmentSlot.Type.ARMOR ) {
                    if( livingEntity.getItemBySlot( slot ).isDamageableItem() ) {
                        // TODO - Configurable damage
                        livingEntity.getItemBySlot( slot ).hurtAndBreak( 100000, livingEntity,
                                ( entity ) -> entity.broadcastBreakEvent( slot ) );
                    }
                }
            }
        }
    }
    
    @Override
    public int getEffectRadius() {
        return ApocalypseConfig.MISC.TRAP_PROPERTIES.armorShatterRange.get();
    }
    
    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }
    
    @Override
    public Component getDescription() {
        return DESCRIPTION.get();
    }
}
