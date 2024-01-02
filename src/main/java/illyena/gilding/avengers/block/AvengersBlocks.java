package illyena.gilding.avengers.block;

import illyena.gilding.avengers.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.avengers.util.data.AvengersLootTableProvider;
import illyena.gilding.avengers.util.data.AvengersModelProvider;
import illyena.gilding.core.item.BlockItemWithGlint;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import static illyena.gilding.avengers.AvengersInit.*;
import static illyena.gilding.avengers.util.data.AvengersLootTableProvider.LootTableTypes.BLOCK_ENTITY;
import static illyena.gilding.avengers.util.data.AvengersLootTableProvider.LootTableTypes.DROPS_NOTHING;

@SuppressWarnings("unused")
public class AvengersBlocks {

    public static void registerBlocks() { LOGGER.info("Registering blocks for {} mod.", MOD_NAME); }

    private static Block registerBlockWithoutItem(String name, Block block, AvengersLootTableProvider.LootTableTypes lootType) {
        AvengersModelProvider.addModels(block);
        AvengersLootTableProvider.addLootTable(block, lootType);
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Block registerBlockWithItem(String name, Block block, Rarity rarity, boolean hasGlint, AvengersLootTableProvider.LootTableTypes lootType, @Nullable ItemGroup group) {
        AvengersModelProvider.addModels(block);
        AvengersLootTableProvider.addLootTable(block, lootType);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                hasGlint ? new BlockItemWithGlint(block, new FabricItemSettings().rarity(rarity).group(group)) : new BlockItem(block, new FabricItemSettings().rarity(rarity).group(group)));
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static StarPortalBlock createStarPortalBlock(DyeColor color) {
        return new StarPortalBlock(color, FabricBlockSettings.of(Material.SHULKER_BOX, MapColor.BLACK).dynamicBounds().nonOpaque().requiresTool().strength(30.0f, 9.0f).luminance(15)
                .suffocates((state, world, pos) -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (!(blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity)) {
                        return true;
                    } else {
                        return starPortalBlockEntity.suffocates();
                    }
                }));
    }

    //BLOCKS
    public static final Block MJOLNIR_BLOCK = registerBlockWithoutItem("mjolnir_block",
            new MjolnirBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f, 9.0f).nonOpaque().requiresTool()), BLOCK_ENTITY);

    public static final Block STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal", createStarPortalBlock(null), Rarity.EPIC, false, BLOCK_ENTITY, null);

    public static final Block WHITE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_white", createStarPortalBlock(DyeColor.WHITE), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block ORANGE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_orange", createStarPortalBlock(DyeColor.ORANGE), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block MAGENTA_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_magenta", createStarPortalBlock(DyeColor.MAGENTA), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block LIGHT_BLUE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_light_blue", createStarPortalBlock(DyeColor.LIGHT_BLUE), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block YELLOW_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_yellow", createStarPortalBlock(DyeColor.YELLOW), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block LIME_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_lime", createStarPortalBlock(DyeColor.LIME), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block PINK_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_pink", createStarPortalBlock(DyeColor.PINK), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block GRAY_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_gray", createStarPortalBlock(DyeColor.GRAY), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block LIGHT_GRAY_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_light_gray", createStarPortalBlock(DyeColor.LIGHT_GRAY), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block CYAN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_cyan", createStarPortalBlock(DyeColor.CYAN), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block PURPLE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_purple", createStarPortalBlock(DyeColor.PURPLE), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block BLUE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_blue", createStarPortalBlock(DyeColor.BLUE), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block BROWN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_brown", createStarPortalBlock(DyeColor.BROWN), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block GREEN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_green", createStarPortalBlock(DyeColor.GREEN), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block RED_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_red", createStarPortalBlock(DyeColor.RED), Rarity.EPIC, false, BLOCK_ENTITY, null);
    public static final Block BLACK_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_black", createStarPortalBlock(DyeColor.BLACK), Rarity.EPIC, false, BLOCK_ENTITY, null);

    public static final Block TELEPORT_ANCHOR = registerBlockWithItem("teleport_anchor",
            new TeleportAnchorBlock(FabricBlockSettings.of(Material.AIR, MapColor.CLEAR).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f)),
            Rarity.EPIC, true, DROPS_NOTHING, null);

}
