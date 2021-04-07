package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.client.ClientEvents;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficultyRate;

/**
 * Referencing client only code here should cause no trouble
 * as long as this class isn't loaded by anything else
 * than the client itself (which should be the case).
 */
public class ClientWork {

    public static void handleDifficultyUpdate(S2CUpdateWorldDifficulty message) {
        ClientEvents.DIFFICULTY = message.difficulty;
    }

    public static void handleDifficultyRateUpdate(S2CUpdateWorldDifficultyRate message) {
        ClientEvents.DIFFICULTY_RATE = message.difficultyRate;
    }
}
