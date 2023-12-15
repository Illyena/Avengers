package illyena.gilding.avengers.block.blockentity;

import illyena.gilding.avengers.block.AvengersBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersBlockEntities {
    public static void registerBlockEntities() { LOGGER.info("Registering block entities for {} mod.", MOD_NAME); }

    public static final BlockEntityType<MjolnirBlockEntity> MJOLNIR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "mjolnir_block_entity"),
            FabricBlockEntityTypeBuilder.create(MjolnirBlockEntity::new, AvengersBlocks.MJOLNIR_BLOCK).build(null));

    public static final BlockEntityType<StarPortalBlockEntity> STAR_PORTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "star_portal_block_entity"),
            FabricBlockEntityTypeBuilder.create(StarPortalBlockEntity::new, AvengersBlocks.STAR_PORTAL_BLOCK,
                    AvengersBlocks.WHITE_STAR_PORTAL_BLOCK,
                    AvengersBlocks.ORANGE_STAR_PORTAL_BLOCK,
                    AvengersBlocks.MAGENTA_STAR_PORTAL_BLOCK,
                    AvengersBlocks.LIGHT_BLUE_STAR_PORTAL_BLOCK,
                    AvengersBlocks.YELLOW_STAR_PORTAL_BLOCK,
                    AvengersBlocks.LIME_STAR_PORTAL_BLOCK,
                    AvengersBlocks.PINK_STAR_PORTAL_BLOCK,
                    AvengersBlocks.GRAY_STAR_PORTAL_BLOCK,
                    AvengersBlocks.LIGHT_GRAY_STAR_PORTAL_BLOCK,
                    AvengersBlocks.CYAN_STAR_PORTAL_BLOCK,
                    AvengersBlocks.PURPLE_STAR_PORTAL_BLOCK,
                    AvengersBlocks.BLUE_STAR_PORTAL_BLOCK,
                    AvengersBlocks.BROWN_STAR_PORTAL_BLOCK,
                    AvengersBlocks.GREEN_STAR_PORTAL_BLOCK,
                    AvengersBlocks.RED_STAR_PORTAL_BLOCK,
                    AvengersBlocks.BLACK_STAR_PORTAL_BLOCK).build(null));

    public static final BlockEntityType<TeleportAnchorBlockEntity> TELEPORT_ANCHOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "teleport_anchor_block_entity"),
            FabricBlockEntityTypeBuilder.create(TeleportAnchorBlockEntity::new, AvengersBlocks.TELEPORT_ANCHOR).build(null));

}
