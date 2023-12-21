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


public class RicochetAndReturn extends AbstractCriterion<RicochetAndReturn.Conditions> {
    static final Identifier ID = new Identifier(MOD_ID, "ricochet_and_return");

    public RicochetAndReturn() { }

    public Identifier getId() { return ID; }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extendedPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityPredicate.Extended[] victims = EntityPredicate.Extended.requireInJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entities"));
        DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("damage_source"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("projectile"));
        return new Conditions(extendedPredicate, itemPredicate, damageSourcePredicate, intRange, victims);
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
        private final EntityPredicate.Extended[] victims;
        private final DamageSourcePredicate damageSourcePredicate;
        private final NumberRange.IntRange uniqueEntities;

        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange uniqueEntities, EntityPredicate.Extended[] victims) {
            super(ID, player);
            this.victims = victims;
            this.item = itemPredicate;
            this.damageSourcePredicate = damageSourcePredicate;
            this.uniqueEntities = uniqueEntities;

        }

        public static Conditions create() {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] victims = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] victims = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, DamageSourcePredicate.EMPTY, entityCount, victims);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicate, NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] victims = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                victims[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicate,  NumberRange.IntRange.ANY, victims);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicate,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(),  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicate, entityCount, lootContextPredicates);
        }

        public static Conditions create(DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, damageSourcePredicateBuilder.build(), entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY,  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, DamageSourcePredicate.EMPTY, entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), DamageSourcePredicate.EMPTY, entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicate, NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate, NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(), NumberRange.IntRange.ANY, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicate,  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate,  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(),  NumberRange.IntRange.ANY, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicate,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate,  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(),  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount) {
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(),  entityCount, new EntityPredicate.Extended[0]);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicate, entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate damageSourcePredicate, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicate, entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate itemPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicate, damageSourcePredicateBuilder.build(), entityCount, lootContextPredicates);
        }

        public static Conditions create(ItemPredicate.Builder itemPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder, NumberRange.IntRange entityCount, EntityPredicate.Builder... victimPredicates) {
            EntityPredicate.Extended[] lootContextPredicates = new EntityPredicate.Extended[victimPredicates.length];
            for(int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder builder = victimPredicates[i];
                lootContextPredicates[i] = EntityPredicate.Extended.ofLegacy(builder.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, itemPredicateBuilder.build(), damageSourcePredicateBuilder.build(), entityCount, lootContextPredicates);
        }

        public boolean matches(ServerPlayerEntity player, ItemStack itemStack, DamageSource damageSource, int entityCount, Collection<LootContext> victimContexts) {
            if (this.victims.length > 0) {
                List<LootContext> list = Lists.newArrayList(victimContexts);
                for (EntityPredicate.Extended lootContextPredicate : this.victims) {
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
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, predicateSerializer));
            jsonObject.add("unique_entities", this.uniqueEntities.toJson());
            jsonObject.add("damage_source", this.damageSourcePredicate.toJson());
            jsonObject.add("projectile", this.item.toJson());
            return jsonObject;
        }
    }

}