package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.triggers.PassedGracePeriodTrigger;
import com.toast.apocalypse.common.triggers.TamedGrumpTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ApocalypseAdvancementProvider extends AdvancementProvider {

    public ApocalypseAdvancementProvider(DataGenerator dataGenerator, ExistingFileHelper fileHelper) {
        super(dataGenerator, fileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(ApocalypseItems.FRAGMENTED_SOUL.get(),
                        Component.translatable(title("root")),
                        Component.translatable(desc("root")),
                        Apocalypse.resourceLoc("textures/gui/advancements/backgrounds/night_sky.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("pass_grace_period", PassedGracePeriodTrigger.TriggerInstance.gracePeriodPassed())
                .save(consumer, Apocalypse.resourceLoc("root"), fileHelper);

        Advancement toasty = Advancement.Builder.advancement()
                .parent(root)
                .display(ApocalypseItems.FATHERLY_TOAST.get(),
                        Component.translatable(title("toasty")),
                        Component.translatable(desc("toasty")),
                        null,
                        FrameType.CHALLENGE, true, true, true)
                .addCriterion("obtain_fatherly_toast", InventoryChangeTrigger.TriggerInstance.hasItems(ApocalypseItems.FATHERLY_TOAST.get()))
                .save(consumer, Apocalypse.resourceLoc("toasty"), fileHelper);

        Advancement lessGrumpy = Advancement.Builder.advancement()
                .parent(toasty)
                .display(Items.COOKIE,
                        Component.translatable(title("less_grumpy")),
                        Component.translatable(desc("less_grumpy")),
                        null,
                        FrameType.TASK, true, true, true)
                .addCriterion("tame_grump", TamedGrumpTrigger.TriggerInstance.tamedGrump())
                .save(consumer, Apocalypse.resourceLoc("less_grumpy"), fileHelper);

        Advancement lunarium = Advancement.Builder.advancement()
                .parent(root)
                .display(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get(),
                        Component.translatable(title("lunarium")),
                        Component.translatable(desc("lunarium")),
                        null,
                        FrameType.TASK, true, true, true)
                .addCriterion("obtain_lunarium", InventoryChangeTrigger.TriggerInstance.hasItems(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()))
                .save(consumer, Apocalypse.resourceLoc("lunarium"), fileHelper);
    }

    private static String title(String advancementName) {
        return Apocalypse.MODID + ".advancements." + advancementName + ".title";
    }

    private static String desc(String advancementName) {
        return Apocalypse.MODID + ".advancements." + advancementName + ".description";
    }
}
