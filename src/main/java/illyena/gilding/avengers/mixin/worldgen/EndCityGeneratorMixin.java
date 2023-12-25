package illyena.gilding.avengers.mixin.worldgen;

import illyena.gilding.mixin.worldgen.SimpleStructurePieceAccessor;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Random;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Mixin(EndCityGenerator.class)
public abstract class EndCityGeneratorMixin {
    @Unique private static final int buildHeight = 255;

    @Shadow static EndCityGenerator.Piece createPiece(StructureManager structureTemplateManager, EndCityGenerator.Piece lastPiece, BlockPos relativePosition, String template, BlockRotation rotation, boolean ignoreAir) { return null; }
    @Shadow static EndCityGenerator.Piece addPiece(List<StructurePiece> pieces, EndCityGenerator.Piece piece) { return null; }
    @Shadow static boolean createPart(StructureManager manager, EndCityGenerator.Part piece, int depth, EndCityGenerator.Piece parent, BlockPos pos, List<StructurePiece> pieces, Random random) { return false; }

    @Inject(method = "addPieces", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onAddPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random, CallbackInfo ci, EndCityGenerator.Piece piece) {
        PLATFORM.init();
        pieces.stream().map(structurePiece -> (EndCityGenerator.Piece) structurePiece)
                .filter(structurePiece -> ((SimpleStructurePieceAccessor)structurePiece).callGetId().getPath().contains("fat_tower_top"))
                .findFirst().ifPresent(fatTowerTopPiece -> createPart(manager, PLATFORM, 7, fatTowerTopPiece, null, pieces, random));
    }

    @Unique
    private static final EndCityGenerator.Part PLATFORM = new EndCityGenerator.Part() {
        public boolean platformGenerated;

        public void init() { this.platformGenerated = false; }

        public boolean create(StructureManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation blockRotation = ((SimpleStructurePieceAccessor)root).getPlacementData().getRotation();
            if (!this.platformGenerated && random.nextBoolean()) {
                addPiece(pieces, createPiece(manager, root, new BlockPos(0, (buildHeight - root.getCenter().getY()) / 2, 0), "star_platform", blockRotation, true));
                this.platformGenerated = true;
                return true;
            }
            return false;
        }
    };

}
