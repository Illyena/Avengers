package illyena.gilding.avengers.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

@Environment(EnvType.CLIENT)
public class AvengersEntityModelLayers {
    public static final EntityModelLayer CAP_SHIELD_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "cap_shield_model_layer"), "main");

    public static void registerModelLayers() {

        EntityModelLayerRegistry.registerModelLayer(CAP_SHIELD_MODEL_LAYER, CapShieldEntityModel::getTexturedModelData);
/*        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(MOD_ID, "entity/cap_shield"));
        }));
 */

    }

}
