package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.common.core.config.ServerConfigHelper;
import com.toast.apocalypse.common.network.message.C2SUpdateServerConfigValues;

public class ServerWork {

    public static void handleServerConfigUpdate(C2SUpdateServerConfigValues message) {
        ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD = message.gracePeriod;
        ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY = message.maxDifficulty;
    }
}
