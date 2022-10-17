package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.core.register.ApocalypseLootMods;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ApocalypseLootModProvider extends GlobalLootModifierProvider {

    public ApocalypseLootModProvider(DataGenerator gen) {
        super(gen, Apocalypse.MODID);
    }

    @Override
    protected void start() {
        add("fatherly_toast", ApocalypseLootMods.SIMPLE_ADD_LOOT_MOD.get(), new SimpleAddLootModifier(
                new ILootCondition[]{},
                new ResourceLocation[] {
                        new ResourceLocation("chests/simple_dungeon"),
                        new ResourceLocation("chests/desert_pyramid"),
                        new ResourceLocation("chests/jungle_temple"),
                        new ResourceLocation("chests/abandoned_mineshaft")
                },
                ApocalypseItems.FATHERLY_TOAST,
                0.3D,
                1,
                4
        ));
    }
}
