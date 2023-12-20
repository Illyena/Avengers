package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.StarPortalBlock;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersBlockTagGenerator extends FabricTagProvider<Block> {
    public static final TagKey<Block> MAGIC_MINEABLE        = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "mineable/magic"));
    public static final TagKey<Block> NEEDS_TOOL_LEVEL_5    = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "needs_tool_level_5"));

    public static final TagKey<Block> DRAGON_IMMUNE                 = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "dragon_immune"));
    public static final TagKey<Block> WITHER_IMMUNE                 = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "wither_immune"));
    public static final TagKey<Block> PORTALS                       = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "portals"));
    public static final TagKey<Block> SHULKER_BOXES                 = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "shulker_boxes"));
    public static final TagKey<Block> HOGLIN_REPELLENTS             = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "hoglin_repellents"));
    public static final TagKey<Block> GUARDED_BY_PIGLINS            = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "guarded_by_piglins"));
    public static final TagKey<Block> OCCLUDES_VIBRATION_SIGNALS    = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "occludes_vibration_signals"));
    public static final TagKey<Block> FEATURES_CANNOT_REPLACE       = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "features_cannot_replace"));
    public static final TagKey<Block> GEODE_INVALID_BLOCKS          = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "geode_invalid_blocks"));

    public AvengersBlockTagGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.BLOCK);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(GildingBlockTagGenerator.MAGIC_MINEABLE).addOptionalTag(MAGIC_MINEABLE);
        getOrCreateTagBuilder(MAGIC_MINEABLE).add(AvengersBlocks.MJOLNIR_BLOCK);
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(MAGIC_MINEABLE).add(block));
        getOrCreateTagBuilder(GildingBlockTagGenerator.NEEDS_TOOL_LEVEL_5).addOptionalTag(NEEDS_TOOL_LEVEL_5);
        getOrCreateTagBuilder(NEEDS_TOOL_LEVEL_5).add(AvengersBlocks.MJOLNIR_BLOCK);
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(NEEDS_TOOL_LEVEL_5).add(block));

        getOrCreateTagBuilder(GildingBlockTagGenerator.DRAGON_IMMUNE).addOptionalTag(DRAGON_IMMUNE);
        getOrCreateTagBuilder(GildingBlockTagGenerator.WITHER_IMMUNE).addOptionalTag(WITHER_IMMUNE);
        getOrCreateTagBuilder(GildingBlockTagGenerator.PORTALS).addOptionalTag(PORTALS);
        getOrCreateTagBuilder(GildingBlockTagGenerator.SHULKER_BOXES).addOptionalTag(SHULKER_BOXES);
        getOrCreateTagBuilder(GildingBlockTagGenerator.HOGLIN_REPELLENTS).addOptionalTag(HOGLIN_REPELLENTS);
        getOrCreateTagBuilder(GildingBlockTagGenerator.GUARDED_BY_PIGLINS).addOptionalTag(GUARDED_BY_PIGLINS);
        getOrCreateTagBuilder(GildingBlockTagGenerator.OCCLUDES_VIBRATION_SIGNALS).addOptionalTag(OCCLUDES_VIBRATION_SIGNALS);
        getOrCreateTagBuilder(GildingBlockTagGenerator.FEATURES_CANNOT_REPLACE).addOptionalTag(FEATURES_CANNOT_REPLACE);
        getOrCreateTagBuilder(GildingBlockTagGenerator.GEODE_INVALID_BLOCKS).addOptionalTag(GEODE_INVALID_BLOCKS);

        getOrCreateTagBuilder(DRAGON_IMMUNE).add(AvengersBlocks.MJOLNIR_BLOCK).add(AvengersBlocks.TELEPORT_ANCHOR);
        getOrCreateTagBuilder(WITHER_IMMUNE).add(AvengersBlocks.MJOLNIR_BLOCK).add(AvengersBlocks.TELEPORT_ANCHOR);
        getOrCreateTagBuilder(PORTALS);
        getOrCreateTagBuilder(SHULKER_BOXES);
        getOrCreateTagBuilder(HOGLIN_REPELLENTS).add(AvengersBlocks.TELEPORT_ANCHOR);
        getOrCreateTagBuilder(GUARDED_BY_PIGLINS);
        getOrCreateTagBuilder(OCCLUDES_VIBRATION_SIGNALS);
        getOrCreateTagBuilder(FEATURES_CANNOT_REPLACE).add(AvengersBlocks.TELEPORT_ANCHOR);
        getOrCreateTagBuilder(GEODE_INVALID_BLOCKS).add(AvengersBlocks.TELEPORT_ANCHOR);

        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(PORTALS).add(block));
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(SHULKER_BOXES).add(block));
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(HOGLIN_REPELLENTS).add(block));
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(GUARDED_BY_PIGLINS).add(block));
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(OCCLUDES_VIBRATION_SIGNALS).add(block));
        StarPortalBlock.getAll().forEach((block) -> getOrCreateTagBuilder(GEODE_INVALID_BLOCKS).add(block));

    }
}
