package illyena.gilding.mixin.birthday.worldgen;

import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleStructurePiece.class)
public interface SimpleStructurePieceAccessor {
    @Accessor
    Structure getStructure();

    @Accessor
    StructurePlacementData getPlacementData();

    @Accessor
    BlockPos getPos();

}
