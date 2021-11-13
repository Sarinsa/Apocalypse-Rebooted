package com.toast.apocalypse.client.mobwiki;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class MobEntry {

    private final ITextComponent mobName;
    private final ITextComponent[] mobDescription;
    private final ResourceLocation mobTexture;
    private final MobType mobType;


    private MobEntry(ITextComponent mobName, ITextComponent[] mobDescription, ResourceLocation mobTexture, MobType mobType) {
        this.mobName = mobName == null ? new StringTextComponent("") : mobName;

        this.mobDescription = mobDescription == null
                ? new StringTextComponent[] { new StringTextComponent("") }
                : mobDescription;

        this.mobTexture = mobTexture == null ? new ResourceLocation("") : mobTexture;
        this.mobType = mobType == null ? MobType.NONE : mobType;
    }

    private MobEntry(ITextComponent mobName, ITextComponent mobDescription, ResourceLocation mobTexture, MobType mobType) {
        this(mobName, new ITextComponent[] { mobDescription }, mobTexture, mobType);
    }

    public ITextComponent getMobName() {
        return this.mobName;
    }

    public ITextComponent[] getMobDescription() {
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
        private ITextComponent mobName;
        private ITextComponent mobDescription;
        private MobType mobType;

        public Builder() {

        }

        public Builder mobTexture(ResourceLocation location) {
            this.mobTexture = location;
            return this;
        }

        public Builder mobName(String mobName) {
            this.mobName = new TranslationTextComponent(mobName);
            return this;
        }

        public Builder mobName(ResourceLocation mobRegName) {
            this.mobName = new TranslationTextComponent("entity." + mobRegName.getNamespace() + "." + mobRegName.getPath());
            return this;
        }

        public Builder mobDescription(String translationKey) {
            this.mobDescription = new TranslationTextComponent(translationKey);
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
        FULL_MOON(Apocalypse.resourceLoc("textures/mobwiki/mobtype/full_moon.png")),
        THUNDERSTORM(Apocalypse.resourceLoc("textures/mobwiki/mobtype/thunderstorm.png")),
        NONE(new ResourceLocation(""));

        MobType(ResourceLocation textureLoc) {
            this.textureLoc = textureLoc;
        }

        private final ResourceLocation textureLoc;

        public ResourceLocation getTexture() {
            return this.textureLoc;
        }
    }
}
