package illyena.gilding.avengers.painting;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersPaintings {
    public static void callPaintings() {
        LOGGER.info("Registering Paintings for " + MOD_NAME);
    }

    private static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registry.PAINTING_VARIANT, new Identifier(MOD_ID, name), paintingVariant);
    }

    public static final PaintingVariant CAPTAIN = registerPainting("captain", new PaintingVariant(32,32));

}
