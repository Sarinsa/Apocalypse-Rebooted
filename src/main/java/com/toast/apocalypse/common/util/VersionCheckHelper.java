package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.ChatFormatting;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ComparableVersion;

import javax.annotation.Nullable;
import java.util.Optional;

public class VersionCheckHelper {
    
    private static final String PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.RED + Apocalypse.MOD_NAME + ChatFormatting.GRAY + "]";
    
    private static String UPDATE_MESSAGE = null;
    
    
    public static void setUpdateMessage() {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById( Apocalypse.MOD_ID );
        
        modContainer.ifPresent( ( container ) -> {
            IModInfo modInfo = container.getModInfo();
            VersionChecker.CheckResult result = VersionChecker.getResult( modInfo );
            VersionChecker.Status status = result.status();
            
            if( status == VersionChecker.Status.PENDING ) {
                Apocalypse.LOGGER.info( "Tried to fetch newest update info, but received check status PENDING." );
                return;
            }
            
            if( status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED ) {
                ComparableVersion targetVersion = result.target();
                
                if( targetVersion != null ) {
                    UPDATE_MESSAGE = createMessage( result.target() );
                }
                else {
                    Apocalypse.LOGGER.info( "Tried looking for Apocalypse updates, but VersionChecker does not contain our mod info! " +
                            "Could the version check json be broken?" );
                }
            }
        } );
    }
    
    private static String createMessage( ComparableVersion version ) {
        String[] s = version.toString().split( "-" );
        
        // This shouldn't really happen; indicates malformed version name
        if( s.length == 0 || s.length > 3 ) {
            return PREFIX + " " + ChatFormatting.YELLOW + "New version available: " + version;
        }
        String versionState = s[1];
        
        versionState = switch( versionState ) {
            case "b" -> ChatFormatting.AQUA + " BETA " + ChatFormatting.YELLOW;
            case "a" -> ChatFormatting.RED + " ALPHA " + ChatFormatting.YELLOW;
            default -> ChatFormatting.GREEN + " RELEASE " + ChatFormatting.YELLOW;
        };
        return PREFIX + " " + ChatFormatting.YELLOW + "New" + versionState + "version available: " + version;
    }
    
    @Nullable
    public static String getUpdateMessage() {
        return UPDATE_MESSAGE;
    }
}
