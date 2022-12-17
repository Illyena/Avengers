package illyena.gilding.birthday.block;

import illyena.gilding.birthday.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.birthday.item.custom.BlockItemWithGlint;
import illyena.gilding.core.item.util.GildingItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayBlocks {
    public static void callBirthdayBlocks() {
        LOGGER.info("Registering Blocks for " + MOD_NAME + " Mod.");
    }

    private static Block registerBlock(String name, Block block, Rarity rarity, @Nullable ItemGroup group) {
         return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Block registerBlockWithItem(String name, Block block, Rarity rarity, boolean hasGlint, @Nullable ItemGroup group) {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                hasGlint ? new BlockItemWithGlint(block, new FabricItemSettings().rarity(rarity).group(group)) : new BlockItem(block, new FabricItemSettings().rarity(rarity).group(group)));
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    //BLOCKS

    public static final Block STAR_PORTAL_BLOCK = registerBlockWithItem("star_portal_block",
            new StarPortalBlock( FabricBlockSettings.of(Material.SHULKER_BOX, MapColor.BLACK).dynamicBounds().nonOpaque().requiresTool().strength(3.0f, 9.0f).luminance((state) -> { return  15; })
                    .suffocates(((state, world, pos) -> {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (!(blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity)) {
                    return true;
                } else {
                    return starPortalBlockEntity.suffocates();
                }
            }))),
            Rarity.EPIC, false,null);

    public static final Block TELEPORT_ANCHOR = registerBlockWithItem("teleport_anchor",
            new TeleportAnchorBlock(FabricBlockSettings.of(Material.AIR, MapColor.CLEAR).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f)),
            Rarity.EPIC, true, null);

}
