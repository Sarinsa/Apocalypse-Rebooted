package com.toast.apocalypse.common.triggers;

import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Grump;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;


public class TamedGrumpTrigger extends SimpleCriterionTrigger<TamedGrumpTrigger.TriggerInstance> {

    private static final ResourceLocation ID = Apocalypse.resourceLoc("tame_grump");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public TamedGrumpTrigger.TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext context) {
        EntityPredicate.Composite composite2 = EntityPredicate.Composite.fromJson(jsonObject, "entity", context);
        return new TamedGrumpTrigger.TriggerInstance(composite, composite2);
    }

    public void trigger(ServerPlayer player, Grump grump) {
        LootContext lootContext = EntityPredicate.createContext(player, grump);
        this.trigger(player, (instance) -> instance.matches(lootContext));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite entity;

        public TriggerInstance(EntityPredicate.Composite predicate1, EntityPredicate.Composite predicate2) {
            super(TamedGrumpTrigger.ID, predicate1);
            entity = predicate2;
        }

        public static TamedGrumpTrigger.TriggerInstance tamedGrump() {
            return new TamedGrumpTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(ApocalypseEntities.GRUMP.get()).build()));
        }

        public boolean matches(LootContext lootContext) {
            return entity.matches(lootContext);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.add("entity", entity.toJson(context));
            return jsonObject;
        }
    }
}
