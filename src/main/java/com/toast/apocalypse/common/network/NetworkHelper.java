package com.toast.apocalypse.common.network;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.network.message.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.UUID;

/** Helper class for sending packets */
public class NetworkHelper {
    
    /**
     * Sends a message from the server to client
     * to inform of a change in world difficulty rate.
     *
     * @param multiplier The new difficulty multiplier.
     */
    public static void sendUpdatePlayerDifficultyMult( @Nonnull ServerPlayer player, double multiplier ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficultyRate( multiplier ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform of a change in world difficulty rate,
     * using the current value from the server.
     */
    public static void sendUpdatePlayerDifficultyMult( @Nonnull ServerPlayer player ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficultyRate( CapabilityHelper.getPlayerDifficultyMult( player ) ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform that the player's difficulty changed.
     *
     * @param difficulty The player's new difficulty.
     */
    public static void sendUpdatePlayerDifficulty( @Nonnull ServerPlayer player, long difficulty ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficulty( difficulty ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform that the player's difficulty changed,
     * using the current difficulty value from the server.
     */
    public static void sendUpdatePlayerDifficulty( @Nonnull ServerPlayer player ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficulty( CapabilityHelper.getPlayerDifficulty( player ) ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform that the player's max difficulty changed.
     *
     * @param maxDifficulty The player's new max difficulty.
     */
    public static void sendUpdatePlayerMaxDifficulty( @Nonnull ServerPlayer player, long maxDifficulty ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerMaxDifficulty( maxDifficulty ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform that the player's max difficulty changed,
     * using the current max difficulty value from the server.
     */
    public static void sendUpdatePlayerMaxDifficulty( @Nonnull ServerPlayer player ) {
        PacketHandler.sendToClient( new S2CUpdatePlayerMaxDifficulty( CapabilityHelper.getMaxPlayerDifficulty( player ) ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to inform that the specified entity's
     * velocity has changed.
     *
     * @param entity        The entity to update velocity for.
     * @param deltaMovement The new velocity vector.
     */
    public static void sendEntityVelocityUpdate( @Nonnull ServerPlayer player, Entity entity, Vec3 deltaMovement ) {
        PacketHandler.sendToClient( new S2CUpdateEntityVelocity( entity, deltaMovement ), player );
    }
    
    /**
     * Sends a message from the server to client
     * to update the trap type that is currently inside the given dynamic trap.
     */
    public static void sendDynTrapUpdate( @Nonnull ServerLevel level, DynamicTrapBlockEntity trap ) {
        if( trap == null ) return;
        
        String id = trap.getCurrentTrap() == null
                ? ""
                : ApocalypseObjects.TRAP_ACTIONS_REGISTRY.get().getKey( trap.getCurrentTrap() ).toString();
        int trapRadius = trap.getCurrentTrapRadius();
        
        for( ServerPlayer player : level.players() ) {
            PacketHandler.sendToClient( new S2CDynTrapUpdate( trap.getBlockPos(), id, trapRadius ), player );
        }
    }
    
    /**
     * Sends a message from the server to client
     * to request one of the listed tasks depending on
     * the value of "actionId":<br>
     * <br>
     * <p>
     * 0 - It is raining acid.<br>
     * 1 - It is no longer raining acid.<br>
     * 2 - Acid snow is enabled.<br>
     * 3 - Acid snow is disabled.<br>
     */
    public static void sendSimpleClientTaskRequest( @Nonnull ServerPlayer player, byte actionId ) {
        PacketHandler.sendToClient( new S2CSimpleClientTask( actionId ), player );
    }
    
    /**
     * Same as above method, except an additional piece of data is sent (value).
     */
    public static void sendSimpleClientTaskRequest( @Nonnull ServerPlayer player, byte actionId, int value ) {
        PacketHandler.sendToClient( new S2CSimpleClientTask( actionId, value ), player );
    }
    
    /**
     * Tells the client to open the Grump inventory GUI.
     */
    public static void openGrumpInventory( @Nonnull ServerPlayer player, int containerId, @Nonnull Grump grump ) {
        PacketHandler.sendToClient( new S2COpenGrumpInventory( player.getUUID(), containerId, grump.getId() ), player );
    }
    
    /**
     * Requests from the server to open the Grump inventory container.
     */
    public static void requestOpenGrumpInventory( @Nonnull UUID playerUUID ) {
        PacketHandler.CHANNEL.sendToServer( new C2SOpenGrumpInventory( playerUUID ) );
    }
    
    /**
     * Informs the server when the player is riding a Grump and pressing the 'descend' keybinding.
     */
    public static void requestGrumpDescentUpdate( @Nonnull UUID playerUUID, boolean keyPressed ) {
        PacketHandler.CHANNEL.sendToServer( new C2SUpdateGrumpDescent( playerUUID, keyPressed ) );
    }
    
    /**
     * Informs the server when the player is riding a Grump and pressing the 'Grump interact' keybinding (launching a fishhook).
     */
    public static void requestGrumpInteractUpdate( @Nonnull UUID playerUUID, Vec3 lookVec ) {
        PacketHandler.CHANNEL.sendToServer( new C2SUpdateGrumpInteract( playerUUID, lookVec ) );
    }
}
