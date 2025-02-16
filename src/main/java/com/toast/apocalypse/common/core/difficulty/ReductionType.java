package com.toast.apocalypse.common.core.difficulty;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum ReductionType implements StringRepresentable {
    NONE("none"),
    RESET("reset"),
    LEVEL("level"),
    PERCENTAGE("percentage");

    ReductionType(String name) {
        this.name = name;
    }
    final String name;

    @Override
    public String getSerializedName() {
        return name;
    }

    @Nullable
    public static ReductionType getByName(String name) {
        for (ReductionType reductionType : values()) {
            if (reductionType.name.equals(name))
                return reductionType;
        }
        return null;
    }
}
