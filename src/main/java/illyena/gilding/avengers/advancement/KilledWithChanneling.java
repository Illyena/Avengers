package illyena.gilding.avengers.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KilledWithChanneling extends AbstractCriterion<KilledWithChanneling.Conditions> {
    static final Identifier ID = new Identifier("killed_with_channeling");

    public KilledWithChanneling() { }

    public Identifier getId() { return ID; }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityPredicate.Extended[] extendeds = EntityPredicate.Extended.requireInJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
        return new Conditions(extended, extendeds);
    }

    public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
        List<LootContext> list = victims.stream().map((entity) -> EntityPredicate.createAdvancementEntityLootContext(player, entity)).collect(Collectors.toList());
        this.trigger(player, (conditions) -> conditions.matches(player, list));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate.Extended[] victims;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended[] victims) {
            super(ID, player);
            this.victims = victims;
        }

        public static Conditions create(EntityPredicate... victims) {
            return new Conditions(EntityPredicate.Extended.EMPTY, Stream.of(victims).map(EntityPredicate.Extended::ofLegacy).toArray(EntityPredicate.Extended[]::new));
        }

        public boolean matches(ServerPlayerEntity player, Collection<? extends LootContext> victims) {
            if (player.isOnGround()) {
                return false;
            } else {
                EntityPredicate.Extended[] var2 = this.victims;
                for (EntityPredicate.Extended extended : var2) {
                    boolean bl = false;
                    for (LootContext lootContext : victims) {
                        if (extended.test(lootContext)) {
                            bl = true;
                            break;
                        }
                    }
                    if (!bl) {
                        return false;
                    }
                }
                return true;
            }
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, predicateSerializer));
            return jsonObject;
        }
    }

}
