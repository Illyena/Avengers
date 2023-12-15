package illyena.gilding.avengers.advancement;

import net.minecraft.advancement.criterion.Criteria;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersAdvancements {
    public static void registerAdvancements() { LOGGER.info("Registering advancements for {} mod.", MOD_NAME); }

    public static final KilledWithChanneling KILLED_WITH_CHANNELING = Criteria.register(new KilledWithChanneling());
    public static final RicochetAndReturn RICOCHET_AND_RETURN = Criteria.register(new RicochetAndReturn());
    public static final NotWorthy NOT_WORTHY = Criteria.register(new NotWorthy());

}
