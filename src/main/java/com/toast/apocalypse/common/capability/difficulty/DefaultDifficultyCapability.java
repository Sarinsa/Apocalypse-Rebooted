package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.util.References;

public class DefaultDifficultyCapability implements IDifficultyCapability {

    private long difficulty;
    private long maxDifficulty = References.DEFAULT_MAX_DIFFICULTY;

    @Override
    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public long getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void addDifficulty(long amount) {
        this.difficulty += amount;
    }

    @Override
    public void setMaxDifficulty(long maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    @Override
    public long getMaxDifficulty() {
        return this.maxDifficulty;
    }
}
