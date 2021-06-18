package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import com.toast.apocalypse.common.util.References;

public class DefaultDifficultyCapability implements IDifficultyCapability {

    private long difficulty =- (long) ApocalypseServerConfig.SERVER.getPlayerGracePeriod() * References.DAY_LENGTH;
    private double difficultyMult = 0.0D;
    private long maxDifficulty = (long) ApocalypseServerConfig.SERVER.getDefaultPlayerMaxDifficulty() * References.DAY_LENGTH;

    @Override
    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public long getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setMaxDifficulty(long maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    @Override
    public long getMaxDifficulty() {
        return this.maxDifficulty;
    }

    @Override
    public void setDifficultyMult(double multiplier) {
        this.difficultyMult = multiplier;
    }

    @Override
    public double getDifficultyMult() {
        return this.difficultyMult;
    }
}
