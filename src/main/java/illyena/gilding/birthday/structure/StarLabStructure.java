package illyena.gilding.birthday.structure;
/*
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import illyena.gilding.GildingInit;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static illyena.gilding.birthday.BirthdayInitializer.MOD_ID;

/**
 *
 * @source StructureTutorialMod <a href="http://www.github.com/TelepathicGrunt/StructureTutorialMod"></a>   @author TelepathicGrunt
 */

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import illyena.gilding.GildingInit;
import illyena.gilding.core.util.GildingTags;
import net.minecraft.block.Blocks;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import org.apache.commons.lang3.RandomUtils;

import java.util.Optional;
import java.util.Random;

public class StarLabStructure  extends StructureFeature<StructurePoolFeatureConfig> {
    /**
     * A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
     * With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    */
    public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)

            ).apply(instance, StructurePoolFeatureConfig::new)
    );

    public StarLabStructure() {
        super(CODEC, StarLabStructure::createPieceGenerator, PostPlacementProcessor.EMPTY);


    }

    static boolean extraSpawningChecks(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        ChunkPos chunkPos = context.chunkPos();
        HeightLimitView world = context.world();


        boolean chunkIsEmpty = true;
        for (int y = world.getBottomY(); y <= world.getTopY(); ++y) {
            GildingInit.LOGGER.warn(context.chunkGenerator().getColumnSample(chunkPos.x, chunkPos.z, world).getState(y) + " y: " + y);
            if (!context.chunkGenerator().getColumnSample(chunkPos.x, chunkPos.z, world).getState(y).isOf(Blocks.AIR)) {
                chunkIsEmpty = false;
                break;
            }
        }

        GildingInit.LOGGER.error(chunkPos + "    " + chunkIsEmpty);
        return true;

    }


    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPieceGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        if (!StarLabStructure.extraSpawningChecks(context)) {
            return Optional.empty();
        }

        int startY = RandomUtils.nextInt(0, 256);

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), startY, chunkPos.getStartZ());

        Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        PoolStructurePiece::new,
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        true);

        return structurePiecesGenerator;
    }

}