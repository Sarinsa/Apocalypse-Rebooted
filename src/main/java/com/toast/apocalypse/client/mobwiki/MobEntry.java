package com.toast.apocalypse.client.mobwiki;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public final class MobEntry {

    private final MutableComponent mobName;
    private final MutableComponent[] mobDescription;
    private final ResourceLocation mobTexture;
    private final MobType mobType;


    private MobEntry(MutableComponent mobName, MutableComponent[] mobDescription, ResourceLocation mobTexture, MobType mobType) {
        this.mobName = mobName == null ? Component.empty() : mobName;

        this.mobDescription = mobDescription == null
                ? new MutableComponent[] { Component.empty() }
                : mobDescription;

        this.mobTexture = mobTexture == null ? new ResourceLocation("") : mobTexture;
        this.mobType = mobType == null ? MobType.NORMAL : mobType;
    }

    private MobEntry(MutableComponent mobName, MutableComponent mobDescription, ResourceLocation mobTexture, MobType mobType) {
        this(mobName, new MutableComponent[] { mobDescription }, mobTexture, mobType);
    }

    public MutableComponent getMobName() {
        return this.mobName;
    }

    public MutableComponent[] getMobDescription() {
        return this.mobDescription;
    }

    public ResourceLocation getMobTexture() {
        return this.mobTexture;
    }

    public MobType getMobType() {
        return this.mobType;
    }

    protected static final class Builder {

        private ResourceLocation mobTexture;
        private MutableComponent mobName;
        private MutableComponent mobDescription;
        private MobType mobType;

        public Builder() {

        }

        public Builder mobTexture(ResourceLocation location) {
            this.mobTexture = location;
            return this;
        }

        public Builder mobName(String mobName) {
            this.mobName = Component.literal(mobName);
            return this;
        }

        public Builder mobName(ResourceLocation mobRegName) {
            this.mobName = Component.translatable("entity." + mobRegName.getNamespace() + "." + mobRegName.getPath());
            return this;
        }

        public Builder mobDescription(String translationKey) {
            this.mobDescription = Component.translatable(translationKey);
            return this;
        }

        public Builder mobType(MobType mobType) {
            this.mobType = mobType;
            return this;
        }

        public MobEntry build() {
            return new MobEntry(this.mobName, this.mobDescription, this.mobTexture, this.mobType);
        }
    }

    public enum MobType {
        FULL_MOON("full_moon"),
        THUNDERSTORM("thunderstorm"),
        NORMAL("normal");

        MobType(String textureName) {
            this.textureLoc = Apocalypse.resourceLoc("textures/mobwiki/mobtype/" + textureName + ".png");
        }

        private final ResourceLocation textureLoc;

        public ResourceLocation getTexture() {
            return this.textureLoc;
        }
    }
}
