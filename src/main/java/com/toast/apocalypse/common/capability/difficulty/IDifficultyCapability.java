package com.toast.apocalypse.common.capability.difficulty;

public interface IDifficultyCapability {

    void setDifficulty(long difficulty);

    long getDifficulty();

    void addDifficulty(long amount);
}
