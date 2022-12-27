package illyena.gilding.birthday.painting;

import  net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayPaintings {
    public static void callPaintings() {
        LOGGER.info("Registering Paintings for " + MOD_NAME);
    }

    private static PaintingMotive registerPainting(String name, PaintingMotive paintingVariant) {
        return Registry.register(Registry.PAINTING_MOTIVE, new Identifier(MOD_ID, name), paintingVariant);
    }

    public static final PaintingMotive CAPTAIN = registerPainting("captain", new PaintingMotive(32,32));

}
