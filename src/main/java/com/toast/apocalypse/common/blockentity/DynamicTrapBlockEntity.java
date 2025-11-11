package com.toast.apocalypse.common.blockentity;

import com.toast.apocalypse.api.BaseTrapAction;
import com.toast.apocalypse.api.register.ModRegistries;
import com.toast.apocalypse.common.block.DynamicTrapBlock;
import com.toast.apocalypse.common.core.register.ApocalypseBlockEntities;
import com.toast.apocalypse.common.core.register.ApocalypseRecipeTypes;
import com.toast.apocalypse.common.core.register.ApocalypseSounds;
import com.toast.apocalypse.common.menus.DynamicTrapMenu;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.recipe.TrapRecipe;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.IBlockEntityDebugShapeProvider;
import fathertoast.crust.api.util.IDebugShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DynamicTrapBlockEntity extends BaseContainerBlockEntity implements IBlockEntityDebugShapeProvider {
    
    private NonNullList<ItemStack> items = NonNullList.withSize( 9, ItemStack.EMPTY );
    
    private BaseTrapAction currentTrap = null;
    private int currentTrapRadius = 0;
    
    @Nullable
    private TrapRecipe currentRecipe = null;
    private int preparationTime = 0;
    private int maxPreparationTime = 0;
    
    private final RecipeManager.CachedCheck<DynamicTrapBlockEntity, TrapRecipe> quickCheck;
    
    
    protected final ContainerData dataAccess = new ContainerData() {
        public int get( int index ) {
            return switch( index ) {
                case 0 -> DynamicTrapBlockEntity.this.getPreparationTime();
                case 1 -> DynamicTrapBlockEntity.this.getMaxPreparationTime();
                case 2 -> DynamicTrapBlockEntity.this.getBlockPos().getX();
                case 3 -> DynamicTrapBlockEntity.this.getBlockPos().getY();
                case 4 -> DynamicTrapBlockEntity.this.getBlockPos().getZ();
                default -> 0;
            };
        }
        
        public void set( int index, int value ) {
            switch( index ) {
                case 0 -> DynamicTrapBlockEntity.this.setPreparationTime( value );
                case 1 -> DynamicTrapBlockEntity.this.setMaxPreparationTime( value );
            }
        }
        
        public int getCount() {
            return 5;
        }
    };
    
    
    public DynamicTrapBlockEntity( BlockPos pos, BlockState state ) {
        super( ApocalypseBlockEntities.DYNAMIC_TRAP.get(), pos, state );
        quickCheck = RecipeManager.createCheck( ApocalypseRecipeTypes.TRAP_ASSEMBLING.get() );
    }
    
    public static void serverTick( Level level, BlockPos pos, BlockState state, DynamicTrapBlockEntity trap ) {
        if( trap.getCurrentTrap() == null ) {
            if( trap.findValidRecipe() ) {
                trap.updateTrapBlock( DynamicTrapBlock.TrapState.PROCESSING );
                
                if( level.getGameTime() % 2 == 0 ) {
                    Direction dir = state.getValue( DynamicTrapBlock.FACING );
                    double x = (pos.getX() + 0.5D) + ((dir.getStepX() / 2.0) * 1.1);
                    double y = (pos.getY() + 0.5D) + ((dir.getStepY() / 2.0) * 1.1);
                    double z = (pos.getZ() + 0.5D) + ((dir.getStepZ() / 2.0) * 1.1);
                    
                    ((ServerLevel) level).sendParticles( ParticleTypes.SMOKE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D );
                }
                
                if( ++trap.preparationTime >= trap.maxPreparationTime ) {
                    trap.setCurrentTrap( trap.currentRecipe.getResultTrap() );
                    trap.currentRecipe = null;
                    trap.preparationTime = 0;
                    trap.maxPreparationTime = 0;
                    
                    // Consume items used in the recipe
                    for( ItemStack itemStack : trap.items ) {
                        if( !itemStack.isEmpty() )
                            itemStack.shrink( 1 );
                    }
                    NetworkHelper.sendDynTrapUpdate( (ServerLevel) level, trap );
                    trap.updateTrapBlock( DynamicTrapBlock.TrapState.READY );
                }
            }
            else {
                trap.currentRecipe = null;
                trap.preparationTime = 0;
                trap.maxPreparationTime = 0;
                trap.updateTrapBlock( DynamicTrapBlock.TrapState.IDLE );
            }
        }
        else {
            BlockPos facingPos = trap.getBlockPos().relative( trap.getBlockState().getValue( DynamicTrapBlock.FACING ) );
            
            trap.updateTrapBlock( level.getBlockState( facingPos ).blocksMotion()
                    ? DynamicTrapBlock.TrapState.NOT_OPERATIONAL
                    : DynamicTrapBlock.TrapState.READY );
            
        }
    }
    
    private boolean findValidRecipe() {
        currentRecipe = quickCheck.getRecipeFor( this, level ).orElse( null );
        
        if( currentRecipe != null ) {
            maxPreparationTime = currentRecipe.getPreparationTime();
            return true;
        }
        return false;
    }
    
    @SuppressWarnings( "ConstantConditions" )
    public void activateTrap() {
        if( getCurrentTrap() != null && getCurrentTrapRadius() > 0 ) {
            getCurrentTrap().execute( level, getBlockPos(), getBlockState().getValue( DynamicTrapBlock.FACING ), getBoundingBox() );
            setCurrentTrap( null );
            
            if( !getLevel().isClientSide ) {
                NetworkHelper.sendDynTrapUpdate( (ServerLevel) level, this );
            }
        }
        if( !level.isClientSide ) {
            level.playSound( null, getBlockPos(), ApocalypseSounds.DYNAMIC_TRAP_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    }
    
    public void setCurrentTrap( @Nullable BaseTrapAction trapAction ) {
        currentTrap = trapAction;
        currentTrapRadius = trapAction == null ? -1 : trapAction.getEffectRadius();
    }
    
    @Nullable
    public BaseTrapAction getCurrentTrap() {
        return currentTrap;
    }
    
    /** Overwrite called on client via packet. Do not use. */
    public void setCurrentTrapRadius( int radius ) {
        this.currentTrapRadius = radius;
    }
    
    /**
     * @return The effect radius of the current trap.<br>
     * If current trap is null, this returns -1.
     */
    public int getCurrentTrapRadius() {
        return getCurrentTrap() == null ? -1 : currentTrapRadius;
    }
    
    public int getPreparationTime() {
        return preparationTime;
    }
    
    public int getMaxPreparationTime() {
        return maxPreparationTime;
    }
    
    /** Intended to be called via packets for client updates */
    public void setPreparationTime( int preparationTime ) {
        this.preparationTime = preparationTime;
    }
    
    /** Intended to be called via packets for client updates */
    public void setMaxPreparationTime( int maxPreparationTime ) {
        this.maxPreparationTime = maxPreparationTime;
    }
    
    @SuppressWarnings( "ConstantConditions" )
    private void updateTrapBlock( DynamicTrapBlock.TrapState trapState ) {
        DynamicTrapBlock.TrapState currentState = getBlockState().getValue( DynamicTrapBlock.TRAP_STATE );
        
        if( currentState != trapState ) {
            getLevel().setBlock( getBlockPos(), getBlockState().setValue( DynamicTrapBlock.TRAP_STATE, trapState ), Block.UPDATE_CLIENTS );
        }
    }
    
    @Override
    public void load( CompoundTag compoundTag ) {
        super.load( compoundTag );
        
        items = NonNullList.withSize( getContainerSize(), ItemStack.EMPTY );
        ContainerHelper.loadAllItems( compoundTag, items );
        
        if( compoundTag.contains( "CraftingTime", Tag.TAG_ANY_NUMERIC ) ) {
            preparationTime = compoundTag.getInt( "CraftingTime" );
        }
        
        if( compoundTag.contains( "CurrentTrap", Tag.TAG_STRING ) ) {
            ResourceLocation id = ResourceLocation.tryParse( compoundTag.getString( "CurrentTrap" ) );
            
            if( ModRegistries.TRAP_ACTIONS_REGISTRY.get().containsKey( id ) ) {
                setCurrentTrap( ModRegistries.TRAP_ACTIONS_REGISTRY.get().getValue( id ) );
            }
        }
    }
    
    @Override
    @SuppressWarnings( "ConstantConditions" )
    protected void saveAdditional( CompoundTag compoundTag ) {
        super.saveAdditional( compoundTag );
        ContainerHelper.saveAllItems( compoundTag, items );
        
        compoundTag.putInt( "CraftingTime", preparationTime );
        
        if( getCurrentTrap() != null ) {
            if( ModRegistries.TRAP_ACTIONS_REGISTRY.get().containsValue( getCurrentTrap() ) ) {
                compoundTag.putString( "CurrentTrap", ModRegistries.TRAP_ACTIONS_REGISTRY.get().getKey( getCurrentTrap() ).toString() );
            }
        }
    }
    
    @Override
    protected Component getDefaultName() {
        return Component.translatable( References.DYNAMIC_TRAP_CONTAINER );
    }
    
    @Override
    protected AbstractContainerMenu createMenu( int containerId, Inventory inventory ) {
        return new DynamicTrapMenu( containerId, inventory, this, dataAccess, getBlockPos() );
    }
    
    @Override
    public int getContainerSize() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return items.stream().allMatch( ItemStack::isEmpty );
    }
    
    @Override
    public ItemStack getItem( int slot ) {
        return items.get( slot );
    }
    
    public List<ItemStack> getContents() {
        return items;
    }
    
    @Override
    public ItemStack removeItem( int slot, int count ) {
        ItemStack itemStack = ContainerHelper.removeItem( items, slot, count );
        
        if( !itemStack.isEmpty() ) {
            setChanged();
        }
        return itemStack;
    }
    
    @Override
    public ItemStack removeItemNoUpdate( int slot ) {
        return ContainerHelper.takeItem( items, slot );
    }
    
    @Override
    public void setItem( int slot, ItemStack itemStack ) {
        items.set( slot, itemStack );
        
        if( itemStack.getCount() > getMaxStackSize() ) {
            itemStack.setCount( getMaxStackSize() );
        }
        setChanged();
    }
    
    @Override
    public boolean stillValid( Player player ) {
        return Container.stillValidBlockEntity( this, player );
    }
    
    @Override
    public void clearContent() {
        items.clear();
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        
        if( getCurrentTrap() != null ) {
            if( ModRegistries.TRAP_ACTIONS_REGISTRY.get().containsValue( getCurrentTrap() ) ) {
                compoundTag.putString( "CurrentTrap", ModRegistries.TRAP_ACTIONS_REGISTRY.get().getKey( getCurrentTrap() ).toString() );
            }
        }
        return compoundTag;
    }
    
    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        if( tag.contains( "CurrentTrap", Tag.TAG_STRING ) ) {
            ResourceLocation id = ResourceLocation.tryParse( tag.getString( "CurrentTrap" ) );
            
            if( ModRegistries.TRAP_ACTIONS_REGISTRY.get().containsKey( id ) ) {
                setCurrentTrap( ModRegistries.TRAP_ACTIONS_REGISTRY.get().getValue( id ) );
            }
        }
    }
    
    @Nullable
    public AABB getBoundingBox() {
        if( getCurrentTrapRadius() < 0 ) return null;
        
        final int effectRadius = currentTrapRadius;
        final Direction dir = getBlockState().getValue( DynamicTrapBlock.FACING );
        AABB box = new AABB( getBlockPos() ).inflate( effectRadius );
        
        switch( dir ) {
            case UP: {
                box = box.move( 0.0D, (effectRadius + 1), 0.0D );
                break;
            }
            case DOWN: {
                box = box.move( 0.0D, -(effectRadius + 1), 0.0D );
                break;
            }
            case NORTH: {
                box = box.move( 0.0D, 0.0D, -(effectRadius + 1) );
                break;
            }
            case EAST: {
                box = box.move( (effectRadius + 1), 0.0D, 0.0D );
                break;
            }
            case SOUTH: {
                box = box.move( 0.0D, 0.0D, (effectRadius + 1) );
                break;
            }
            case WEST: {
                box = box.move( -(effectRadius + 1), 0.0D, 0.0D );
                break;
            }
        }
        return box;
    }
    
    // TODO - Wait for crust update
    @Override
    @Nullable
    public List<IDebugShape> getDebugShapes() {
        return getCurrentTrapRadius() > 0 ? List.of( new BoxShape( getBoundingBox() ) ) : null;
    }
}
