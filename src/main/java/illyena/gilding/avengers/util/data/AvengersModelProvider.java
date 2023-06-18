package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.StarPortalBlock;
import illyena.gilding.avengers.block.TeleportAnchorBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersModelProvider extends FabricModelProvider {
    static List<Block> modelList = new ArrayList<>();

    static Model STAR_PORTAL_BLOCK_MODEL = new Model(Optional.empty(), Optional.empty(), TextureKey.PARTICLE);
    static Model STAR_PORTAL_ITEM_MODEL = new Model(Optional.of(new Identifier(MOD_ID, "item/star_portal_inventory")), Optional.empty(), TextureKey.LAYER0);
    static Model TELEPORT_ANCHOR_BLOCK_MODEL = new Model(Optional.empty(), Optional.empty(), TextureKey.PARTICLE);
    static Model TELEPORT_ANCHOR_ITEM_MODEL = new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), TextureKey.ALL);

    public AvengersModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        modelList.forEach((block) -> registerBlockModels(blockStateModelGenerator, block));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) { }

    public static void registerBlockModels(BlockStateModelGenerator modelGenerator, Block block) {
        if (block instanceof StarPortalBlock) {
            registerStarPortal(modelGenerator, block);
        } else if (block instanceof TeleportAnchorBlock) {
            registerTeleportAnchor(modelGenerator, block);
        } else if (block instanceof MjolnirBlock){
            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block))));
            modelGenerator.excludeFromSimpleItemModelGeneration(block);
        } else {
            modelGenerator.registerSimpleCubeAll(block);
        }
    }

    public static void addModels(Block block ) { modelList.add(block); }

    public static void registerStarPortal(BlockStateModelGenerator modelGenerator, Block block) {
        String color = ModelIds.getBlockModelId(block).getPath().replace("block/star_portal_block_", "");
        Identifier blockModelId = block != AvengersBlocks.STAR_PORTAL_BLOCK ? new Identifier("minecraft", "block/" + color + "_shulker_box") : new Identifier("minecraft", "block/shulker_box");
        Identifier itemModelId = block != AvengersBlocks.STAR_PORTAL_BLOCK ? new Identifier("minecraft", "entity/shulker/shulker_" + color) : new Identifier("minecraft", "entity/shulker/shulker");

        Identifier identifier = STAR_PORTAL_BLOCK_MODEL.upload(block, new TextureMap().put(TextureKey.PARTICLE, blockModelId), modelGenerator.modelCollector);
        modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, identifier));
        STAR_PORTAL_ITEM_MODEL.upload(new Identifier(MOD_ID, ModelIds.getBlockModelId(block).getPath().replace("block/", "item/")), new TextureMap().put(TextureKey.LAYER0, itemModelId), modelGenerator.modelCollector);
    }

    public static void registerTeleportAnchor(BlockStateModelGenerator modelGenerator, Block block) {
        Identifier blockModelId = new Identifier("minecraft", "entity/end_portal");
        Identifier identifier = TELEPORT_ANCHOR_BLOCK_MODEL.upload(block, new TextureMap().put(TextureKey.PARTICLE, blockModelId), modelGenerator.modelCollector);
        modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, identifier));
        TELEPORT_ANCHOR_ITEM_MODEL.upload(new Identifier(MOD_ID, ModelIds.getBlockModelId(block).getPath().replace("block/", "item/")), new TextureMap().put(TextureKey.ALL, blockModelId), modelGenerator.modelCollector);
    }
}
