package com.toast.apocalypse.datagen.advancement;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.triggers.PassedGracePeriodTrigger;
import com.toast.apocalypse.common.triggers.TamedGrumpTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// TODO - More advancements? Maybe we could make some difficult challenges?
public class ApocalypseAdvancementProvider extends ForgeAdvancementProvider {
    
    public ApocalypseAdvancementProvider( DataGenerator dataGen, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper ) {
        super( dataGen.getPackOutput(), lookupProvider, fileHelper, List.of( new AdvancementGen() ) );
    }
    
    private static class AdvancementGen implements ForgeAdvancementProvider.AdvancementGenerator {
        
        @Override
        public void generate( HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper ) {
            Advancement root = Advancement.Builder.advancement()
                    .display( ApocalypseObjects.Items.FRAGMENTED_SOUL.get(),
                            title( "root", "The Apocalypse" ),
                            desc( "root", "The grace period is over. Stay on your toes!" ),
                            Apocalypse.rl( "textures/gui/advancements/backgrounds/night_sky.png" ),
                            FrameType.TASK, true, true, false )
                    .addCriterion( "pass_grace_period", PassedGracePeriodTrigger.TriggerInstance.gracePeriodPassed() )
                    .save( saver, Apocalypse.rl( "root" ), existingFileHelper );
            
            Advancement toasty = Advancement.Builder.advancement()
                    .parent( root )
                    .display( ApocalypseObjects.Items.FATHERLY_TOAST.get(),
                            title( "toasty", "Secret Snack" ),
                            desc( "toasty", "Obtain some Fatherly Toast. Quite toasty... Grumps likes cookies, so maybe..?" ),
                            null,
                            FrameType.CHALLENGE, true, true, true )
                    .addCriterion( "obtain_fatherly_toast", InventoryChangeTrigger.TriggerInstance.hasItems( ApocalypseObjects.Items.FATHERLY_TOAST.get() ) )
                    .save( saver, Apocalypse.rl( "toasty" ), existingFileHelper );
            
            Advancement.Builder.advancement()
                    .parent( toasty )
                    .display( Items.COOKIE,
                            title( "less_grumpy", "Slightly Less Grumpy" ),
                            desc( "less_grumpy", "Tame a Grump with a scrumptious slice of Fatherly Toast!" ),
                            null,
                            FrameType.TASK, true, true, true )
                    .addCriterion( "tame_grump", TamedGrumpTrigger.TriggerInstance.tamedGrump() )
                    .save( saver, Apocalypse.rl( "less_grumpy" ), existingFileHelper );
            
            Advancement.Builder.advancement()
                    .parent( root )
                    .display( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT.get(),
                            title( "midnight_steel", "Moon Alloy" ),
                            desc( "midnight_steel", "Craft a Midnight Steel Ingot from an Iron Ingot and a Fragmented Soul." ),
                            null,
                            FrameType.TASK, true, true, true )
                    .addCriterion( "obtain_midnight_steel", InventoryChangeTrigger.TriggerInstance.hasItems( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT.get() ) )
                    .save( saver, Apocalypse.rl( "midnight_steel" ), existingFileHelper );
        }
        
        /** @return A title component from the given sub-key and translation string. */
        private static Component title( String subKey, String translation ) {
            return Apocalypse.tk( Apocalypse.MOD_ID + ".advancements." + subKey + ".title", translation ).get();
        }
        
        /** @return A description component from the given sub-key and translation string. */
        private static Component desc( String subKey, String translation ) {
            return Apocalypse.tk( Apocalypse.MOD_ID + ".advancements." + subKey + ".description", translation ).get();
        }
    }
}
