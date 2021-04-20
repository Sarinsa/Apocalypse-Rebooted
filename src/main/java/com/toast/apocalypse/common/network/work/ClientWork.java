package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.client.event.ClientEvents;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.message.S2CUpdateEntityVelocity;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficultyRate;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldMaxDifficulty;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Referencing client only code here should cause no trouble
 * as long as this class isn't loaded by anything else
 * than the client itself (which should be the case).
 */
public class ClientWork {

    public static void handleDifficultyUpdate(S2CUpdateWorldDifficulty message) {
        Apocalypse.INSTANCE.getDifficultyManager().setDifficulty(message.difficulty);
    }

    public static void handleDifficultyRateUpdate(S2CUpdateWorldDifficultyRate message) {
        Apocalypse.INSTANCE.getDifficultyManager().setDifficultyRate(message.difficultyRate);
    }

    public static void handleMaxDifficultyUpdate(S2CUpdateWorldMaxDifficulty message) {
        long maxDifficulty = message.maxDifficulty;

        Apocalypse.INSTANCE.getDifficultyManager().setMaxDifficulty(maxDifficulty);

        ClientEvents.COLOR_CHANGE = maxDifficulty > -1 ? maxDifficulty : References.DEFAULT_COLOR_CHANGE;
    }

    public static void handleEntityVelocityUpdate(S2CUpdateEntityVelocity message) {
        World world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(message.entityId);

            if (entity != null) {
                entity.setDeltaMovement(message.xMotion, message.yMotion, message.zMotion);
            }
        }
    }
}
