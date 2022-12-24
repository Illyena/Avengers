package illyena.gilding.birthday.block;

import illyena.gilding.birthday.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.birthday.item.custom.BlockItemWithGlint;
import illyena.gilding.birthday.util.data.BirthdayLootTableProvider;
import illyena.gilding.birthday.util.data.BirthdayModelProvider;
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

import static illyena.gilding.birthday.BirthdayInitializer.*;
import static illyena.gilding.birthday.util.data.BirthdayLootTableProvider.LootTableTypes.DROPS_NOTHING;
import static illyena.gilding.birthday.util.data.BirthdayLootTableProvider.LootTableTypes.STAR_PORTAL;

public class BirthdayBlocks {
    public static void callBirthdayBlocks() {
        LOGGER.info("Registering Blocks for " + MOD_NAME + " Mod.");
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, Rarity rarity, @Nullable ItemGroup group) {
         return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Block registerBlockWithItem(String name, Block block, Rarity rarity, boolean hasGlint, BirthdayLootTableProvider.LootTableTypes lootType, @Nullable ItemGroup group) {
        BirthdayModelProvider.addModels(block);
        BirthdayLootTableProvider.addLootTable(block, lootType);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                hasGlint ? new BlockItemWithGlint(block, new FabricItemSettings().rarity(rarity).group(group)) : new BlockItem(block, new FabricItemSettings().rarity(rarity).group(group)));
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static StarPortalBlock registerStarPortalBlock(DyeColor color) {

        return new StarPortalBlock(color, FabricBlockSettings.of(Material.SHULKER_BOX, MapColor.BLACK).dynamicBounds().nonOpaque().requiresTool().strength(30.0f, 9.0f).luminance((state) -> { return 15; })
                .suffocates((((state, world, pos) -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (!(blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity)) {
                        return true;
                    } else {
                        return starPortalBlockEntity.suffocates();
                    }
                }))));
    }

    //BLOCKS

    public static final Block STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block", registerStarPortalBlock(null), Rarity.EPIC, false, STAR_PORTAL, null);

    public static final Block WHITE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_white", registerStarPortalBlock(DyeColor.WHITE), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block ORANGE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_orange", registerStarPortalBlock(DyeColor.ORANGE), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block MAGENTA_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_magenta", registerStarPortalBlock(DyeColor.MAGENTA), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block LIGHT_BLUE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_light_blue", registerStarPortalBlock(DyeColor.LIGHT_BLUE), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block YELLOW_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_yellow", registerStarPortalBlock(DyeColor.YELLOW), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block LIME_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_lime", registerStarPortalBlock(DyeColor.LIME), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block PINK_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_pink", registerStarPortalBlock(DyeColor.PINK), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block GRAY_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_gray", registerStarPortalBlock(DyeColor.GRAY), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block LIGHT_GRAY_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_light_gray", registerStarPortalBlock(DyeColor.LIGHT_GRAY), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block CYAN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_cyan", registerStarPortalBlock(DyeColor.CYAN), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block PURPLE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_purple", registerStarPortalBlock(DyeColor.PURPLE), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block BLUE_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_blue", registerStarPortalBlock(DyeColor.BLUE), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block BROWN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_brown", registerStarPortalBlock(DyeColor.BROWN), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block GREEN_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_green", registerStarPortalBlock(DyeColor.GREEN), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block RED_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_red", registerStarPortalBlock(DyeColor.RED), Rarity.EPIC, false, STAR_PORTAL, null);
    public static final Block BLACK_STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block_black", registerStarPortalBlock(DyeColor.BLACK), Rarity.EPIC, false, STAR_PORTAL, null);

    public static final Block TELEPORT_ANCHOR = registerBlockWithItem("teleport_anchor",
            new TeleportAnchorBlock(FabricBlockSettings.of(Material.AIR, MapColor.CLEAR).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f)),
            Rarity.EPIC, true, DROPS_NOTHING, null);

}