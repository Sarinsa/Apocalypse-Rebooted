package com.toast.apocalypse.common.capability.difficulty;

public class DefaultDifficultyCapability implements IDifficultyCapability {

    private long difficulty;

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
}
