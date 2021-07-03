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

public class DifficultyChangeTrigger extends AbstractCriterionTrigger<DifficultyChangeTrigger.Instance> {

    private static final ResourceLocation ID = Apocalypse.resourceLoc("changed_difficulty");

    public ResourceLocation getId() {
        return ID;
    }

    public DifficultyChangeTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate predicate, ConditionArrayParser p_230241_3_) {
        long currentDifficulty = jsonObject.has("currentDifficulty") ? JSONUtils.getAsLong(jsonObject, "currentDifficulty") : 0L;
        long targetDifficulty = jsonObject.has("targetDifficulty") ? JSONUtils.getAsLong(jsonObject, "targetDifficulty") : 0L;

        return new DifficultyChangeTrigger.Instance(predicate, currentDifficulty, targetDifficulty);
    }

    public void trigger(ServerPlayerEntity player, long currentDifficulty, long targetDifficulty) {
        this.trigger(player, (instance) -> instance.matches(currentDifficulty, targetDifficulty));
    }

    public static class Instance extends CriterionInstance {

        private final long currentDifficulty;
        private final long targetDifficulty;

        public Instance(EntityPredicate.AndPredicate predicate, long currentDifficulty, long targetDifficulty) {
            super(DifficultyChangeTrigger.ID, predicate);
            this.currentDifficulty = currentDifficulty;
            this.targetDifficulty = targetDifficulty;
        }

        public static DifficultyChangeTrigger.Instance difficultyGreaterOrEqual(long targetDifficulty) {
            return new DifficultyChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, 0L, targetDifficulty);
        }

        public boolean matches(long currentDifficulty, long targetDifficulty) {
            return currentDifficulty >= targetDifficulty;
        }

        public JsonObject serializeToJson(ConditionArraySerializer serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);

            jsonObject.addProperty("currentDifficulty", this.currentDifficulty);
            jsonObject.addProperty("targetDifficulty", this.targetDifficulty);

            return jsonObject;
        }
    }
}
