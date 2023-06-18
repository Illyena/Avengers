package illyena.gilding.avengers.painting;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersPaintings {
    public static void callPaintings() {
        LOGGER.info("Registering Paintings for " + MOD_NAME);
    }

    private static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registries.PAINTING_VARIANT, new Identifier(MOD_ID, name), paintingVariant);
    }

    public static final PaintingVariant CAPTAIN = registerPainting("captain", new PaintingVariant(32,32));

}
