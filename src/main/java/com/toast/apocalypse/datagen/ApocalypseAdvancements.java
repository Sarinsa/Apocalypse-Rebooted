package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.register.ApocalypseItems;
import com.toast.apocalypse.common.triggers.DifficultyChangeTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public class ApocalypseAdvancements implements Consumer<Consumer<Advancement>> {

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.advancement()
                .display(ApocalypseItems.SOUL_FRAGMENT.get(),
                        new TranslationTextComponent("apocalypse.advancements.root.title"),
                        new TranslationTextComponent("apocalypse.advancements.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/end.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("pass_grace_period", DifficultyChangeTrigger.Instance.difficultyGreaterOrEqual(0L))
                .save(consumer, "apocalypse/root");
    }
}
