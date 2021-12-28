package com.toast.apocalypse.common.triggers;

import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class PassedGracePeriodTrigger extends AbstractCriterionTrigger<PassedGracePeriodTrigger.Instance> {

    private static final ResourceLocation ID = Apocalypse.resourceLoc("passed_grace_period");

    public ResourceLocation getId() {
        return ID;
    }

    public PassedGracePeriodTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate predicate, ConditionArrayParser parser) {
        long currentDifficulty = jsonObject.has("currentDifficulty") ? JSONUtils.getAsLong(jsonObject, "currentDifficulty") : 0L;
        return new PassedGracePeriodTrigger.Instance(predicate, currentDifficulty);
    }

    public void trigger(ServerPlayerEntity player, long currentDifficulty) {
        this.trigger(player, (instance) -> instance.matches(currentDifficulty));
    }

    public static class Instance extends CriterionInstance {

        private final long currentDifficulty;

        public Instance(EntityPredicate.AndPredicate predicate, long currentDifficulty) {
            super(PassedGracePeriodTrigger.ID, predicate);
            this.currentDifficulty = currentDifficulty;
        }

        public static PassedGracePeriodTrigger.Instance gracePeriodPassed() {
            return new PassedGracePeriodTrigger.Instance(EntityPredicate.AndPredicate.ANY, 0L);
        }

        public boolean matches(long currentDifficulty) {
            return currentDifficulty >= 0L;
        }

        public JsonObject serializeToJson(ConditionArraySerializer serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);

            jsonObject.addProperty("currentDifficulty", this.currentDifficulty);

            return jsonObject;
        }
    }
}
