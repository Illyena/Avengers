package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.StarPortalBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersLootTableProvider extends SimpleFabricLootTableProvider {
    public static Map<Block, LootTableTypes> lootTables = new HashMap<>();

    public AvengersLootTableProvider(FabricDataOutput output) { super(output, LootContextTypes.BLOCK); }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {
        lootTables.forEach((block, lootType) ->
                identifierBuilderBiConsumer.accept(new Identifier(MOD_ID, ModelIds.getBlockModelId(block).getPath().replace("block/", "blocks/")),
                        dropsPerLootType(block, lootType)));
    }

    public static void addLootTable(Block block, LootTableTypes lootType) { lootTables.put(block, lootType); }

    public static LootTable.Builder dropsPerLootType(Block block, LootTableTypes lootType) {
        switch (lootType) {
            case BLOCK -> { return BlockLootTableGenerator.drops(block, SurvivesExplosionLootCondition.builder(), LootTableEntry.builder(block.getLootTableId())); }
            case BLOCK_ENTITY -> { return blockEntityDrops(block); }
            default -> { return BlockLootTableGenerator.dropsNothing(); }
        }
    }

    public static LootTable.Builder blockEntityDrops(Block block) {
        return LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(block)
                                .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                                .apply(blockEntityNbtLootFunction(block))));
    }

    private static CopyNbtLootFunction.Builder blockEntityNbtLootFunction(Block block) {
        if (block instanceof MjolnirBlock) {
            return CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    .withOperation("Damage", "Damage")
                    .withOperation("Enchantments", "Enchantments");
        } else if (block instanceof StarPortalBlock) {
            return CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    .withOperation("ExitPortal", "BlockEntityTag.ExitPortal")
                    .withOperation("ExactTeleport", "BlockEntityTag.ExactTeleport");
        } else return CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                .getThisConditionConsumingBuilder();
    }

    public enum LootTableTypes {
        DROPS_NOTHING,
        BLOCK,
        BLOCK_ENTITY;

        LootTableTypes () { }

    }

}
