package illyena.gilding.birthday.painting;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayPaintings {
    public static void callPaintings() {
        LOGGER.info("Registering Paintings for " + MOD_NAME);
    }

    private static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registry.PAINTING_VARIANT, new Identifier(MOD_ID, name), paintingVariant);
    }

    public static final PaintingVariant CAPTAIN = registerPainting("captain", new PaintingVariant(32,32));

}
