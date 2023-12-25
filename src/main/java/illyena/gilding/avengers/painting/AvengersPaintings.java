package illyena.gilding.avengers.painting;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.*;

@SuppressWarnings("unused")
public class AvengersPaintings {
    public static void callPaintings() { LOGGER.info("Registering paintings for {} mod.", MOD_NAME); }

    private static PaintingMotive registerPainting(String name, PaintingMotive paintingVariant) {
        return Registry.register(Registry.PAINTING_MOTIVE, new Identifier(MOD_ID, name), paintingVariant);
    }

    public static final PaintingMotive CAPTAIN = registerPainting("captain", new PaintingMotive(32,32));

}
