package illyena.gilding.birthday.block.blockentity;

import illyena.gilding.birthday.block.BirthdayBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayBlockEntities {
    public static void registerBirthdayBlockEntities() {
        LOGGER.info("Registering Block Entities for " + MOD_NAME + " Mod.");
    }

    public static final BlockEntityType<StarPortalBlockEntity> STAR_PORTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "star_portal_block_entity"),
            FabricBlockEntityTypeBuilder.create(StarPortalBlockEntity::new, BirthdayBlocks.STAR_PORTAL_BLOCK,
                    BirthdayBlocks.WHITE_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.ORANGE_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.MAGENTA_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.LIGHT_BLUE_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.YELLOW_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.LIME_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.PINK_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.GRAY_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.LIGHT_GRAY_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.CYAN_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.PURPLE_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.BLUE_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.BROWN_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.GREEN_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.RED_STAR_PORTAL_BLOCK,
                    BirthdayBlocks.BLACK_STAR_PORTAL_BLOCK).build(null));

    public static final BlockEntityType<TeleportAnchorBlockEntity> TELEPORT_ANCHOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "teleport_anchor_block_entity"),
            FabricBlockEntityTypeBuilder.create(TeleportAnchorBlockEntity::new, BirthdayBlocks.TELEPORT_ANCHOR).build(null));

}
