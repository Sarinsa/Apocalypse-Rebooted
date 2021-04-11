package com.toast.apocalypse.common.misc;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class EntityAttributeModifiers {

    private static final UUID HEAVY_ID = UUID.fromString("47A23AE3-904F-4C7B-9910-E2A8641B8394");

    public static final AttributeModifier HEAVY = new AttributeModifier(HEAVY_ID, "Heavy falling acceleration increment", 0.09, AttributeModifier.Operation.ADDITION);
}
