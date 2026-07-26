package com.toast.apocalypse.common.network;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.network.message.*;
import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

/** Helper class for sending packets. */
public class NetworkHelper {
    
    /**
     * Sends a client bound message to inform of a change in a player's difficulty rate.
     *
     * @param multiplier The new difficulty multiplier.
     */
    public static void sendUpdatePlayerDifficultyMult( ServerPlayer player, double multiplier ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficultyRate( multiplier ), player );
    }
    
    /**
     * Sends a client bound message to inform of a change in a player's difficulty rate,
     * using the current value from the server.
     */
    public static void sendUpdatePlayerDifficultyMult( ServerPlayer player ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficultyRate( CapabilityHelper.getDifficultyMult( player ) ), player );
    }
    
    /**
     * Sends a client bound message to inform that the player's difficulty changed.
     *
     * @param difficulty The player's new difficulty.
     */
    public static void sendUpdatePlayerDifficulty( ServerPlayer player, long difficulty ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficulty( difficulty ), player );
    }
    
    /**
     * Sends a client bound message to inform that the player's difficulty changed,
     * using the current value from the server.
     */
    public static void sendUpdatePlayerDifficulty( ServerPlayer player ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerDifficulty( CapabilityHelper.getDifficulty( player ) ), player );
    }
    
    /**
     * Sends a client bound message to inform that the player's max difficulty changed.
     *
     * @param maxDifficulty The player's new max difficulty.
     */
    public static void sendUpdatePlayerMaxDifficulty( ServerPlayer player, long maxDifficulty ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerMaxDifficulty( maxDifficulty ), player );
    }
    
    /**
     * Sends a client bound message to inform that the player's max difficulty changed,
     * using the current value from the server.
     */
    public static void sendUpdatePlayerMaxDifficulty( ServerPlayer player ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CUpdatePlayerMaxDifficulty( CapabilityHelper.getMaxDifficulty( player ) ), player );
    }
    
    /**
     * Sends a client bound message to update the trap type that is currently displayed in the given dynamic trap.
     *
     * @param level The level the dynamic trap block entity is in.
     * @param trap  The dynamic trap block entity to update.
     */
    public static void sendDynTrapUpdate( ServerLevel level, DynamicTrapBlockEntity trap ) {
        Objects.requireNonNull( level );
        Objects.requireNonNull( trap );
        
        ResourceLocation id = ApocalypseObjects.TRAP_ACTIONS_REGISTRY.get().getKey( trap.getCurrentTrap() );
        if( id == null ) id = ResourceLocationUtils.EMPTY;
        int trapRadius = trap.getCurrentTrapRadius();
        
        for( ServerPlayer player : level.players() ) {
            PacketHandler.sendToClient( new S2CDynTrapUpdate( trap.getBlockPos(), id.toString(), trapRadius ), player );
        }
    }
    
    /**
     * Sends a client bound message to execute a specific simple task
     * depending on the value of {@code taskId}.
     * <br><br>
     * {@code 0} - It is raining acid.<br>
     * {@code 1} - It is no longer raining acid.<br>
     * {@code 2} - Acid snow is enabled.<br>
     * {@code 3} - Acid snow is disabled.<br>
     *
     * @param taskId The ID of the task to run on the client.
     * @see com.toast.apocalypse.common.network.work.ClientWork#handleSimpleClientTaskRequest(S2CSimpleClientTask)
     */
    public static void sendSimpleClientTaskRequest( ServerPlayer player, byte taskId ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CSimpleClientTask( taskId ), player );
    }
    
    /**
     * Same as above method, except an additional piece of data can be attached.
     *
     * @param taskId The ID of the task to run on the client.
     * @param value  An additional piece of data to send.
     * @see com.toast.apocalypse.common.network.work.ClientWork#handleSimpleClientTaskRequest(S2CSimpleClientTask)
     */
    public static void sendSimpleClientTaskRequest( ServerPlayer player, byte taskId, int value ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CSimpleClientTask( taskId, value ), player );
    }
    
    /**
     * Sends a client bound message to open the Grump inventory GUI.
     *
     * @param containerId The container ID of the Grump inventory that is being opened. Used for client-server sync.
     * @param grump       The Grump entity to open the inventory for.
     */
    public static void openGrumpInventory( ServerPlayer player, int containerId, Grump grump ) {
        Objects.requireNonNull( player );
        Objects.requireNonNull( grump );
        PacketHandler.sendToClient( new S2COpenGrumpInventory( player.getUUID(), containerId, grump.getId() ), player );
    }
    
    /**
     * Sends a server bound message to request opening the Grump inventory container.
     *
     * @param playerUUID The UUID of the player to open the Grump inventory for.
     */
    public static void requestOpenGrumpInventory( UUID playerUUID ) {
        Objects.requireNonNull( playerUUID );
        PacketHandler.CHANNEL.sendToServer( new C2SOpenGrumpInventory( playerUUID ) );
    }
    
    /**
     * Sends a server bound message to inform the server that the player is riding a Grump and pressing the 'descend' keybinding.
     *
     * @param playerUUID The UUID of the player to update keybind state for.
     * @param keyPressed True if the key is pressed. False if it was released.
     */
    public static void requestGrumpDescentUpdate( UUID playerUUID, boolean keyPressed ) {
        Objects.requireNonNull( playerUUID );
        PacketHandler.CHANNEL.sendToServer( new C2SUpdateGrumpDescent( playerUUID, keyPressed ) );
    }
    
    /**
     * Sends a server bound message to inform the server that the player is riding a Grump and pressing the
     * 'Grump interact' keybinding (launching a fishhook).
     *
     * @param playerUUID The UUID of the player who is launching a fishhook on behalf of a ridden Grump.
     * @param lookVec    The current look vector of the player. Used to calculate the trajectory of the launched hook.
     */
    public static void requestGrumpInteractUpdate( UUID playerUUID, Vec3 lookVec ) {
        Objects.requireNonNull( playerUUID );
        Objects.requireNonNull( lookVec );
        PacketHandler.CHANNEL.sendToServer( new C2SUpdateGrumpInteract( playerUUID, lookVec ) );
    }
    
    /**
     * Sends a client bound message to inform the client that the specified Apocalypse event was started for the player.
     *
     * @param eventId The ID of the Apocalypse event that started.
     */
    public static void sendEventStarted( ServerPlayer player, int eventId ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CApocalypseEvent( eventId, S2CApocalypseEvent.EventStatus.STARTED ), player );
    }
    
    /**
     * Sends a client bound message to inform the client that the specified Apocalypse event just ended for the player.
     *
     * @param eventId The ID of the Apocalypse event that ended.
     */
    public static void sendEventEnded( ServerPlayer player, int eventId ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CApocalypseEvent( eventId, S2CApocalypseEvent.EventStatus.ENDED ), player );
    }
}
