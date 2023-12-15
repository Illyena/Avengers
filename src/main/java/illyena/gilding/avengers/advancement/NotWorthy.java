package illyena.gilding.avengers.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class NotWorthy extends AbstractCriterion<NotWorthy.Conditions> {
    static final Identifier ID = new Identifier(MOD_ID, "not_worthy");

    public Identifier getId() { return ID; }

    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate player, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(obj.get("item"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(obj.get("distance"));
        return new Conditions(player, itemPredicate, distancePredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack itemStack, BlockPos blockPos) {
        this.trigger(player, conditions -> conditions.matches(player, itemStack, blockPos));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate itemPredicate;
        private final DistancePredicate distancePredicate;

        public Conditions(LootContextPredicate player, ItemPredicate itemPredicate, DistancePredicate distancePredicate) {
            super(ID, player);
            this.itemPredicate = itemPredicate;
            this.distancePredicate = distancePredicate;
        }

        public static Conditions create() {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, DistancePredicate.ANY);
        }
        //todo

        public boolean matches(ServerPlayerEntity player, ItemStack itemStack, BlockPos blockPos) {
            return this.itemPredicate.test(itemStack) && this.distancePredicate.test(player.getX(), player.getY(), player.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.itemPredicate.toJson());
            jsonObject.add("distance", this.distancePredicate.toJson());
            return jsonObject;
        }
    }



}
