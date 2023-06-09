package illyena.gilding.avengers.mixin.worldgen;

import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;


@Mixin(EndCityGenerator.class)
public abstract class EndCityGeneratorMixin {
    private static final int buildHeight = 255;

    @Mutable
    @Shadow @Final
    static EndCityGenerator.Part FAT_TOWER;

    @Shadow @Final
    static EndCityGenerator.Part BRIDGE_PIECE;

    @Shadow @Final
    static List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS;

    @Shadow
    static EndCityGenerator.Piece createPiece(StructureTemplateManager structureTemplateManager, EndCityGenerator.Piece lastPiece, BlockPos relativePosition, String template, BlockRotation rotation, boolean ignoreAir) {
        return null;
    }

    @Shadow
    static EndCityGenerator.Piece addPiece(List<StructurePiece> pieces, EndCityGenerator.Piece piece) {
        return null;
    }

    @Shadow
    static boolean createPart(StructureTemplateManager manager, EndCityGenerator.Part piece, int depth, EndCityGenerator.Piece parent, BlockPos pos, List<StructurePiece> pieces, Random random) {
        return false;
    }



/*
    private static final EndCityGenerator.Part STAR_PLATFORM = new EndCityGenerator.Part() {
        public void init() {
        }

        public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation blockRotation = root.method_41626().getRotation();
            EndCityGenerator.Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(3 + random.nextInt(2), 25, 3 + random.nextInt(2)), "star_lab", blockRotation, true));
            return true;
        }
    };
*/


    static {
        FAT_TOWER = new EndCityGenerator.Part() {
            public boolean starPlatformGenerated;
            public void init() {
                this.starPlatformGenerated = false;
            }

            public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
                BlockRotation blockRotation = root.method_41626().getRotation();
                EndCityGenerator.Piece piece = addPiece(pieces, createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true));
                piece = addPiece(pieces, createPiece(manager, piece, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true));

                for(int i = 0; i < 2 && random.nextInt(3) != 0; ++i) {
                    piece = addPiece(pieces, createPiece(manager, piece, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true));

                    for (Pair<BlockRotation, BlockPos> fatTowerBridgeAttachment : FAT_TOWER_BRIDGE_ATTACHMENTS) {
                        if (random.nextBoolean()) {
                            EndCityGenerator.Piece piece2 = addPiece(pieces, createPiece(manager, piece, fatTowerBridgeAttachment.getRight(), "bridge_end", blockRotation.rotate(fatTowerBridgeAttachment.getLeft()), true));
                            createPart(manager, BRIDGE_PIECE, depth + 1, piece2, null, pieces, random);
                        }
                    }
                }

                addPiece(pieces, createPiece(manager, piece, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true));

                if (!this.starPlatformGenerated && random.nextInt(2) == 0) {
                    int height = buildHeight - piece.getCenter().getY();
                    this.starPlatformGenerated = true;
                    addPiece(pieces, createPiece(manager, piece, new BlockPos(0, height, 0), "star_platform", blockRotation, true));
                }

                return true;
            }
        };
    }

} //todo UNSAFE OVERWRITE

