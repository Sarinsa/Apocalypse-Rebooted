package com.toast.apocalypse.common.capability.difficulty;

public class DefaultDifficultyCapability implements IDifficultyCapability {

    private long difficulty;
    // -1 will be treated as no limit.
    private long maxDifficulty = -1;

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
