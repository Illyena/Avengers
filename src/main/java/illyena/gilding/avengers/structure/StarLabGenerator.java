package illyena.gilding.avengers.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class StarLabGenerator {
    public static final RegistryEntry<StructurePool> STRUCTURE_POOLS =
            StructurePools.register(new StructurePool(new Identifier(MOD_ID, "star_lab/starts"), new Identifier("empty"),
                    ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("star_lab/platform", StructureProcessorLists.EMPTY), 1)), StructurePool.Projection.RIGID));

}
