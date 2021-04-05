package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficultyRate;

public class ClientWork {

    public static void handleDifficultyUpdate(S2CUpdateWorldDifficulty message) {
        Apocalypse.INSTANCE.getDifficultyManager().setWorldDifficulty(message.difficulty);
    }

    public static void handleDifficultyRateUpdate(S2CUpdateWorldDifficultyRate message) {
        Apocalypse.INSTANCE.getDifficultyManager().setWorldDifficultyRate(message.difficultyRate);
    }
}
