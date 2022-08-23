package com.toast.apocalypse.common.triggers;

import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class TamedGrumpTrigger extends AbstractCriterionTrigger<TamedGrumpTrigger.Instance> {

    private static final ResourceLocation ID = Apocalypse.resourceLoc("tame_grump");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public TamedGrumpTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate predicate1, ConditionArrayParser conditionParser) {
        EntityPredicate.AndPredicate predicate2 = EntityPredicate.AndPredicate.fromJson(jsonObject, "entity", conditionParser);
        return new TamedGrumpTrigger.Instance(predicate1, predicate2);
    }

    public void trigger(ServerPlayerEntity player, GrumpEntity grump) {
        LootContext lootContext = EntityPredicate.createContext(player, grump);
        this.trigger(player, (instance) -> instance.matches(lootContext));
    }

    public static class Instance extends CriterionInstance {
        private final EntityPredicate.AndPredicate entity;

        public Instance(EntityPredicate.AndPredicate predicate1, EntityPredicate.AndPredicate predicate2) {
            super(TamedGrumpTrigger.ID, predicate1);
            this.entity = predicate2;
        }

        public static TamedGrumpTrigger.Instance tamedGrump() {
            return new TamedGrumpTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(EntityPredicate.Builder.entity().of(ApocalypseEntities.GRUMP.get()).build()));
        }

        public boolean matches(LootContext lootContext) {
            return this.entity.matches(lootContext);
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);
            jsonObject.add("entity", this.entity.toJson(serializer));
            return jsonObject;
        }
    }
}
