package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.datagen.lang.TranslationKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ComparableVersion;

import javax.annotation.Nullable;
import java.util.Optional;

public class VersionCheckHelper {
    
    public static final TranslationKey UPDATE_MESSAGE = Apocalypse.tk( "apocalypse.version_check.update_message",
            "%s New %s version available: %s" );
    public static final TranslationKey UPDATE_MESSAGE_MALFORMED = Apocalypse.tk( "apocalypse.version_check.update_message.malformed",
            "%s New version available: %s" );
    
    /** The prefix string that is prepended to the update message when sent to a player. */
    private static final String PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.RED + Apocalypse.MOD_NAME + ChatFormatting.GRAY + "]" + ChatFormatting.YELLOW;
    
    /** The current update message value. */
    private static Component MESSAGE = null;
    
    
    /**
     * Called from {@link Apocalypse#onLoadComplete(FMLLoadCompleteEvent)}.
     * <br><br>
     * Updates the update message component to send to players.
     */
    public static void setUpdateMessage() {
        final Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById( Apocalypse.MOD_ID );
        
        modContainer.ifPresent( ( container ) -> {
            final IModInfo modInfo = container.getModInfo();
            final VersionChecker.CheckResult result = VersionChecker.getResult( modInfo );
            final VersionChecker.Status status = result.status();
            
            if( status == VersionChecker.Status.PENDING ) {
                Apocalypse.LOGGER.info( "Tried to fetch newest update info, but received check status {}.", status.name() );
                return;
            }
            
            if( status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED ) {
                ComparableVersion version = result.target();
                
                if( version != null ) {
                    MESSAGE = createMessage( version );
                }
                else {
                    Apocalypse.LOGGER.info( "Tried looking for Apocalypse updates, but VersionChecker does not contain our mod info! " +
                            "Could the version check json be broken?" );
                }
            }
        } );
    }
    
    /** Creates the update message component to display to the player from the given version info. */
    private static Component createMessage( ComparableVersion version ) {
        final String[] parts = version.toString().split( "-" );
        
        // Should always be 3; anything else suggests a malformed version string
        if( parts.length == 3 ) {
            return UPDATE_MESSAGE_MALFORMED.withArgs( PREFIX, version );
        }
        String versionState = parts[1];
        
        versionState = switch( versionState ) {
            case "b" -> ChatFormatting.AQUA + " BETA " + ChatFormatting.YELLOW;
            case "a" -> ChatFormatting.RED + " ALPHA " + ChatFormatting.YELLOW;
            default -> ChatFormatting.GREEN + " RELEASE " + ChatFormatting.YELLOW;
        };
        return UPDATE_MESSAGE.withArgs( PREFIX, versionState, version );
    }
    
    /** @return The update message to display to the player. */
    @Nullable
    @SuppressWarnings( "unused" )
    public static Component getUpdateMessage() {
        return MESSAGE;
    }
    
    /**
     * Sends the current update message component to the player,
     * if the component exists and update notification is enabled in the version check config.
     */
    public static void trySendMessage( Player player ) {
        if( ApocalypseConfig.MISC.VERSION_CHECK.sendUpdateMessage.get() && MESSAGE != null ) {
            player.sendSystemMessage( MESSAGE );
        }
    }
}
