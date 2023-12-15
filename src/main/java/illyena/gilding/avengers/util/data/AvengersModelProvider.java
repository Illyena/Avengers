package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.StarPortalBlock;
import illyena.gilding.avengers.block.TeleportAnchorBlock;
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
    private static final TextureKey OVERLAY = TextureKey.of("overlay");

    public AvengersModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        modelList.forEach(block -> registerBlockModels(blockStateModelGenerator, block));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) { }

    public static void registerBlockModels(BlockStateModelGenerator modelGenerator, Block block) {
        if (block instanceof StarPortalBlock starPortalBlock) {
            registerStarPortal(modelGenerator, starPortalBlock);
        } else if (block instanceof TeleportAnchorBlock) {
            registerTeleportAnchor(modelGenerator, block);
        } else if (block instanceof MjolnirBlock) {
            registerBreakableBlock(modelGenerator, block);
        } else {
            modelGenerator.registerSimpleCubeAll(block);
        }
    }

    public static void addModels(Block block ) { modelList.add(block); }

    public static void registerBreakableBlock(BlockStateModelGenerator modelGenerator, Block block) {
        Identifier blockBrokenId = ModelIds.getBlockSubModelId(block, "_broken");
        modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(MjolnirBlock.BROKEN, blockBrokenId, ModelIds.getBlockModelId(block))));
        Model brokenModel = new Model(Optional.of(ModelIds.getBlockModelId(block)), Optional.of("_broken"), OVERLAY, TextureKey.TEXTURE);
        brokenModel.upload(blockBrokenId, new TextureMap().put(OVERLAY, blockBrokenId.withSuffixedPath("_underlay")).put(TextureKey.TEXTURE, blockBrokenId), modelGenerator.modelCollector);

        modelGenerator.excludeFromSimpleItemModelGeneration(block);
        modelGenerator.modelCollector.accept(ModelIds.getItemModelId(block.asItem()).withSuffixedPath("_broken"), new SimpleModelSupplier(blockBrokenId));
    }

    public static void registerStarPortal(BlockStateModelGenerator modelGenerator, StarPortalBlock block) {
        String color = block.getColor() == null ? "" : block.getColor().asString() + "_";
        Identifier textureId = new Identifier("minecraft", "block/" + color + "shulker_box");
        Identifier blockModelId = Models.PARTICLE.upload(block, new TextureMap().put(TextureKey.PARTICLE, textureId), modelGenerator.modelCollector);
        modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, blockModelId));

        Model itemModel = new Model(Optional.of(new Identifier(MOD_ID, "item/star_portal_inventory")), Optional.empty(), TextureKey.LAYER0);
        itemModel.upload(ModelIds.getItemModelId(block.asItem()), new TextureMap().put(TextureKey.LAYER0, textureId), modelGenerator.modelCollector);
    }

    public static void registerTeleportAnchor(BlockStateModelGenerator modelGenerator, Block block) {
        Identifier textureId = new Identifier(MOD_ID, "block/end_portal");
        Identifier blockModelId = Models.PARTICLE.upload(block, new TextureMap().put(TextureKey.PARTICLE, textureId), modelGenerator.modelCollector);
        modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, blockModelId));
        Models.CUBE_ALL.upload(ModelIds.getItemModelId(block.asItem()), new TextureMap().put(TextureKey.ALL, textureId), modelGenerator.modelCollector);
    }
}
