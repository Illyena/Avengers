package illyena.gilding.avengers.util.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
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

    public AvengersLootTableProvider(FabricDataGenerator dataGenerator) { super(dataGenerator, LootContextTypes.BLOCK); }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {
        lootTables.forEach((block, lootType) ->
                identifierBuilderBiConsumer.accept(new Identifier(MOD_ID, ModelIds.getBlockModelId(block).getPath().replace("block/", "blocks/")),
                        dropsPerLootType(block, lootType)));
    }

    public static void addLootTable(Block block, LootTableTypes lootType) {
        lootTables.put(block, lootType);
    }

    public static LootTable.Builder dropsPerLootType(Block block, LootTableTypes lootType) {
        switch (lootType) {
            case BLOCK -> { return BlockLootTableGenerator.drops(block); }
            case STAR_PORTAL -> { return starPortalDrops(block); }
            default -> { return BlockLootTableGenerator.dropsNothing(); }
        }
    }

    public static LootTable.Builder starPortalDrops(Block drop) {
        return LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(drop)
                                .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                        .withOperation("ExitPortal", "BlockEntityTag.ExitPortal"))
                                .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                        .withOperation("ExactTeleport", "BlockEntityTag.ExactTeleport"))));
    }

    public enum LootTableTypes {
        DROPS_NOTHING,
        BLOCK,
        STAR_PORTAL;

        LootTableTypes () { }

    }

}
