package illyena.gilding.avengers.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class RicochetAndReturnCriterion extends AbstractCriterion<RicochetAndReturnCriterion.Conditions> {
    static final Identifier ID = new Identifier(MOD_ID, "ricochet_and_return");

    public RicochetAndReturnCriterion() { }

    public Identifier getId() { return ID; }

    public Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        LootContextPredicate[] victims = EntityPredicate.contextPredicateArrayFromJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entities"));
        DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("damage_source"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("projectile"));
        return new Conditions(playerPredicate, itemPredicate, damageSourcePredicate, intRange, victims);
    }

    public void trigger(ServerPlayerEntity player, ItemStack item, DamageSource damageSource, Collection<Entity> ricochetHitList) {
        List<LootContext> list = new ArrayList<>();
        Set<Entity> set = Sets.newHashSet();

        for (Entity entity : ricochetHitList) {
            set.add(entity);
            list.add(EntityPredicate.createAdvancementEntityLootContext(player, entity));
        }

        this.trigger(player, conditions -> conditions.matches(player, item, damageSource, set.size(), list));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final LootContextPredicate[] victims;
        private final DamageSourcePredicate damageSourcePredicate;
        private final NumberRange.IntRange uniqueEntities;

        private final ItemPredicate item;

        public Conditions(LootContextPredicate player, ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange uniqueEntities, LootContextPredicate[] victims) {
            super(ID, player);
            this.victims = victims;
            this.item = itemPredicate;
            this.damageSourcePredicate = damageSourcePredicate;
            this.uniqueEntities = uniqueEntities;

        }

        public static Conditions create() {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY, entityCount, victims);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicate, NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicate,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicate,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(),  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicate, entityCount, victims);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(), entityCount, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY, entityCount, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY, entityCount, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicate, NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate, NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicate,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicate,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate,  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(),  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(),  entityCount, new LootContextPredicate[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicate, entityCount, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate, entityCount, victims);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(), entityCount, victims);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            LootContextPredicate[] victims = new LootContextPredicate[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.asLootContextPredicate(builder.build());
            }
            return new Conditions(LootContextPredicate.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(), entityCount, victims);
        }

        public boolean matches(ServerPlayerEntity player, ItemStack itemStack, DamageSource damageSource, int entityCount, Collection<LootContext> victimContexts) {
            if (this.victims.length > 0) {
                List<LootContext> list = Lists.newArrayList(victimContexts);
                for (LootContextPredicate lootContextPredicate : this.victims) {
                    boolean bool = false;
                    Iterator<LootContext> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LootContext lootContext = iterator.next();
                        if (lootContextPredicate.test(lootContext)) {
                            iterator.remove();
                            bool = true;
                            break;
                        }
                    }
                    if (!bool) {
                        return false;
                    }
                }
            }
            return this.uniqueEntities.test(entityCount)
                    && this.damageSourcePredicate.test(player, damageSource)
                    && this.item.test(itemStack);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("victims", LootContextPredicate.toPredicatesJsonArray(this.victims, predicateSerializer));
            jsonObject.add("unique_entities", this.uniqueEntities.toJson());
            jsonObject.add("damage_source", this.damageSourcePredicate.toJson());
            jsonObject.add("projectile", this.item.toJson());
            return jsonObject;
        }

    }

}
