package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.register.ApocalypseItems;
import com.toast.apocalypse.common.triggers.PassedGracePeriodTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.OnlyIns;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ApocalypseAdvancementProvider extends AdvancementProvider {

    public ApocalypseAdvancementProvider(DataGenerator dataGenerator, ExistingFileHelper fileHelper) {
        super(dataGenerator, fileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(ApocalypseItems.SOUL_FRAGMENT.get(),
                        new TranslationTextComponent("apocalypse.advancements.root.title"),
                        new TranslationTextComponent("apocalypse.advancements.root.description"),
                        Apocalypse.resourceLoc("textures/gui/advancements/backgrounds/night_sky.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("pass_grace_period", PassedGracePeriodTrigger.Instance.gracePeriodPassed())
                .save(consumer, Apocalypse.resourceLoc("root"), fileHelper);

        Advancement toasty = Advancement.Builder.advancement()
                .parent(root)
                .display(ApocalypseItems.FATHERLY_TOAST.get(),
                        new TranslationTextComponent("apocalypse.advancements.toasty.title"),
                        new TranslationTextComponent("apocalypse.advancements.toasty.description"),
                        null,
                        FrameType.CHALLENGE, true, true, true)
                .addCriterion("obtain_fatherly_toast", InventoryChangeTrigger.Instance.hasItems(ApocalypseItems.FATHERLY_TOAST.get()))
                .save(consumer, Apocalypse.resourceLoc("toasty"), fileHelper);
    }
}
