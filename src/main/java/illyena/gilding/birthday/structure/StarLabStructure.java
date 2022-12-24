package illyena.gilding.birthday.structure;

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

public class StarLabStructure  extends Structure {
    /**
     * A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
     * With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    */
     public static final Codec<StarLabStructure> CODEC = RecordCodecBuilder.<StarLabStructure>mapCodec(instance ->
            instance.group(StarLabStructure.configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, StarLabStructure::new)).codec();

    private final RegistryEntry<StructurePool> startPool;
    private final Optional<Identifier> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Type> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public StarLabStructure(Structure.Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size,
                            HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;

    }

    private static StructurePlacementData createPlacementData(StructureTemplateManager manager, Identifier identifier) {
        StructureTemplate structureTemplate = manager.getTemplateOrBlank(identifier);
        BlockPos pos = new BlockPos(structureTemplate.getSize().getX() / 2, 0, structureTemplate.getSize().getZ() / 2);

        BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS;
        return (new StructurePlacementData()).addProcessor(blockIgnoreStructureProcessor).setPosition(pos);
    }

    /** <p>
     * This is where extra checks can be done to determine if the structure can spawn here.
     * This only needs to be overridden if you're adding additional spawn conditions.
     * </p> <p>
     * Fun fact, if you set your structure separation/spacing to be 0/1, you can use
     * extraSpawningChecks to return true only if certain chunk coordinates are passed in which
     * allows you to spawn structures only at certain coordinates in the world.
     * </p> <p>
     * Basically, this method is used for determining if the land is at a suitable height,
     * if certain other structures are too close or not, or some other restrictive condition.
     * </p> <p>
     * For example, Pillager Outposts added a check to make sure it cannot spawn within 10 chunk of a Village.
     * (Bedrock Edition seems to not have the same check)
     * </p> <p>
     * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
     * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
     * Then loop through it and look for land with air above it and set blockpos's Y value to it.
     * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
     * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
     * </p> <p>
     * Also, please for the love of god, do not do dimension checking here.
     * If you do and another mod's dimension is trying to spawn your structure,
     * the locate command will make minecraft hang forever and break the game.
     * Use the biome tags for where to spawn the structure and users can datapack
     * it to spawn in specific biomes that aren't in the dimension they don't like if they wish.
     * </p>
     */
    private static boolean extraSpawningChecks(Structure.Context context) {
        /**
         * Grabs the chunk position we are at
         */

        ChunkPos chunkPos = context.chunkPos();
        HeightLimitView world = context.world();
        /**
         * Checks to make sure our structure does not spawn above land that's higher than y = 150
         * to demonstrate how this method is good for checking extra conditions for spawning
         * @<code> return context.chunkGenerator().getHeightInGround(
        chunkPos.getStartX(),
        chunkPos.getStartZ(),
        Gi_WG,
        context.world(),
        context.noiseConfig()) < 150;</code>
         */


        boolean chunkIsEmpty = true;
        for (int y = world.getBottomY(); y <= world.getTopY(); ++y) {
            GildingInit.LOGGER.warn(context.chunkGenerator().getColumnSample(chunkPos.x, chunkPos.z, world, context.noiseConfig()).getState(y) + " y: " + y);
            if (!context.chunkGenerator().getColumnSample(chunkPos.x, chunkPos.z, world, context.noiseConfig()).getState(y).isOf(Blocks.AIR)) {
                chunkIsEmpty = false;
                break;
            }
        }

        GildingInit.LOGGER.error(chunkPos + "    " + chunkIsEmpty);
        return true;

    }


    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!StarLabStructure.extraSpawningChecks(context)) {
            return Optional.empty();
        }


        // Set's our spawning blockpos's y offset to be 60 blocks up.
        // Since we are going to have heightmap/terrain height spawning set to true further down, this will make it so we spawn 60 blocks above terrain.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG heightmap.
        int startY = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), startY, chunkPos.getStartZ());

        Optional<Structure.StructurePosition> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
                        // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter); // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.

        /**
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> getType() { return BirthdayStructures.STAR_LAB; }


}
