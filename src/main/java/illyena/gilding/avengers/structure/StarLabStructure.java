package illyena.gilding.avengers.structure;

/**
 * @source StructureTutorialMod <a href="http://www.github.com/TelepathicGrunt/StructureTutorialMod"></a>   @author TelepathicGrunt
 */

import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.commons.lang3.RandomUtils;

import java.util.Optional;

public class StarLabStructure  extends StructureFeature<StructurePoolFeatureConfig> {
    public StarLabStructure() { super(StructurePoolFeatureConfig.CODEC, StarLabStructure::createPieceGenerator, PostPlacementProcessor.EMPTY); }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPieceGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        int startY = RandomUtils.nextInt(context.world().getBottomY(), context.world().getTopY() - 30);
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), startY, chunkPos.getStartZ());

        return StructurePoolBasedGenerator.generate(context, PoolStructurePiece::new, blockPos, false,true);
    }

}