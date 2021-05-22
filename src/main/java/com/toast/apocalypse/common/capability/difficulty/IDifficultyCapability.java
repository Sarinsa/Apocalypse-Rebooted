package com.toast.apocalypse.common.capability.difficulty;

public interface IDifficultyCapability {

    void setDifficulty(long difficulty);

    long getDifficulty();

    void setMaxDifficulty(long maxDifficulty);

    long getMaxDifficulty();
}
